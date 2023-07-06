package md2html.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import md2html.parser.Parser;
import md2html.parser.RenderType;

public abstract class Token {
	protected Token[] tokens;
	
	// For unformatted text, newline, footnote
	public Token() {
		this.tokens = new Token[]{};
	}
	
	// For title
	public Token(UnformattedTextToken token) {
		this.tokens = new Token[]{token};
	}
	
	public Token(Token[] tokens) {
		this.tokens = tokens;
	}
	
	public Token[] getTokens() {
		return this.tokens;
	}
	
	public void addToken(Token... t) {
		List<Token> tokensL = new ArrayList<>(Arrays.asList(this.tokens));
		tokensL.addAll(Arrays.asList(t));
		this.tokens = ((Token[])tokensL.toArray(new Token[tokensL.size()]));
	}
	
	// For md2html.parser.TokenStack
	public void addToken(Stack<Token> stack) {
		this.addToken((Token[])stack.toArray(new Token[stack.size()]));
	}
	
	// Default implementation, can be overrided
	public String toHTML() {
		return this.getSymbol(false) + Parser.renderTo(RenderType.HTML, tokens) + this.getSymbol(true);
	}
	
	// Default implementation, can be overrided
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + Parser.renderTo(RenderType.AST, this.tokens) + ")";
	}

	public abstract String getSymbol(boolean closed);
	
}
