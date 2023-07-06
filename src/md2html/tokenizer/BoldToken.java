package md2html.tokenizer;

public class BoldToken extends Token {
	public BoldToken(Token... tokens) {
		super(tokens);
	}
	
	@Override
	public String getSymbol(boolean closed) {
		return closed ? "</b>" : "<b>";
	}
}
