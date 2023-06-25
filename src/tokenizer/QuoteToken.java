package tokenizer;

public class QuoteToken extends Token {
	private static final String[] ALLOWED_TYPES = {"UnformattedTextToken", "BoldToken", "ItalicToken", "UnderlineToken"};
		
	public QuoteToken(Token... tokens) throws TokenFormatException {
		super(tokens, QuoteToken.ALLOWED_TYPES);
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
