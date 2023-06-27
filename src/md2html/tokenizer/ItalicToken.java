package md2html.tokenizer;

public class ItalicToken extends Token {
	public ItalicToken(Token... tokens) {
		super(tokens);
	}
	
	public String getSymbol() {
		return "i";
	}
}
