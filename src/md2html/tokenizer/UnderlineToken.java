package md2html.tokenizer;

public class UnderlineToken extends Token {
	public UnderlineToken(Token... tokens) {
		super(tokens);
	}
	
	@Override
	public String getSymbol(boolean closed) {
		return closed ? "</u>" : "<u>";
	}
}
