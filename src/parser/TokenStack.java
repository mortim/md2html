package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import tokenizer.Token;

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
		Stack<Token> tmp = new Stack<>();
		for(int i = 0; i < nbElements; i++)
			tmp.insertElementAt(this.tokens.pop(), 0);
		this.tokens.peek().setTokens((Token[])tmp.toArray(new Token[tmp.size()]));
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
