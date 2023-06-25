package tokenizer;

public class ItalicToken extends Token {
	private static final String[] ALLOWED_TYPES = {"UnformattedTextToken", "BoldToken", "UnderlineToken"};
	
	public ItalicToken(Token... tokens) throws TokenFormatException {
		super(tokens, ItalicToken.ALLOWED_TYPES);
	}
	
	public String getSymbol() {
		return "i";
	}
}
