package tokenizer;

public class UnderlineToken extends Token {
	private static final String[] ALLOWED_TYPES = {"UnformattedTextToken", "BoldToken", "ItalicToken"};
	
	public UnderlineToken(Token... tokens) throws TokenFormatException {
		super(tokens, UnderlineToken.ALLOWED_TYPES);
	}
	
	public String getSymbol() {
		return "u";
	}
}
