package md2html.tokenizer;

import md2html.parser.Parser;
import md2html.parser.RenderType;

public class TitleToken extends Token {
	private int size;
	
	public TitleToken(UnformattedTextToken token, int size) {
		super(token);
		this.size = size;
	}
	
	@Override
	public String getSymbol(boolean closed) {
		return closed ? "</h" + this.size + ">" : "<h" + this.size + ">";
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(size=" + this.size + ", " + Parser.renderTo(RenderType.AST, this.tokens) + ")";
	}
	
}
