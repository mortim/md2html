package md2html.tokenizer;

import md2html.parser.Parser;
import md2html.parser.RenderType;

public class QuoteToken extends Token {
	public QuoteToken(Token... tokens) {
		super(tokens);
	}
	
	@Override
	public String getSymbol(boolean closed) {
		return closed ? "</blockquote>" : "<blockquote style=\"border-left: 4px solid #d6d6d6; padding: 10px;background-color; color: #5c5c5c\">";
	}

	@Override
	public String toHTML() {
		return this.getSymbol(false) + Parser.renderTo(RenderType.HTML, this.tokens) + this.getSymbol(true);
	}
	
	
}