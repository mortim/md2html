package tokenizer;

public class UnderlineToken extends Token {
	public UnderlineToken(Token... tokens) {
		super(tokens);
	}
	
	public String getSymbol() {
		return "u";
	}
}
