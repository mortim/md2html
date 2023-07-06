package md2html.tokenizer;

public class UnformattedTextToken extends Token {
	private String msg;
	
	public UnformattedTextToken(String msg) {
		super();
		this.msg = msg;
	}
	
	public String getMsg() {
		return this.msg;
	}
	
	@Override
	public String getSymbol(boolean closed) { return ""; }
	
	@Override
	public String toHTML() {
		return this.msg;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(\"" + this.msg + "\"), ";
	}

	
}
