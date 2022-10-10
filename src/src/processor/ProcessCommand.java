package src.processor;

import java.util.Arrays;
import java.util.HashMap;

import src.input.Command;
import src.input.RequestType;

public class ProcessCommand {

	public static Command processGET(String commandString) {

		Command commandObj = new Command();
		commandObj.setType(RequestType.GET);

		if (commandString.contains(" -o ")) {
			String outputFileName = commandString.substring(commandString.indexOf("-o"), commandString.length());
			outputFileName = outputFileName.replace("-o", "").trim();
			commandObj.setOutputFileName(outputFileName);
			System.out.println("outputFileName: " + outputFileName);
			commandString = commandString.substring(0, commandString.indexOf("-o"));
		}

		String url = getUrl(commandString);

		// clean commandString string
		commandString = commandString.replace(url, "");
		url = url.replaceAll("'", "");
		commandObj.setUrl(url);

		commandString = commandString.replace("httpc " + commandObj.getType().name().toLowerCase(), "");

		// check verbose option
		boolean isVerbose = commandString.contains(" -v ");
		commandString = isVerbose ? commandString.replace("-v", "") : commandString;
		commandObj.setVerbose(isVerbose);

		if (commandString.contains(" -h ")) { // extract headers
			int headerEndIndex = commandString.length();
			String headerPart = commandString.substring(commandString.indexOf("-h"), headerEndIndex - 1);
			String[] headers = headerPart.substring(3).split("-h");
			HashMap<String, String> headerMap = createHeaderMap(headers);
			commandObj.setHeaders(headerMap);

		}
		return commandObj;
	}

	public static Command processPOST(String command) {

		Command commandObj = new Command();
		commandObj.setType(RequestType.POST);

		if (command.contains(" -o ")) {
			String outputFileName = command.substring(command.indexOf("-o"), command.length());
			outputFileName = outputFileName.replace("-o", "").trim();
			commandObj.setOutputFileName(outputFileName);
			System.out.println("outputFileName: " + outputFileName);
			command = command.substring(0, command.indexOf("-o"));
		}

		String url = getUrl(command);
		commandObj.setUrl(url);

		// clean command string
		command = command.replace(url, "");
		command = command.replace("httpc " + commandObj.getType().name().toLowerCase(), "");

		// check verbose option
		boolean isVerbose = command.contains(" -v ");
		command = isVerbose ? command.replace("-v", "") : command;
		commandObj.setVerbose(isVerbose);

		if (command.contains(" -h ")) { // extract headers
			int headerEndIndex = command.indexOf("-d") == -1 ? command.indexOf("-f") : command.indexOf("-d");
			headerEndIndex = headerEndIndex == -1 ? command.length() : headerEndIndex;
			String headerPart = command.substring(command.indexOf("-h"), headerEndIndex - 1);
			String[] headers = headerPart.substring(3).split("-h");

			HashMap<String, String> headerMap = createHeaderMap(headers);
			commandObj.setHeaders(headerMap);
		}

		if (command.contains(" -d ")) {
			// extract data
			String dataPart = command.substring(command.indexOf("-d"), command.length());
			dataPart = dataPart.replace("-d", "").trim();
			dataPart = dataPart.replaceAll("'", "");
			commandObj.setInlineData(dataPart);

		}
		if (command.contains(" -f ")) { // extract file
			String filePath = command.substring(command.indexOf("-f"), command.length());
			filePath = filePath.replaceAll("-f", "").trim();
			commandObj.setFilePath(filePath);
		}

		return commandObj;
	}

	private static HashMap<String, String> createHeaderMap(String[] headerPart) {

		System.out.println(Arrays.toString(headerPart));
		HashMap<String, String> headerMap = new HashMap<String, String>();
		for (int i=0; i< headerPart.length; i++) {
			String[] headerTokens = headerPart[i].split(":");
			headerMap.put(headerTokens[0], headerTokens[1]);
		}

		return headerMap;
	}

	private static String getUrl(String command) {
		String[] spaceTokens = command.split(" ");
		String url = spaceTokens[spaceTokens.length - 1];
		return url;
	}

}
