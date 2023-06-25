package tokenizer;

public class TokenFormatException extends Exception {

	public TokenFormatException(int line, String msg) {
		super("At L" + line + " : " + msg);
	}
	
	public TokenFormatException(String type) {
		super("A " + type + " has incorrect format.");
	}

}
