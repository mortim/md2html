package md2html.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import md2html.tokenizer.Token;

public class TokenStack {
	private Stack<Integer> idx;
	private Stack<Token> tokens;
	public Map<String, Boolean> insideToken;
	
	public TokenStack() {
		this.idx = new Stack<>();
		this.tokens = new Stack<>();
		this.insideToken = new HashMap<>(
			Map.of(
				"BoldToken", false,
				"ItalicToken", false,
				"UnderlineToken", false
			)
		);
	}
	
	public Token[] getTokens() {
		return (Token[])(this.tokens.toArray(new Token[this.tokens.size()]));
	}
	
	public void addToken(Token token) {
		this.tokens.push(token);
	}
	
	public void merge() {
		int nbElements = (this.tokens.size()-1)-this.idx.pop();
		Stack<Token> stack = new Stack<>();
		for(int i = 0; i < nbElements; i++)
			stack.insertElementAt(this.tokens.pop(), 0);
		this.tokens.peek().addToken(stack);
	}
	
	public void handle(Token token) {
		boolean cond = insideToken.get(token.getClass().getSimpleName());
		if(cond)
			this.merge();
		else {
			this.tokens.push(token);
			this.idx.push(this.tokens.size()-1);
		}
		this.insideToken.put(token.getClass().getSimpleName(), !cond);
	}
	
}
