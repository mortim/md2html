package md2html.tokenizer;

import md2html.parser.Parser;
import md2html.parser.RenderType;

public class ListItemToken extends Token {
	private String id;
	
	public ListItemToken(String id, Token... tokens) {
		super(tokens);
		this.id = id;
	}
	
	public ListItemToken(Token... tokens) {
		this(null, tokens);
	}
	
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getSymbol(boolean closed) {
		return closed ? "</li>" : "<li id=\"" + (this.id != null ? this.id : "#") + "\">";
	}
	
	@Override
	public String toHTML() {
		return this.getSymbol(false) + Parser.renderTo(RenderType.HTML, tokens) + this.getSymbol(true);
	}
}