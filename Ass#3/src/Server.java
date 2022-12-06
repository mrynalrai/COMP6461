import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * This class is the entry point of Server for FTP client Implementation.
 * 
 */
public class Server {

	static final String SERVER = "Server: httpfs/1.0.0";
	static final String DATE = "Date: ";
	static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin: *";
	static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials: true";
	static final String VIA = "Via : 1.1 vegur";
	static boolean debug = false;
	static final String OK_STATUS_CODE = "HTTP/1.1 200 OK";
	static final String FILE_NOT_FOUND_STATUS_CODE = "HTTP/1.1 404 FILE NOT FOUND";
	static final String FILE_OVERWRITTEN_STATUS_CODE = "HTTP/1.1 201 FILE OVER-WRITTEN";
	static String dir = System.getProperty("user.dir");
	static final String FILE_NOT_OVERWRITTEN_STATUS_CODE = "HTTP/1.1 201 FILE NOT OVER-WRITTEN";
	static final String NEW_FILE_CREATED_STATUS_CODE = "HTTP/1.1 202 NEW FILE CREATED";
	static final String CONNECTION_ALIVE = "Connection: keep-alive";

	static File currentDir;
	static int timeout = 3000;
	static int port = 8080;
	List<String> clientRequestList;

	/**
	 * This method is the entry point requesting client to connect to server based
	 * on client type
	 * 
	 */
	public static void main(String[] args) throws Exception {
		String request;
		List<String> serverRequestList = new ArrayList<>();

		System.out.print("Please enter command--->");
		Scanner sc = new Scanner(System.in);
		request = sc.nextLine();
		if (request.isEmpty() || request.length() == 0) {
			System.out.println("Invalid Command Please enter valid command");
		}
		String[] serverRequestArray = request.split(" ");
		serverRequestList = new ArrayList<>();
		for (int i = 0; i < serverRequestArray.length; i++) {
			serverRequestList.add(serverRequestArray[i]);
		}

		if (serverRequestList.contains("-v")) {
			debug = true;
		}

		if (serverRequestList.contains("-p")) {
			String portStr = serverRequestList.get(serverRequestList.indexOf("-p") + 1).trim();
			port = Integer.valueOf(portStr);
		}

		if (serverRequestList.contains("-d")) {
			dir = serverRequestList.get(serverRequestList.indexOf("-d") + 1).trim();
		}

		if (debug)
			System.out.println("Server is up and it assign to port Number: " + port);

		currentDir = new File(dir);

		Server server = new Server();
		Runnable task = () -> {
			try {
				server.serveRequestToServer(port);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Thread thread = new Thread(task);
		thread.start();
	}

	/**
	 * This method will extract payload from client
	 * 
	 */
	private void serveRequestToServer(int port) throws Exception {
		try (DatagramChannel channel = DatagramChannel.open()) {
			channel.bind(new InetSocketAddress(port));

			ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN).order(ByteOrder.BIG_ENDIAN);

			for (;;) {
				buf.clear();
				SocketAddress router = channel.receive(buf);
				if (router != null) {
					// Parse a packet from the received raw data.
					buf.flip();
					Packet packet = Packet.fromBuffer(buf);
					buf.flip();

					String requestPayload = new String(packet.getPayload(), UTF_8);
					// Send the response to the router not the client.
					// The peer address of the packet is the address of the client already.
					// We can use toBuilder to copy properties of the current packet.
					// This demonstrate how to create a new packet from an existing packet.

					if (requestPayload.equals("Hi from Client")) {
						System.out.println(requestPayload);
						Packet resp = packet.toBuilder().setPayload("Hi from Server".getBytes()).create();
						channel.send(resp.toBuffer(), router);
						System.out.println("Sending Hi from Server");
					} else if (requestPayload.contains("httpfs") || requestPayload.contains("httpc")) {
						String responsePayload = processPayload(requestPayload);

						Packet resp = packet.toBuilder().setPayload(responsePayload.getBytes()).create();
						channel.send(resp.toBuffer(), router);

					} else if (requestPayload.equals("Received")) {
						System.out.println(requestPayload);
						Packet respClose = packet.toBuilder().setPayload("Close".getBytes()).create();
						channel.send(respClose.toBuffer(), router);

					} else if (requestPayload.equals("Ok")) {

						System.out.println(requestPayload + " received..!");

					}
				}
			}
		}

	}

	/**
	 * This method id the entry point requesting client to connect to server based
	 * on client type
	 */
	private String processPayload(String requestPayload) throws Exception {

		String method ="";
		
		String[] clientRequestArray = requestPayload.split(" ");
		clientRequestList = new ArrayList<>();
		for (int i = 0; i < clientRequestArray.length; i++) {
			clientRequestList.add(clientRequestArray[i]);

			if (clientRequestArray[i].startsWith("http://")) {
				String[] methodarray = clientRequestArray[i].split("/");
				if (methodarray.length == 4) {

					method = methodarray[3] + "/";
				} else if (methodarray.length == 5) {

					method = methodarray[3] + "/" + methodarray[4];

				}
			}

		}

		String url;
		String fileData = "";
		String downloadFileName = "";

		if (requestPayload.contains("post")) {
			url = clientRequestList.get(1);
		} else {
			url = clientRequestList.get(clientRequestList.size() - 1);
		}
		String host = new URL(url).getHost();
		//method = clientRequestList.get(1);
		String responseHeaders = getResponseHeaders(OK_STATUS_CODE);

		if (debug)

			System.out.println(" Server is Processing the httpfs request");

		String body = "{\n";
		body = body + "\t\"args\":";
		body = body + "{},\n";
		body = body + "\t\"headers\": {";

		if (!method.endsWith("/") && method.contains("get/")
				&& requestPayload.contains("Content-Disposition:attachment")) {
			body = body + "\n\t\t\"Content-Disposition\": \"attachment\",";
		} else if (!method.endsWith("/") && method.contains("get/")
				&& requestPayload.contains("Content-Disposition:inline")) {
			body = body + "\n\t\t\"Content-Disposition\": \"inline\",";
		}
		body = body + "\n\t\t\"Connection\": \"close\",\n";
		body = body + "\t\t\"Host\": \"" + host + "\"\n";
		body = body + "\t},\n";

		if (method.equalsIgnoreCase("get/")) {

			body = body + "\t\"files\": { ";
			List<String> files = getFilesFromDir(currentDir);
			List<String> fileFilterList = new ArrayList<String>();
			fileFilterList.addAll(files);

			if (requestPayload.contains("Content-Type")) {

				String fileType = clientRequestList.get(clientRequestList.indexOf("-h") + 1).split(":")[1];
				fileFilterList = new ArrayList<String>();
				for (String file : files) {
					if (file.endsWith(fileType)) {
						fileFilterList.add(file);
					}
				}
			}

			if (!fileFilterList.isEmpty()) {
				for (int i = 0; i < fileFilterList.size(); i++) {

					if (i != fileFilterList.size() - 1) {
						body = body + fileFilterList.get(i) + " , ";
					} else {
						body = body + fileFilterList.get(i) + " },\n";
					}

				}
			} else {
				body = body + " },\n";
			}

		}

		// if the request is 'GET /fileName'
		else if (!method.endsWith("/") && method.contains("get/")) {

			String requestedFileName = method.split("/")[1];
			List<String> files = getFilesFromDir(currentDir);

			if (requestPayload.contains("Content-Type")) {
				String fileType = clientRequestList.get(clientRequestList.indexOf("-h") + 1).split(":")[1];
				requestedFileName = requestedFileName + "." + fileType;
			}

			if (!files.contains(requestedFileName)) {
				responseHeaders = getResponseHeaders(FILE_NOT_FOUND_STATUS_CODE);
			} else {
				File file = new File(dir + "/" + requestedFileName);
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String st;
				while ((st = bufferedReader.readLine()) != null) {
					fileData = fileData + st;
				}
				if (requestPayload.contains("Content-Disposition:attachment")) {
					downloadFileName = requestedFileName;
				} else {
					body = body + "\t\"data\": \"" + fileData + "\",\n";
				}

			}
		}

		else if (!method.endsWith("/") && method.contains("post/")) {

			String fileName = method.split("/")[1];
			File file = new File(fileName);
			List<String> files = getFilesFromDir(currentDir);
			if (files.contains(fileName)) {
				synchronized (file) {
					file.delete();
					file = new File(dir + "/" + fileName);
					file.createNewFile();
					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(requestPayload.substring(requestPayload.indexOf("-d") + 3));
					fileWriter.close();
				}
				responseHeaders = getResponseHeaders(FILE_OVERWRITTEN_STATUS_CODE);
			}

			else {
				file = new File(dir + "/" + fileName);
				synchronized (file) {
					file.createNewFile();
					FileWriter fileWriter = new FileWriter(file);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					PrintWriter printWriter = new PrintWriter(bufferedWriter);

					printWriter.print(requestPayload.substring(requestPayload.indexOf("-d") + 3));
					printWriter.flush();
					printWriter.close();
				}
				responseHeaders = getResponseHeaders(NEW_FILE_CREATED_STATUS_CODE);
			}
		}
		body = body + "\t\"origin\": \"" + InetAddress.getLocalHost().getHostAddress() + "\",\n";
		body = body + "\t\"url\": \"" + url + "\"\n";
		body = body + "}";

		if (debug)
			System.out.println("Sending response to Client..");
		String responsePayload = responseHeaders + body;
		if (requestPayload.contains("Content-Disposition:attachment")) {
			responsePayload = responsePayload + "|" + downloadFileName + "|" + fileData;
		}
		return responsePayload;
	}

	/**
	 * This method will give list of files from specific directory
	 * 
	 */
	static private List<String> getFilesFromDir(File currentDir) {
		List<String> filelist = new ArrayList<>();
		for (File file : currentDir.listFiles()) {
			if (!file.isDirectory()) {
				filelist.add(file.getName());
			}
		}
		return filelist;
	}

	/**
	 * This method will give responseHeader in format
	 * 
	 */
	static String getResponseHeaders(String status) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String datetime = dateFormat.format(date);
		String responseHeaders = status + "\n" + CONNECTION_ALIVE + "\n" + Server.SERVER + "\n" + Server.DATE + datetime
				+ "\n" + ACCESS_CONTROL_ALLOW_ORIGIN + "\n" + ACCESS_CONTROL_ALLOW_CREDENTIALS + "\n" + VIA + "\n";
		return responseHeaders;
	}
}
