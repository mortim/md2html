package tokenizer;

public abstract class Token {
	protected Token[] tokens;
	
	// For unformatted text, newline
	public Token() {
		this.tokens = new Token[]{};
	}
	
	// For title
	public Token(UnformattedTextToken token) {
		this.tokens = new Token[]{token};
	}
	
	public Token(Token[] tokens, String[] allowedTypes) throws TokenFormatException {
		if(!Utils.hasCorrectFormat(tokens, allowedTypes))
			throw new TokenFormatException(this.getClass().getSimpleName());
		else
			this.tokens = tokens;
	}
	
	// Default implementation, can be overrided
	public String toHTML() {
		return "<" + this.getSymbol() + ">" + Utils.renderTo(RenderType.HTML, tokens) + "</" + this.getSymbol() + ">";
	}
	
	// Default implementation, can be overrided
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + Utils.renderTo(RenderType.AST, this.tokens) + ")";
	}
	
	public abstract String getSymbol();
	
}
