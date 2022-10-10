package src.processor;

import src.input.Command;

import java.util.Arrays;

public class ProcessInput {
	public Command parseInput(String commandString) {

		if (!commandString.startsWith("httpc")) {
			return null;
		}

		String[] commandArray = commandString.split(" ");
		if (commandArray.length == 0) {
			return null;
		}

		String commandType = commandArray[1];
		switch (commandType) {
		case "help":
			String help = commandArray.length == 3 ? commandArray[2] : "none";
			return printHelpMenu(help);
		case "post":
			if (Arrays.stream(commandArray).anyMatch("-d"::equals) && Arrays.stream(commandArray).anyMatch("-f"::equals)) {
				System.out.println("Invalid command, can't use -d and -f together");
				return null;
			} else
			return ProcessCommand.processPOST(commandString);
		case "get":
			return ProcessCommand.processGET(commandString);
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
