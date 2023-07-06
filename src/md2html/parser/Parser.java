package md2html.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import md2html.tokenizer.*;
import md2html.tokenizer.enums.ItemType;
import md2html.tokenizer.enums.LinkType;
import md2html.tokenizer.enums.TokenDisplay;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

public class Parser {
	private ArrayDeque<String> source;
	private List<Token> ast;
	private ArrayDeque<String> initialSource;
	private ListToken footNotes;
	private static final String[] LINE_DELIMITERS = new String[]{"\n"};
	private static final String[] PARAGRAPH_DELIMITERS = new String[]{"\n", "*", "_", "`", "["};
	
	public Parser(String text) {
		if(text.charAt(text.length()-1) != '\n')
			text += '\n';
		this.source = new ArrayDeque<>(Arrays.asList(text.split("")));
		this.ast = new ArrayList<>();
		this.initialSource = new ArrayDeque<>();
		this.footNotes = new ListToken(ItemType.ORDERED, false);
	}
	
	public Token[] getAST() {
		return this.ast.toArray(new Token[this.ast.size()]);
	}
	
	public static String renderTo(RenderType type, Token[] tokens) {
		String out = "";
		for(Token token : tokens)
			out += type == RenderType.AST ? token.toString() : token.toHTML();
		return out;
	}

	// Utils
	private void consume(int nb, String token) throws ConsumeTokenException {
		for(int i = 0; i < nb; i++) {
			// For this.parseCodeBlock(), to avoid an NullPointerException thrown by the "`" token consumption
			if(!this.source.isEmpty()) {
				if(this.source.peek().equals(token))
					this.source.poll();
				else {
					throw new ConsumeTokenException("cannot consume '" + this.source.peek() + "' token.");
				}
			} else
				throw new ConsumeTokenException("cannot consume an empty source");
		}
	}
	
	private void consume(String token) throws ConsumeTokenException {
		this.consume(1, token);
	}
	
	private void backtrack(ArrayDeque<String> source, ArrayDeque<String> initial) {
		source.clear();
		source.addAll(initial);
	}
	
	private Token getLastToken() {
		if(!this.ast.isEmpty())
			return this.ast.get(this.ast.size()-1);
		else
			return null;
	}
	
	private void consumeNewlineForBlockToken() {
		if(this.ast.size() > 1) {
			if(this.ast.get(this.ast.size()-2).getClass().getSimpleName().equals("NewlineToken"))
				this.ast.remove(this.ast.size()-2);
		}
	}
	
	private String decodeURLFormat(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			return "";
		}
	}
	
	// Parsers
	private String parseUnformattedText(String[] delims) {
		String output = "";
		List<String> listDelims = Arrays.asList(delims);
		boolean done = false;
		while(!done) {
			if(listDelims.contains(this.source.peek()) || this.source.isEmpty()) 
				done = true;
			else {
				output += this.source.peek().equals("\n") ? "<br>" : this.source.peek();
				this.source.poll();
			}
		}
		return output;
	}
	
	private void parseTitle(int n) throws ConsumeTokenException {
		this.consume(n, "#");
		this.consume(" ");
		
		// Read until a newline or an EOF
		String text = this.parseUnformattedText(Parser.LINE_DELIMITERS);

		// Consume the newline
		this.consume("\n");

		this.ast.add(new TitleToken(new UnformattedTextToken(text), n));
	}
	
	private void parseTitles() throws ConsumeTokenException {
		try {
			this.parseTitle(6);
		} catch(ConsumeTokenException e) {
			try {
				// Backtrack the text to previous state
				this.backtrack(this.source, this.initialSource);
				this.parseTitle(5);
			} catch(ConsumeTokenException e2) {
				try {
					this.backtrack(this.source, this.initialSource);
					this.parseTitle(4);
				} catch(ConsumeTokenException e3) {
					try {
						this.backtrack(this.source, this.initialSource);
						this.parseTitle(3);
					} catch(ConsumeTokenException e4) {
						try {
							this.backtrack(this.source, this.initialSource);
							this.parseTitle(2);
						} catch(ConsumeTokenException e5) {
							this.backtrack(this.source, this.initialSource);
							this.parseTitle(1);
						}
					}
				}
			}
		}
	}
	
	private void parseNewline() throws ConsumeTokenException {
		this.consume("\n");
		this.ast.add(new NewlineToken());
	}

	private void parseQuote() throws ConsumeTokenException {
		// Keep the initial source because this source is lost with this this.parseParagraph() invokation
		ArrayDeque<String> defaultSource = new ArrayDeque<>(this.initialSource);
		boolean nested = false;

		try {
			this.consume(2, ">");
			this.consume(" ");
			nested = true;
		} catch(ConsumeTokenException e) {
			this.backtrack(this.source, this.initialSource);
			this.consume(">");
			this.consume(" ");
		}
		
		Token[] content = this.parseParagraph();
		this.backtrack(initialSource, defaultSource);
		this.consume("\n");
	
		if(!this.ast.isEmpty()) {
			Token lastToken = this.getLastToken();

			if(lastToken.getClass().getSimpleName().equals("QuoteToken")) {
				if(nested)
					lastToken.addToken(new QuoteToken(content));
				else {
					Token[] tokens = lastToken.getTokens();
					if(!tokens[tokens.length-1].getClass().getSimpleName().equals("QuoteToken"))
						lastToken.addToken(new NewlineToken());
					lastToken.addToken(content);
				}
			} else {
				if(nested)
					throw new ConsumeTokenException("cannot parse nested quote without a main quote.");
				else
					this.ast.add(new QuoteToken(content));
			}
		} else {
			if(nested)
				throw new ConsumeTokenException("cannot parse nested quote without a main quote.");
			else
				this.ast.add(new QuoteToken(content));
		}
	}
	
	private void parseBold() throws ConsumeTokenException {
		this.consume(2, "*");
	}
	
	private void parseItalic() throws ConsumeTokenException {
		this.consume("*");
	}
	
	private void parseUnderline() throws ConsumeTokenException {
		this.consume("_");
	}
	
	private Token parseCode(TokenDisplay display) throws ConsumeTokenException {
		String[] delims;
		int n = display == TokenDisplay.BLOCK ? 3 : 2;
		delims = display == TokenDisplay.BLOCK ? new String[]{"`"} : new String[]{"`", "\n"};
		
		this.consume(n, "`");
		
		if(display == TokenDisplay.BLOCK)
			this.consume("\n");
		
		String content = this.parseUnformattedText(delims);
		this.consume(n, "`");
		
		if(display == TokenDisplay.BLOCK)
			this.consume("\n");
		
		return new CodeToken(display, new UnformattedTextToken(content));
	}
	
	private String[] parseLink() throws ConsumeTokenException {
		String[] content = new String[2];
		this.consume("[");
		content[0] = this.parseUnformattedText(new String[]{"]", "\n"});
		this.consume("]");
		this.consume("(");
		content[1] = this.parseUnformattedText(new String[]{")", "\n"});
		this.consume(")");
		return content;
	}

	private Token parseHyperlink() throws ConsumeTokenException {
		String[] content = this.parseLink();
		return new URLToken(LinkType.HYPERLINK, new UnformattedTextToken(content[0]), content[1]);
	}
	
	private Token parseFootNote() throws NumberFormatException, ConsumeTokenException {
		String[] content = this.parseLink();
		this.consume("[");
		int id = Integer.parseInt(this.parseUnformattedText(new String[]{"]", "\n"}));
		this.consume("]");
		
		if(id == this.footNotes.getTokens().length+1)
			this.footNotes.addToken(new ListItemToken(String.valueOf(id), new URLToken(LinkType.HYPERLINK, new UnformattedTextToken(content[1]), this.decodeURLFormat(content[1]))));

		return new FootNoteToken(String.valueOf(id), content[0]);
	}

	private Token[] parseParagraph() {
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
						try {
							stack.addToken(this.parseCode(TokenDisplay.INLINE));
						} catch(ConsumeTokenException e4) {
							try {
								stack.addToken(this.parseFootNote());
							} catch(NumberFormatException | ConsumeTokenException e5) {
								try {
									this.backtrack(this.source, this.initialSource);
									stack.addToken(this.parseHyperlink());
								} catch(ConsumeTokenException e6) {
									stack.addToken(new UnformattedTextToken(this.parseUnformattedText(Parser.PARAGRAPH_DELIMITERS)));
								}
							}
						}
					}
				}
			}
		}

		return stack.getTokens();
	}
	
	private void parseItem(ItemType type) throws NumberFormatException, ConsumeTokenException {
		if(type == ItemType.ORDERED) {
			Integer.parseInt(this.parseUnformattedText(new String[]{"."}));
			this.consume(".");
			this.consume(" ");
		} else {
			this.consume("-");
			this.consume(" ");
		}
	}
	
	private boolean parseItemList(ItemType type) throws NumberFormatException, ConsumeTokenException {
		boolean subitem = false;

		try {
			this.consume(4, " ");
			this.parseItem(type);
			subitem = true;
		} catch(ConsumeTokenException e3) {
			this.parseItem(type);
		}

		return subitem;
	}
	
	private void parseList() throws ConsumeTokenException {
		ItemType itemType = null;
		boolean subitem = false;

		try {
			itemType = ItemType.ORDERED;
			subitem = this.parseItemList(itemType);
		} catch(NumberFormatException | ConsumeTokenException e2) {
			// Backtrack before the unformatted text parsing above
			this.backtrack(this.source, this.initialSource);
			itemType = ItemType.UNORDERED;
			subitem = this.parseItemList(itemType);
		}
		
		Token[] content = this.parseParagraph();
		this.consume("\n");

		Token token = this.getLastToken();

		if(token != null) {
			if(token.getClass().getSimpleName().equals("ListToken")) {
				Token[] items = token.getTokens();
				Token lastItem = items[items.length-1];
				Token[] itemsOfLastItem = lastItem.getTokens();
				Token lastItemOfLastItem;
				
				if(itemsOfLastItem.length != 0)
					lastItemOfLastItem = itemsOfLastItem[itemsOfLastItem.length-1];
				else
					lastItemOfLastItem = lastItem;
				
				if(subitem) {
					if(lastItemOfLastItem.getClass().getSimpleName().equals("ListToken"))
						lastItemOfLastItem.addToken(new ListItemToken(content));
					else
						lastItem.addToken(new ListToken(itemType, subitem, new ListItemToken(content)));
				} else
					token.addToken(new ListItemToken(content));
			} else {
				if(subitem)
					throw new ConsumeTokenException("cannot create a sublist without a main list.");
				else
					this.ast.add(new ListToken(itemType, subitem, new ListItemToken(content)));
			}
		} else {
			if(subitem)
				throw new ConsumeTokenException("cannot create a sublist without a main list.");
			else
				this.ast.add(new ListToken(itemType, subitem, new ListItemToken(content)));
		}
	}
	
	private Token parseImage() throws ConsumeTokenException {
		this.consume("!");
		String[] content = this.parseLink();
		return new URLToken(LinkType.IMAGE, new UnformattedTextToken(content[0]), content[1]);
	}
	
	private void parseImageWithLink() throws ConsumeTokenException {
		this.consume("[");
		Token img = this.parseImage();
		this.consume("]");
		this.consume("(");
		String url = this.parseUnformattedText(new String[]{")", "\n"});
		this.consume(")");
		this.ast.add(new URLToken(LinkType.HYPERLINK, img, url));
	}
	
	private void parseSeparator() throws ConsumeTokenException {
		this.consume(3, "-");
		this.parseNewline();
		this.ast.add(new SeparatorToken());
	}

	public void parse() {
		while(!this.source.isEmpty()) {
			this.backtrack(this.initialSource, this.source);
			try {
				this.parseTitles();
				this.consumeNewlineForBlockToken();
			} catch(ConsumeTokenException e) {
				try {
					this.parseNewline();
				} catch(ConsumeTokenException e2) {
					try {
						this.parseList();
						this.consumeNewlineForBlockToken();
					} catch(ConsumeTokenException e3) {
						try {
							this.backtrack(this.source, this.initialSource);
							this.parseQuote();
							this.consumeNewlineForBlockToken();
						} catch(ConsumeTokenException e4) {
							try {
								this.ast.add(this.parseCode(TokenDisplay.BLOCK));
								this.consumeNewlineForBlockToken();
							} catch(ConsumeTokenException e5) {
								try {
									this.ast.add(this.parseImage());
								} catch(ConsumeTokenException e6) {
									try {
										this.backtrack(this.source, this.initialSource);
										this.parseImageWithLink();
									} catch(ConsumeTokenException e7) {
										try {
											this.parseSeparator();
											this.consumeNewlineForBlockToken();
										} catch(ConsumeTokenException e8) {
											this.backtrack(this.source, this.initialSource);
											this.ast.add(new ParagraphToken(this.parseParagraph()));
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(this.footNotes.getTokens().length != 0) {
			this.ast.add(new SeparatorToken());
			this.ast.add(new TitleToken(new UnformattedTextToken("References:"), 3));
			this.ast.add(this.footNotes);
		}
		
	}
}