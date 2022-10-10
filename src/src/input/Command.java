package src.input;

import java.util.HashMap;

public class Command {

	RequestType type;
	boolean verboseOption;
	HashMap<String, String> headers = new HashMap<>();
	String inlineData;
	String filePath;
	String url;
	String outputFileName;

	public Command() {

	}

	public Command(RequestType type, boolean verboseOption, HashMap<String, String> headers, String inlineData,
			String filePath, String url) {
		super();
		this.type = type;
		this.verboseOption = verboseOption;
		this.headers = headers;
		this.inlineData = inlineData;
		this.filePath = filePath;
		this.url = url;
	}

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	public boolean isVerbose() {
		return verboseOption;
	}


	public void setVerbose(boolean verboseOption) {
		this.verboseOption = verboseOption;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}

	public String getInlineData() {
		return inlineData;
	}

	public void setInlineData(String inlineData) {
		this.inlineData = inlineData;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Command [type=");
		builder.append(type);
		builder.append(", verboseOption=");
		builder.append(verboseOption);
		builder.append(", headers=");
		builder.append(headers);
		builder.append(", inlineData=");
		builder.append(inlineData);
		builder.append(", filePath=");
		builder.append(filePath);
		builder.append(", url=");
		builder.append(url);
		builder.append(", outputFileName=");
		builder.append(outputFileName);
		builder.append("]");
		return builder.toString();
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

}
