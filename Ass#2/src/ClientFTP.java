
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This class implements the FTP library
 * 
 * 
 */
public class ClientFTP {

	private static ClientRequest clientRequest = new ClientRequest();
	private static List<String> headerLst = null;
	static Socket socket = null;
	static ObjectOutputStream objectOutputStream = null;
	static ObjectInputStream objectInputStream = null;
	static ServerResponse serverResponse;

	/**
	 * This method allows user to provide appropriate command and passes it to server to process it.
	 * 
	 */
	public static void main(String[] args)
			throws UnknownHostException, IOException, EOFException, URISyntaxException, ClassNotFoundException {
		String directory = System.getProperty("user.dir");
		File file = new File("attachment");
		file.mkdir();
		while (true) {
			String request = "";
			System.out.print("Please Enter File transfer command --> ");
			Scanner scanner = new Scanner(System.in);
			request = scanner.nextLine();
			if (request.isEmpty() || request.length() == 0 || (request.contains("post") && !request.contains("-d"))) {
				System.out.println("Invalid Command or Please enter POST url with inline data");
				continue;
			}

			String[] requestArray = request.split(" ");
			requestArray[0] = "httpfs";
			List<String> dataList = Arrays.asList(requestArray);
			String url = "";
			if (request.contains("post")) {
				url = dataList.get(2);

			} else {
				url = dataList.get(dataList.size() - 1);
			}

			if (url.contains("\'")) {
				url = url.replace("\'", "");
			}
			clientRequest.setHttpRequest(request);
			parseInputRequest(dataList);
		
			URI uri = new URI(clientRequest.getRequestUrl());
			String hostName = uri.getHost();
			socket = new Socket(hostName, uri.getPort());
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			System.out.println("Sending request to Socket Server");
			objectOutputStream.writeObject(clientRequest);

			objectOutputStream.writeObject("Test");

			String method = clientRequest.getRequestMethod();

			serverResponse = (ServerResponse) objectInputStream.readObject();

			if (method.equalsIgnoreCase("get/")) {
				
				System.out.println(serverResponse.getResponseHeaders());
				System.out.println(serverResponse.getBody());

			} else if (!method.endsWith("/") && method.contains("get/")) {
				if (request.contains("Content-Disposition:attachment")) {

					String statusCode = serverResponse.getResponseCode();

					if (!statusCode.equals("404")) {

						String fileData = serverResponse.getBody();
						String fileName = serverResponse.getRequestFileName();

						file = new File(directory + "/attachment/" + fileName);
						file.createNewFile();

						FileWriter fileWriter = new FileWriter(file);
						BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
						PrintWriter printWriter = new PrintWriter(bufferedWriter);

						printWriter.print(fileData);
						printWriter.flush();
						printWriter.close();
					}

					System.out.println(serverResponse.getResponseHeaders());
					System.out.println(serverResponse.getBody());

					if (!statusCode.equals("404"))
						System.out.println("File downloaded in " + directory + "/attachment");
				} else {

					System.out.println(serverResponse.getResponseHeaders());
					System.out.println(serverResponse.getBody());
				}
			}
			else if (!method.endsWith("/") && method.contains("post/")) {

				System.out.println(serverResponse.getResponseHeaders());
				System.out.println(serverResponse.getBody());
			}

			objectOutputStream.flush();
			objectOutputStream.close();

		}
	}

	/**
	 * This method will parse the user request URL and set different value in
	 * Request class based on different conditions
	 * 
	 */
	private static void parseInputRequest(List<String> dataList)
			throws URISyntaxException, UnknownHostException, IOException {

		headerLst = new ArrayList<String>();

		// Collecting user request elements
		for (int i = 0; i < dataList.size(); i++) {

			if (dataList.get(i).equals("-v")) {
				clientRequest.setVerbosePreset(true);

			} else if (dataList.get(i).startsWith("http://") || dataList.get(i).startsWith("https://")) {
				clientRequest.setRequestUrl(dataList.get(i));

			} else if (dataList.get(i).equals("-h")) {

				headerLst.add(dataList.get(i + 1));

				clientRequest.setHttpHeader(true);
				clientRequest.setHeaderLst(headerLst);

			} else if (dataList.get(i).equals("-d") || dataList.get(i).equals("--d")) {

				clientRequest.setInlineData(true);
				clientRequest.setInlineData(dataList.get(i + 1));

			} else if (dataList.get(i).equals("-f")) {

				clientRequest.setFilesend(true);
				clientRequest.setFileSendPath(dataList.get(i + 1));

			} else if (dataList.get(i).equals("-o")) {

				clientRequest.setFileWrite(true);
				clientRequest.setFileWritePath(dataList.get(i + 1));

			}
		}
		String str = clientRequest.getHttpRequest();
		
		String[] strArray = str.split("\\s+");
		for(int i=0;i<strArray.length;i++)
		{
			if(strArray[i].startsWith("http://"))
			{
				String[] methodarray = strArray[i].split("/");
				if(methodarray.length==4)
				{
					clientRequest.setClientType(dataList.get(0));
					clientRequest.setRequestMethod(methodarray[3]+"/");
				}
				else if(methodarray.length==5)
				{
					clientRequest.setClientType(dataList.get(0));
					String a = methodarray[3]+"/"+methodarray[4];
					clientRequest.setRequestMethod(a);
				}
			}
		}

		if (clientRequest.isInlineData()) {
			if (clientRequest.getInlineData().contains("\'")) {

				clientRequest.setInlineData(clientRequest.getInlineData().replace("\'", ""));
			}

		}

	}

}
