package md2html.tokenizer;

public class ItalicToken extends Token {
	public ItalicToken(Token... tokens) {
		super(tokens);
	}
	
	@Override
	public String getSymbol(boolean closed) {
		return closed ? "</i>" : "<i>";
	}
}
