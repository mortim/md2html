package tokenizer;

public class QuoteToken extends Token {
	public QuoteToken(Token... tokens) {
		super(tokens);
	}
	
	@Override
	public String getSymbol() {
		return "blockquote";
	}
	
	@Override
	public String toHTML() {
		return "<" + this.getSymbol() + " style=\"border-left: 4px solid #d6d6d6; padding: 10px;background-color; color: #5c5c5c\">" + Utils.renderTo(RenderType.HTML, this.tokens) + "</" + this.getSymbol() + ">";
	}
	
}
