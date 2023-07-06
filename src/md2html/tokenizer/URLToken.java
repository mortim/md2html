package md2html.tokenizer;

import md2html.parser.Parser;
import md2html.parser.RenderType;
import md2html.tokenizer.enums.LinkType;

public class URLToken extends Token {
	private LinkType type;
	private String url;
	
	public URLToken(LinkType type, Token desc, String url) {
		super(new Token[]{desc});
		this.type = type;
		this.url = url;
	}

	@Override
	public String getSymbol(boolean closed) {
		String[] tag = this.type == LinkType.HYPERLINK ? new String[]{"<a href=\"" + this.url + "\">", "</a>"} : new String[]{"<img src=\"" + this.url + "\">", "</img>"};
		return closed ? tag[1] : tag[0];
	}
	
	@Override
	public String toHTML() {
		return this.getSymbol(false) + Parser.renderTo(RenderType.HTML, this.tokens) + this.getSymbol(true);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(type=" + this.type + ", desc=" + Parser.renderTo(RenderType.AST, this.tokens) + ", url=" + this.url + ")";
	}
}
