package md2html.tokenizer;

public class TitleToken extends Token {
	private int size;
	
	public TitleToken(UnformattedTextToken token, int size) {
		super(token);
		this.size = size;
	}
	
	public String getSymbol() {
		if(this.size == 1)
			return "h1";
		else if(this.size == 2)
			return "h2";
		else
			return "h3";
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(size=" + this.size + ", " + Utils.renderTo(RenderType.AST, this.tokens) + ")";
	}
	
}
