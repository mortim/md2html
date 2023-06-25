package tokenizer;

public class UnformattedTextToken extends Token {
	private String msg;
	
	public UnformattedTextToken(String msg) {
		super();
		this.msg = msg;
	}
	
	public String getSymbol() { return ""; }
	
	@Override
	public String toHTML() {
		return this.msg;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(\"" + this.msg + "\")";
	}

	
}
