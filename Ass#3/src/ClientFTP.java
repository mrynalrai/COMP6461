import static java.nio.channels.SelectionKey.OP_READ;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 *This class implements the FTP library
 * 
 */
public class ClientFTP {

	static long sequenceNum = 0;
	static List<Long> receivedPackets = new ArrayList<>();
	static int timeout = 3000;
	static int ackCount = 0;

	/**
	 * This method allows user to provide appropriate command and passes it to server to process it.
	 * 
	 */
	public static void main(String[] args) throws Exception {

		// Router address
		String routerHost = "localhost";
		int routerPort = 3000;

		ArrayList<String> requestList = new ArrayList<>();
		File file = new File("attachment");
		file.mkdir();
		while (true) {
			String url = "";
			String request = "";
			System.out.print("Please Enter File transfer command --> ");
			receivedPackets.clear();
			sequenceNum = 0;
			ackCount = 0;
			Scanner sc = new Scanner(System.in);
			request = sc.nextLine();

			if (request.isEmpty() || request.length() == 0) {
				System.out.println("Invalid Command, Please enter Valid Command");
				continue;
			}
			String[] clientRequestArray = request.split(" ");
			requestList = new ArrayList<>();
			for (int i = 0; i < clientRequestArray.length; i++) {
				if(clientRequestArray[i].startsWith("http://")) {
					url = clientRequestArray[i];
				}
				requestList.add(clientRequestArray[i]);
			}
			
//			if (request.contains("post")) {
//				url = requestList.get(2);
//			} else {
//				url = requestList.get(requestList.size() - 1);
//			}
			String serverHost = new URL(url).getHost();
			int serverPort = new URL(url).getPort();

			SocketAddress routerAddress = new InetSocketAddress(routerHost, routerPort);
			InetSocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);

			startConnection(routerAddress, serverAddress);
			runClient(routerAddress, serverAddress, request);

		}
	}

	/**
	 * This method will parse the user request URL and set different value in
	 * Request class based on different conditions
	 * 
	 */
	private static void startConnection(SocketAddress routerAddress, InetSocketAddress serverAddress) throws Exception {

		try (DatagramChannel channel = DatagramChannel.open()) {
			String msg = "Hi from Client";
			sequenceNum++;
			// SYN
			Packet p = new Packet.Builder().setType(0).setSequenceNumber(sequenceNum)
					.setPortNumber(serverAddress.getPort()).setPeerAddress(serverAddress.getAddress())
					.setPayload(msg.getBytes()).create();
			channel.send(p.toBuffer(), routerAddress);
			System.out.println("Sending Hi from Client");

			channel.configureBlocking(false);
			Selector selector = Selector.open();
			channel.register(selector, OP_READ);

			selector.select(timeout);

			Set<SelectionKey> keys = selector.selectedKeys();
			if (keys.isEmpty()) {
				System.out.println("No response after timeout\nSending again");
				resend(channel, p, routerAddress);
			}

			ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
			//buf.flip();
			Packet resp = Packet.fromBuffer(buf);
			String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
			System.out.println(payload + " received..!");
			receivedPackets.add(resp.getSequenceNumber());
			keys.clear();

		}
	}

	/**
	 * This method wait until timeout to resend the data.
	 * 
	 *
	 */
	private static void resend(DatagramChannel channel, Packet p, SocketAddress routerAddress) throws IOException {
		channel.send(p.toBuffer(), routerAddress);
		System.out.println(new String(p.getPayload()));
		if (new String(p.getPayload()).equals("Received")) {
			ackCount++;
		}

		channel.configureBlocking(false);
		Selector selector = Selector.open();
		channel.register(selector, OP_READ);
		selector.select(timeout);

		Set<SelectionKey> keys = selector.selectedKeys();
		if (keys.isEmpty() && ackCount < 10) {

			System.out.println("No response after timeout\nSending again");
			resend(channel, p, routerAddress);

		} else {
			return;
		}
	}

	/**
	 * This method sends UDP request based on the input command by client.
	 * 
	 *
	 */
	private static void runClient(SocketAddress routerAddr, InetSocketAddress serverAddr, String msg)
			throws IOException {
		String dir = System.getProperty("user.dir");
		try (DatagramChannel channel = DatagramChannel.open()) {
			sequenceNum++;
			Packet p = new Packet.Builder().setType(0).setSequenceNumber(sequenceNum)
					.setPortNumber(serverAddr.getPort()).setPeerAddress(serverAddr.getAddress())
					.setPayload(msg.getBytes()).create();
			channel.send(p.toBuffer(), routerAddr);
			System.out.println("sending request to Router...>");

			// Trying to receive packet before timeout.
			channel.configureBlocking(false);
			Selector selector = Selector.open();
			channel.register(selector, OP_READ);
			selector.select(timeout);

			Set<SelectionKey> keys = selector.selectedKeys();
			if (keys.isEmpty()) {
				System.out.println("No response after timeout\nSending again");
				resend(channel, p, routerAddr);
			}

		
			ByteBuffer buffer = ByteBuffer.allocate(Packet.MAX_LEN);
			SocketAddress router = channel.receive(buffer);
			buffer.flip();
			Packet resp = Packet.fromBuffer(buffer);
			String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);

			if (!receivedPackets.contains(resp.getSequenceNumber())) {
				receivedPackets.add(resp.getSequenceNumber());

				if (msg.contains("Content-Disposition:attachment")) {
					String[] responseArray = payload.split("\\|");

					File file = new File(dir + "/attachment/" + responseArray[1].trim());
					file.createNewFile();

					FileWriter fileWriter = new FileWriter(file);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					PrintWriter printWriter = new PrintWriter(bufferedWriter);

					printWriter.print(responseArray[2]);
					printWriter.flush();
					printWriter.close();

					System.out.println(responseArray[0]);
					System.out.println("File downloaded in " + dir + "\\attachment");
				} else {
					System.out.println(payload);
				}

				// Sending ACK for the received of the response
				sequenceNum++;
				Packet pAck = new Packet.Builder().setType(0).setSequenceNumber(sequenceNum)
						.setPortNumber(serverAddr.getPort()).setPeerAddress(serverAddr.getAddress())
						.setPayload("Received".getBytes()).create();
				channel.send(pAck.toBuffer(), routerAddr);

				channel.configureBlocking(false);
				selector = Selector.open();
				channel.register(selector, OP_READ);
				selector.select(timeout);

				keys = selector.selectedKeys();
				if (keys.isEmpty()) {
					resend(channel, pAck, router);
				}

				buffer.flip();

				System.out.println("Connection closed..!");
				keys.clear();

				sequenceNum++;
				Packet packetClose = new Packet.Builder().setType(0).setSequenceNumber(sequenceNum)
						.setPortNumber(serverAddr.getPort()).setPeerAddress(serverAddr.getAddress())
						.setPayload("Ok".getBytes()).create();
				channel.send(packetClose.toBuffer(), routerAddr);
				System.out.println("OK sent");
			}
		}
	}
}
