package src.application;

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
import java.util.Map;
import java.util.Scanner;

import src.input.Command;
import src.input.RequestType;
import src.processor.ProcessInput;

public class MainApplication {
	public static String PROJECT_LOCATION = "D:/Concordia Academics/Fall 2022/COMP 6461/COMP6461/src/src";

	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);

		while(true) {
			String commandString = scanner.nextLine();
			ProcessInput processInput = new ProcessInput();
			Command command = processInput.parseInput(commandString);

			if (command == null) {
				scanner.close();
				return;
			}
			processRequest(command);
		}
	}

	public static void processRequest(Command command) throws IOException {

		URL url = new URL(command.getUrl().replaceAll("'", ""));
		String host = url.getHost();
		int port = 80;
		// Resolve the host name to an IP address
		InetAddress ipAddress = InetAddress.getByName(host);

		// Open socket to a specific host and port
		Socket socket = new Socket(host, port);

		// Get input and output streams for the socket
		OutputStream outStream = socket.getOutputStream();

		String method = url.getPath().contains("get") ? "GET" : "POST";

		// HTTP GET
		if (command.getType().equals(RequestType.GET)) {

			String request = "GET " + url.getPath() + "?" + url.getQuery() + " HTTP/1.0\r\n";

			for (Map.Entry<String,String> ele : command.getHeaders().entrySet()) {
				String key = ele.getKey();
				String value = ele.getValue();
				request += key + " : " + value + "\r\n";
			}
			request += "Host: " + host;
			request += "\r\n" + "Connection: Close\r\n\r\n";

			// Sends off HTTP GET request
			outStream.write(request.getBytes());
			outStream.flush();
		} else if (command.getType().equals(RequestType.POST)) { // HTTP POST
			String data = command.getFilePath() != null ? getFileContent(command.getFilePath())
					: command.getInlineData();
			System.out.println(data);

			//multiple headers support
			String request = "POST " + url.getPath() + " HTTP/1.0\r\n";
			for (Map.Entry<String,String> ele : command.getHeaders().entrySet()) {
				String key = ele.getKey();
				String value = ele.getValue();
				request += key + " : " + value + "\r\n";
			}
			request += "Host: " + host + "\r\n";
			request += "Content-Length: " + data.length() + "\r\n\r\n";
			request += data;

			// Send off HTTP POST request
			outStream.write(request.getBytes());
			outStream.flush();
		} else {
			System.out.println("Invalid HTTP method");
			socket.close();
			return;
		}

		InputStream inputStream = socket.getInputStream();
		boolean writeToFile = false;
		if (command.getOutputFileName() != null) {
			writeToFile = true;
		}

		PrintStream printStream = null;

		if (writeToFile) {
			printStream = new PrintStream(new File(PROJECT_LOCATION + "/output/" + command.getOutputFileName()));

		} else {
			printStream = System.out;
		}
		System.setOut(printStream);

		if (command.isVerbose()) {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			int i;
			while ((i = bufferedReader.read()) != -1) {
				System.out.print((char) i);
			}

		} else {
			StringBuffer response = new StringBuffer();
			byte[] buffer = new byte[4096];
			int bytes_read;

			// Reads HTTP response
			while ((bytes_read = inputStream.read(buffer, 0, 4096)) != -1) {
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
