package md2html.tokenizer;

public class NewlineToken extends Token {
	@Override
	public String getSymbol(boolean closed) {
		return "<br>";
	}
	
	@Override
	public String toHTML() {
		return this.getSymbol(true);
	}
}
