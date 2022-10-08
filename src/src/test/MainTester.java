package src.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import src.input.Command;
import src.input.RequestType;
import src.processor.InputParser;

public class MainTester {

	public static String PROJECT_LOCATION = "D:/Concordia/Fall 2022/CN/Ass#1/comp6461-lab01-12974d2246beb6926e4d993adcc746531b812a20";

	public static void main(String[] args) throws Exception {

		Scanner scanner = new Scanner(System.in);
		String commandStr = scanner.nextLine();

		InputParser commandParser = new InputParser();

		Command command = commandParser.parseInput(commandStr);

		if (command == null) {
			scanner.close();
			return;
		}

		processRequest(command);

		scanner.close();
	}

	public static void processRequest(Command command) throws IOException {

		URL url = new URL(command.getUrl().replaceAll("'", ""));

		String host = url.getHost();
		int port = 80;

		// Resolve the host name to an IP address
		InetAddress ip = InetAddress.getByName(host);

		// Open socket to a specific host and port
		Socket socket = new Socket(host, port);

		// Get input and output streams for the socket
		OutputStream out = socket.getOutputStream();
		String method = url.getPath().contains("get") ? "GET" : "POST";

		// HTTP GET
		if (command.getType().equals(RequestType.GET)) {

			String request = "GET " + url.getPath() + "?" + url.getQuery() + " HTTP/1.0\r\n" + "Accept: */*\r\n"
					+ "Host: " + host

					+ "\r\n" + "Connection: Close\r\n\r\n";

			// Sends off HTTP GET request
			out.write(request.getBytes());
			out.flush();
		} else if (command.getType().equals(RequestType.POST)) { // HTTP POST
			String data = command.getFilePath() != null ? getFileContent(command.getFilePath())
					: command.getInlineData();
			System.out.println(data);

			String request = "POST " + url.getPath() + " HTTP/1.0\r\n" + "Accept: */*\r\n" + "Host: " + host + "\r\n"
					+ "Content-Type: application/x-www-form-urlencoded\r\n" + "Content-Length: " + data.length()
					+ "\r\n\r\n" + data;

			// Send off HTTP POST request
			out.write(request.getBytes());
			out.flush();
		} else {
			System.out.println("Invalid HTTP method");
			socket.close();
			return;
		}

		InputStream in = socket.getInputStream();
		boolean writeToFile = false;
		if (command.getOutputFileName() != null) {
			writeToFile = true;
		}

		PrintStream ps = null;

		if (writeToFile) {
			ps = new PrintStream(new File(PROJECT_LOCATION + "/output/" + command.getOutputFileName()));

		} else {
			ps = System.out;
		}
		System.setOut(ps);

		if (command.isVerboseOption()) {
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			int c;
			while ((c = br.read()) != -1) {
				System.out.print((char) c);
			}

		}

		else {
			StringBuffer response = new StringBuffer();
			byte[] buffer = new byte[4096];
			int bytes_read;

			// Reads HTTP response
			while ((bytes_read = in.read(buffer, 0, 4096)) != -1) {
				// Print server's response
				for (int i = 0; i < bytes_read; i++)
					response.append((char) buffer[i]);
			}

			if (response.substring(response.indexOf(" ") + 1, response.indexOf(" ") + 4).equals("200")) {
				System.out.println(response.substring(response.indexOf("\r\n\r\n") + 4));
			} else
				System.out.println("HTTP request failed");
		}
		// Closes socket
		socket.close();
	}

	private static String getFileContent(String filePath) throws IOException {

		byte[] encoded = Files.readAllBytes(Paths.get(PROJECT_LOCATION + "/" + filePath));
		String data = new String(encoded, "utf-8");
		return data;

	}

}
