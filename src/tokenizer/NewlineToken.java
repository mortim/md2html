package tokenizer;

public class NewlineToken extends Token {

	public String getSymbol() {
		return "br";
	}
	
	@Override
	public String toHTML() {
		return "</ br>";
	}
}
