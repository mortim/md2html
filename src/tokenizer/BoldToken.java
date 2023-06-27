package tokenizer;

public class BoldToken extends Token {
	public BoldToken(Token... tokens) {
		super(tokens);
	}
	
	public String getSymbol() {
		return "b";
	}
}
