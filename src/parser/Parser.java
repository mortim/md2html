package parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import tokenizer.*;

public class Parser {
	private ArrayDeque<String> source;
	private List<Token> ast;
	private ArrayDeque<String> initialSource;
	private static final String[] LINE_DELIMITERS = new String[]{"\n"};
	private static final String[] PARAGRAPH_DELIMITERS = new String[]{"*", "_"};
	
	public Parser(String text) {
		this.source = new ArrayDeque<>(Arrays.asList(text.split("")));
		this.ast = new ArrayList<>();
		this.initialSource = new ArrayDeque<>();
	}
	
	public List<Token> getAST() {
		return this.ast;
	}
	
	@Override
	public String toString() {
		String out = "";
		for(Token token : this.ast)
			out += "=> " + token + "\n";
		return out;
	}
	
	public String toHTML() {
		String out = "";
		for(Token token : this.ast)
			out += token.toHTML();
		return out;	
	}
	
	// Utils
	private void consume(String token) throws ConsumeTokenException {
		if(this.source.peek().equals(token))
			this.source.poll();
		else {
			throw new ConsumeTokenException("cannot consume '" + this.source.peek() + "' token.");
		}
	}
	
	private void consumeN(int nb, String token) throws ConsumeTokenException {
		for(int i = 0; i < nb; i++)
			this.consume(token);
	}
	
	private void backtrack(ArrayDeque<String> source, ArrayDeque<String> initial) {
		source.clear();
		source.addAll(initial);
	}
	
	// Parsers
	private String parseUnformattedText(String[] delims, boolean checkEOF) {
		String output = "";
		List<String> listDelims = Arrays.asList(delims);
		boolean done = false;
		while(!done) {
			if(listDelims.contains(this.source.peek()) || (checkEOF && this.source.isEmpty())) 
				done = true;
			else {
				output += this.source.peek();
				this.source.poll();
			}
		}
		return output;
	}
	
	private void parseTitle(int n) throws ConsumeTokenException {
		this.consumeN(n, "#");
		this.consume(" ");
		
		// Read until a newline or an EOF
		String text = this.parseUnformattedText(Parser.LINE_DELIMITERS, true);

		// Parse the newline
		if(this.source.peek().equals("\n"))
			this.consume("\n");
		
		this.ast.add(new TitleToken(new UnformattedTextToken(text), n));
		this.ast.add(new NewlineToken());
	}
	
	private void parseNewline() throws ConsumeTokenException {
		this.consume("\n");
		this.ast.add(new NewlineToken());
	}
	
	private void parseQuote() throws ConsumeTokenException {
		this.consume(">");
		this.consume(" ");
	}
	
	private void parseBold() throws ConsumeTokenException {
		this.consumeN(2, "*");
	}
	
	private void parseItalic() throws ConsumeTokenException {
		this.consume("*");
	}
	
	private void parseUnderline() throws ConsumeTokenException {
		this.consume("_");
	}
	
	private Token[] parseParagraph() throws ConsumeTokenException {
		TokenStack stack = new TokenStack();
		
		while(!this.source.peek().equals("\n")) {
			this.backtrack(this.initialSource, this.source);
			try {
				this.parseBold();
				stack.handle(new BoldToken());
			} catch(ConsumeTokenException e) {
				try {
					this.backtrack(this.source, this.initialSource);
					this.parseItalic();
					stack.handle(new ItalicToken());
				} catch(ConsumeTokenException e2) {
					try {
						this.parseUnderline();
						stack.handle(new UnderlineToken());
					} catch(ConsumeTokenException e3) {
						stack.addToken(new UnformattedTextToken(this.parseUnformattedText(Parser.PARAGRAPH_DELIMITERS, false)));
					}
				}
			}
		}
		
		return stack.getTokens();
	}
	
	public void parse() {
		while(!this.source.isEmpty()) {
			this.backtrack(this.initialSource, this.source);
			try {
				this.parseTitle(3);
			} catch(ConsumeTokenException e) {
				try {
					// Backtrack the text to previous state
					this.backtrack(this.source, this.initialSource);
					this.parseTitle(2);
				} catch(ConsumeTokenException e2) {
					try {
						this.backtrack(this.source, this.initialSource);
						this.parseTitle(1);
					} catch(ConsumeTokenException e3) {
						try {
							this.parseNewline();
						} catch(ConsumeTokenException e4) {
							try {
								this.parseQuote();
								this.ast.add(new QuoteToken(this.parseParagraph()));
							} catch(ConsumeTokenException e5) {
								try {
									this.ast.add(new ParagraphToken(this.parseParagraph()));
								} catch(ConsumeTokenException e6) {
									System.out.println(e3.getMessage());
									return;
								}
							}
						}
					}
				}
			}
		}	
	}
}
