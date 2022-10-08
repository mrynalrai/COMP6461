package src.processor;

import java.util.HashMap;

import src.input.Command;
import src.input.RequestType;

public class CommandParser {

	public static Command processGET(String command) {

		Command commandObj = new Command();
		commandObj.setType(RequestType.GET);

		if (command.contains(" -o ")) {
			String outputFileName = command.substring(command.indexOf("-o"), command.length());
			outputFileName = outputFileName.replace("-o", "").trim();
			commandObj.setOutputFileName(outputFileName);
			System.out.println("outputFileName: " + outputFileName);
			command = command.substring(0, command.indexOf("-o"));
		}

		String url = getUrl(command);

		// clean command string
		command = command.replace(url, "");
		url = url.replaceAll("'", "");
		commandObj.setUrl(url);

		command = command.replace("httpc " + commandObj.getType().name().toLowerCase(), "");

		// check verbose option
		boolean isVerbose = command.contains(" -v ");
		command = isVerbose ? command.replace("-v", "") : command;
		commandObj.setVerboseOption(isVerbose);

		if (command.contains(" -h ")) { // extract headers
			int headerEndIndex = command.length();
			String headerPart = command.substring(command.indexOf("-h"), headerEndIndex - 1);
			headerPart = headerPart.replace("-h", "").trim();

			HashMap<String, String> headerMap = createHeaderMap(headerPart);
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
		commandObj.setVerboseOption(isVerbose);

		if (command.contains(" -h ")) { // extract headers
			int headerEndIndex = command.indexOf("-d") == -1 ? command.indexOf("-f") : command.indexOf("-d");
			headerEndIndex = headerEndIndex == -1 ? command.length() : headerEndIndex;
			String headerPart = command.substring(command.indexOf("-h"), headerEndIndex - 1);
			headerPart = headerPart.replace("-h", "").trim();

			HashMap<String, String> headerMap = createHeaderMap(headerPart);
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

	private static HashMap<String, String> createHeaderMap(String headerPart) {

		HashMap<String, String> headerMap = new HashMap<String, String>();
		String[] headerTokens = headerPart.split(" ");
		for (String token : headerTokens) {
			headerMap.put(token.substring(0, token.indexOf(":")),
					token.substring(token.indexOf(":") + 1, token.length()));
		}

		return headerMap;
	}

	private static String getUrl(String command) {
		String[] spaceTokens = command.split(" ");
		String url = spaceTokens[spaceTokens.length - 1];
		return url;
	}

}
