package tokenizer;

public class ParagraphToken extends Token {
	public ParagraphToken(Token... tokens) {
		super(tokens);
	}

	@Override
	public String getSymbol() {
		return "p";
	}
}
