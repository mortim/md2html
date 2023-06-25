package tokenizer;

public class BoldToken extends Token {
	private static final String[] ALLOWED_TYPES = {"UnformattedTextToken", "ItalicToken", "UnderlineToken"};
	
	public BoldToken(Token... tokens) throws TokenFormatException {
		super(tokens, BoldToken.ALLOWED_TYPES);
	}
	
	public String getSymbol() {
		return "b";
	}
}
