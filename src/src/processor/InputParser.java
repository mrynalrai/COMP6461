package src.processor;

import src.input.Command;

public class InputParser {

	public Command parseInput(String commandStr) {

		if (!commandStr.startsWith("httpc")) {
			return null;
		}

		String[] tokens = commandStr.split(" ");

		if (tokens.length == 0) {
			return null;
		}

		String commandType = tokens[1];
		switch (commandType) {

		case "help":
			String helpType = tokens.length == 3 ? tokens[2] : "none";
			return printHelpMenu(helpType);

		case "post":
			return CommandParser.processPOST(commandStr);
		case "get":
			return CommandParser.processGET(commandStr);

		default:
			System.out.println("Invalid command, use 'httpc help' for valid commands");
		}

		return null;

	}

	private Command printHelpMenu(String type) {

		switch (type) {
		case "none":
			System.out.println(
					"httpc is a curl-like application but supports HTTP protocol only. \nUsage:\n\t httpc command [arguments]");
			System.out.println(
					"The commands are:\n\tget\texecutes a HTTP GET request and prints the response.\n\tpost\texecutes a HTTP POST request and prints the response\n\thelp\tprints this screen");

			System.out.println("Use \"httpc help [command]\" for more information about a command.");

			break;
		case "get":
			System.out.println("usage: httpc get [-v] [-h key:value] URL");
			System.out.println("Get executes a HTTP GET request for a given URL.");
			System.out.println("-v\tPrints the detail of the response such as protocol, status,\n"
					+ "and headers. \n-h key:value\tAssociates headers to HTTP Request with the format\n"
					+ "'key:value'.");
			break;
		case "post":

			System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL");
			System.out
					.println("Post executes a HTTP POST request for a given URL with inline data or from\n" + "file.");
			System.out.println("-v\tPrints the detail of the response such as protocol, status,\n"
					+ "and headers.\n-h key:value\tAssociates headers to HTTP Request with the format\n"
					+ "'key:value'.\n-d string\tAssociates an inline data to the body HTTP POST request.\n-f file \tAssociates the content of a file to the body HTTP POST\n"
					+ "request.");
			System.out.println("Either [-d] or [-f] can be used but not both.");
			break;
		default:
			System.out.println("Invalid option, only get/post allowed.");

		}
		return null;
	}

}
