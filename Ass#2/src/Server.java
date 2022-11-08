
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *This class implements the Server for HTTP and FTP client requests.
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
	static final String FILE_NOT_OVERWRITTEN_STATUS_CODE = "HTTP/1.1 201 FILE NOT OVER-WRITTEN";
	static final String NEW_FILE_CREATED_STATUS_CODE = "HTTP/1.1 202 NEW FILE CREATED";
	static final String CONNECTIONA_LIVE = "Connection: keep-alive";
	
	//Create serversocket to accept requests
	private static ServerSocket serverSocket;
	//Port Value
	private static int port = 8080;

	static ObjectOutputStream objectOutputStream = null;
	static ObjectInputStream objectInputStream = null;

	private static ServerResponse serverResponse;

	private static ClientRequest clientRequest;

	/**
	 * Main Method to specify Port and Start Server to wait for the connections from the clients
	 * 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {

		String request;
		List<String> requestList = new ArrayList<>();

		String directory = System.getProperty("user.dir");

		System.out.println("Server Directory------>>>> " + directory);

		System.out.print(">");
		Scanner scanner = new Scanner(System.in);
		request = scanner.nextLine();
		if (request.isEmpty() || request.length() == 0) {
			System.out.println("Invalid Command Please try again!!");
		}
		String[] requestArray = request.split(" ");
		requestList = new ArrayList<>();
		for (int i = 0; i < requestArray.length; i++) {
			requestList.add(requestArray[i]);
		}

		if (requestList.contains("-v")) {
			debug = true;
		}

		if (requestList.contains("-p")) {
			String portStr = requestList.get(requestList.indexOf("-p") + 1).trim();
			port = Integer.valueOf(portStr);
		}

		if (requestList.contains("-d")) {
			directory = requestList.get(requestList.indexOf("-d") + 1).trim();
			System.out.println("Dir ==>>>>> " + directory);
		}
		serverSocket = new ServerSocket(port);
		if (debug)
			System.out.println("Server is up and it assign to port Number: " + port);

		File currentFolder = new File(directory);

		while (true) {

			serverResponse = new ServerResponse();

			Socket socket = serverSocket.accept();
			if (debug)
				System.out.println("Server is Connected to client ------>>");

			// read data from socket to object stream
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			//convert object stream to string
			clientRequest = (ClientRequest) objectInputStream.readObject();

			String clientType = clientRequest.getClientType();

			String method = clientRequest.getRequestMethod();

			String responseHeaders = getResponseHeaders(OK_STATUS_CODE);

			if (clientType.equalsIgnoreCase("httpc")) {

				String URL = clientRequest.getRequestUrl();
				URI uri = new URI(clientRequest.getRequestUrl());
				String hostName = uri.getHost();

				String query = uri.getQuery();

				System.out.println("clientType ==> " + clientType);

				if (debug)
					System.out.println(" Server is Processing the httpc request");

				String[] paramArr = {};
				if (query != null && !query.isEmpty()) {

					paramArr = query.split("&");
				}
				String inlineData = "";
				String fileData = "";

				if (clientRequest.isVerbosePreset()) {
					serverResponse.setResponseHeaders(responseHeaders);
				}
				String body = "{\n";
				body = body + "\t\"args\":";
				body = body + "{";
				if (paramArr.length > 0) {
					for (int i = 0; i < paramArr.length; i++) {
						body = body + "\n\t    \"" + paramArr[i].substring(0, paramArr[i].indexOf("=")) + "\": \""
								+ paramArr[i].substring(paramArr[i].indexOf("=") + 1) + "\"";
						if (i != paramArr.length - 1) {
							body = body + ",";
						} else {
							body = body + "\n";
							body = body + "\t},\n";
						}
					}
				} else {
					body = body + "},\n";
				}

				//Process post request
				if (method.equalsIgnoreCase("POST")) {
					body = body + "\t\"data\": ";
					if (clientRequest.isInlineData()) {
						inlineData = clientRequest.getInlineData();
						body = body + "\"" + inlineData + "\",\n";
					} else if (clientRequest.isFilesend()) {
						fileData = clientRequest.getFileSendData();
						body = body + "\"" + fileData + "\",\n";
					} else {
						body = body + "\"\",\n";
					}
					body = body + "\t\"files\": {},\n";
					body = body + "\t\"form\": {},\n";
				}

				body = body + "\t\"headers\": {";

				if (clientRequest.isHttpHeader()) {

					for (String header : clientRequest.getHeaderLst()) {
						String[] headerArr = header.split(":");
						if (headerArr[0].equalsIgnoreCase("connection"))
							continue;
						body = body + "\n\t\t\"" + headerArr[0] + "\": \"" + headerArr[1].trim() + "\",";

					}
				}
				if (clientRequest.isInlineData()) {
					body = body + "\n\t\t\"Content-Length\": \"" + clientRequest.getInlineData().length() + "\",";
				} else if (clientRequest.isFilesend()) {
					body = body + "\n\t\t\"Content-Length\": \"" + clientRequest.getFileSendData().length() + "\",";
				}
				body = body + "\n\t\t\"Connection\": \"close\",\n";
				body = body + "\t\t\"Host\": \"" + hostName + "\"\n";
				body = body + "\t},\n";

				if (method.equalsIgnoreCase("POST")) {
					body = body + "\t\"json\": ";
					if (clientRequest.isInlineData()) {
						body = body + "{\n\t\t " + inlineData.substring(1, inlineData.length() - 1) + "\n\t},\n";
					} else {
						body = body + "{\n\t\t " + fileData + "\n\t},\n";
					}
				}
				body = body + "\t\"origin\": \"" + InetAddress.getLocalHost().getHostAddress() + "\",\n";
				body = body + "\t\"url\": \"" + URL + "\"\n";
				body = body + "}";

				serverResponse.setBody(body);
				
				if (debug)
					System.out.println("Sending the response to Client ======>");

			
				objectOutputStream.writeObject(serverResponse);
				objectInputStream.close();
				objectOutputStream.close();
				socket.close();

			} else if (clientType.equalsIgnoreCase("httpfs")) {

				URI uri = new URI(clientRequest.getRequestUrl());
				String hostName = uri.getHost();

				String URL = clientRequest.getHttpRequest();

				if (debug)
					System.out.println("Processing the httpfs request");
				String body = "{\n";
				body = body + "\t\"args\":";
				body = body + "{},\n";
				body = body + "\t\"headers\": {";

				if (!method.endsWith("/") && method.contains("get/")
						&& URL.contains("Content-Disposition:attachment")) {
					body = body + "\n\t\t\"Content-Disposition\": \"attachment\",";
				} else if (!method.endsWith("/") && method.contains("get/")
						&& URL.contains("Content-Disposition:inline")) {
					body = body + "\n\t\t\"Content-Disposition\": \"inline\",";
				}
				body = body + "\n\t\t\"Connection\": \"close\",\n";
				body = body + "\t\t\"Host\": \"" + hostName + "\"\n";
				body = body + "\t},\n";
				
				//Process get request from the clients
				if (method.equalsIgnoreCase("get/")) {

					body = body + "\t\"files\": { ";
					List<String> files = getFilesFromDir(currentFolder);
					List<String> fileFilterList = new ArrayList<String>();
					fileFilterList.addAll(files);
					if (URL.contains("Content-Type")) {
						String fileType = clientRequest.getHeaderLst().get(0).split(":")[1];
						fileFilterList = new ArrayList<String>();
						for (String file : files) {
							if (file.endsWith(fileType)) {
								fileFilterList.add(file);
							}
						}
					}
					for (int i = 0; i < fileFilterList.size(); i++) {

						if (i != fileFilterList.size() - 1) {
							body = body + fileFilterList.get(i) + " , ";
						} else {
							body = body + fileFilterList.get(i) + " },\n";
						}

					}

				}

			//get the file specified by the client
				else if (!method.endsWith("/") && method.contains("get/")) {

					String response = "";
					String requestedFileName = method.split("/")[1];
					List<String> files = getFilesFromDir(currentFolder);

					if (!files.contains(requestedFileName)) {
						responseHeaders = getResponseHeaders(FILE_NOT_FOUND_STATUS_CODE);

					} else {

						File file = new File(directory + "/" + requestedFileName);
						BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
						String st;
						while ((st = bufferedReader.readLine()) != null) {
							response = response + st;
						}
						if (URL.contains("Content-Disposition:attachment")) {
							serverResponse.setResponseCode("203");
							// System.out.println(response);
							// serverResponse.setBody(response);
							body = body + "\t\"data\": \"" + response + "\",\n";
							serverResponse.setRequestFileName(requestedFileName);

						} else {

							serverResponse.setResponseCode("203");
							body = body + "\t\"data\": \"" + response + "\",\n";
						}

					}

				}

				else if (!method.endsWith("/") && method.contains("post/")) {
					String fileName = method.split("/")[1];
					File file = new File(fileName);
					List<String> files = getFilesFromDir(currentFolder);
					if (files.contains(fileName)) {

						if (URL.contains("overwrite")) {
							String overwrite = clientRequest.getHeaderLst().get(0).split(":")[1];
							if (overwrite.equalsIgnoreCase("true")) {
								synchronized (file) {
									file.delete();
									file = new File(directory + "/" + fileName);
									file.createNewFile();
									FileWriter fw = new FileWriter(file);
									fw.write(clientRequest.getInlineData());
									fw.close();
								}
								responseHeaders = getResponseHeaders(FILE_OVERWRITTEN_STATUS_CODE);
							} else {
								responseHeaders = getResponseHeaders(FILE_NOT_OVERWRITTEN_STATUS_CODE);
							}
						} else {
							synchronized (file) {
								file.delete();
								file = new File(directory + "/" + fileName);
								file.createNewFile();
								FileWriter fileWriter = new FileWriter(file);
								fileWriter.write(clientRequest.getInlineData());
								fileWriter.close();
							}
							responseHeaders = getResponseHeaders(FILE_OVERWRITTEN_STATUS_CODE);
						}

					} else {

						file = new File(directory + "/" + fileName);
						synchronized (file) {
							file.createNewFile();
							FileWriter fileWriter = new FileWriter(file);
							BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
							PrintWriter printWriter = new PrintWriter(bufferedWriter);

							printWriter.write(clientRequest.getInlineData());
							printWriter.flush();
							printWriter.close();
						}
						responseHeaders = getResponseHeaders(NEW_FILE_CREATED_STATUS_CODE);

					}

				}

				body = body + "\t\"origin\": \"" + InetAddress.getLocalHost().getHostAddress() + "\",\n";
				body = body + "\t\"url\": \"" + URL + "\"\n";
				body = body + "}";

				if (debug)
					System.out.println("Sending the response to Client ------>");

				// write object to Socket
				serverResponse.setResponseHeaders(responseHeaders);
				serverResponse.setBody(body);
				objectOutputStream.writeObject(serverResponse);
			}

		}
	}
	
	/**
	 * This method gives the list of files from the specified directory
	 * 
	 * @return List of files
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
	 * This method returns responseheader in string format
	 * 
	 */
	static String getResponseHeaders(String status) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String datetime = dateFormat.format(date);
		String responseHeaders = status + "\n" + CONNECTIONA_LIVE + "\n" + Server.SERVER + "\n" + Server.DATE + datetime
				+ "\n" + ACCESS_CONTROL_ALLOW_ORIGIN + "\n" + ACCESS_CONTROL_ALLOW_CREDENTIALS + "\n" + VIA + "\n";
		return responseHeaders;
	}
}
