package md2html.tokenizer;

import md2html.parser.Parser;
import md2html.parser.RenderType;
import md2html.tokenizer.enums.TokenDisplay;

public class CodeToken extends Token {
	private TokenDisplay display;
	
	public CodeToken(TokenDisplay display, UnformattedTextToken token) {
		super(token);
		this.display = display;
	}
	
	@Override
	public String getSymbol(boolean closed) {
		String[] tag = this.display == TokenDisplay.BLOCK ? new String[]{"<pre style=\"background-color: #e9ecef;padding: 10px\"><code>", "</pre></code>"} : new String[]{"<code style=\"background-color: #e9ecef;padding: 3px\">", "</code>"};
		return closed ? tag[1] : tag[0];
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(display=" + this.display + ", " + Parser.renderTo(RenderType.AST, this.tokens) + ")";
	}
}
