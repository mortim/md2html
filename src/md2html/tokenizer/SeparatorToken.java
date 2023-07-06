package md2html.tokenizer;

public class SeparatorToken extends Token {

	@Override
	public String getSymbol(boolean closed) {
		return "<hr style=\"color: lightgrey\">";
	}
	
	@Override
	public String toHTML() {
		return this.getSymbol(true);
	}
}
