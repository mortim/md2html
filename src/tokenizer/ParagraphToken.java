package tokenizer;

public class ParagraphToken extends Token {
	private static final String[] ALLOWED_TYPES = {"UnformattedTextToken", "BoldToken", "ItalicToken", "UnderlineToken"};
	
	public ParagraphToken(Token... tokens) throws TokenFormatException {
		super(tokens, ParagraphToken.ALLOWED_TYPES);
	}

	@Override
	public String getSymbol() {
		return "p";
	}
}
