package md2html.tokenizer;

public class ParagraphToken extends Token {
	public ParagraphToken(Token... tokens) {
		super(tokens);
	}

	@Override
	public String getSymbol(boolean closed) {
		return closed ? "</p>" : "<p>";
	}
}
