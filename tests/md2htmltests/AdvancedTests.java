package md2htmltests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import md2html.parser.Parser;
import md2html.tokenizer.*;
import md2html.tokenizer.enums.ItemType;
import md2html.tokenizer.enums.LinkType;
import md2html.tokenizer.enums.TokenDisplay;

// For each test, the AST and the HTML code is checked
public class AdvancedTests {
	private String case1, case2, case3, case4, case5, case6, case7, case8, case9;
	
	@BeforeEach
	public void initialization() {
		this.case1 = "- a\n"
				+ "    - b\n"
				+ "    - c\n"
				+ "- d\n";
		
		this.case2 = "- a\n"
				+ "    1. b\n"
				+ "    2. c\n"
				+ "- d\n";
		
		this.case3 = "1. a\n"
				+ "    - b\n"
				+ "    - c\n"
				+ "2. d\n";
		
		this.case4 = "1. a\n"
				+ "    1. b\n"
				+ "    2. c\n"
				+ "2. d\n";
		
		this.case5 = "1. some text\n"
				+ "    - some text **bold** *italic* and ***both*** _underline_ **some text 2 *_bold, italic and underline_* some text 3** some text 4\n"
				+ "    - some text 2\n"
				+ "2. some text **bold** *italic* and ***both*** _underline_ **some text 2 *_bold, italic and underline_* some text 3** some text 4\n";
		
		this.case6 = "- \n"
				+ "- b\n"
				+ "- c\n"
				+ "- d\n";
		this.case7 = "```\n"
				+ "def fact(n):\n"
				+ "	if n < 2:\n"
				+ "		return n\n"
				+ "	else:\n"
				+ "		return n * fact(n-1)\n"
				+ "```\n"
				+ "This is ``code``";
		this.case8 = "This is [link](url)\n"
				+ "![img](img_url)\n"
				+ "[![img](img_url)](url)";
		this.case9 = "[note](note)[0] [note](note)[1] [note](note)[2] [note](note)[4] [note](note)[3]";
	}

	@Test
	public void testUnorderedList() {
		Parser p = new Parser(this.case1);
		p.parse();
		Token[] expected = new Token[] {
			new ListToken(ItemType.UNORDERED, false,
					new ListItemToken(
							new UnformattedTextToken("a"),
							new ListToken(ItemType.UNORDERED, true,
									new ListItemToken(new UnformattedTextToken("b")),
									new ListItemToken(new UnformattedTextToken("c"))
							)
					),
					new ListItemToken(new UnformattedTextToken("d"))
			)
		};
		BasicTests.checkTest(expected, p.getAST());
		
		// ----------------------
		
		Parser p2 = new Parser(this.case2);
		p2.parse();
		Token[] expected2 = new Token[] {
				new ListToken(ItemType.UNORDERED, false,
						new ListItemToken(
								new UnformattedTextToken("a"),
								new ListToken(ItemType.ORDERED, true,
										new ListItemToken(new UnformattedTextToken("b")),
										new ListItemToken(new UnformattedTextToken("c"))
								)
						),
						new ListItemToken(new UnformattedTextToken("d"))
				)
		};
		BasicTests.checkTest(expected2, p2.getAST());
	}
	
	@Test
	public void testOrderedList() {
		Parser p = new Parser(this.case3);
		p.parse();
		Token[] expected = new Token[] {
				new ListToken(ItemType.ORDERED, false,
						new ListItemToken(
								new UnformattedTextToken("a"),
								new ListToken(ItemType.UNORDERED, true,
										new ListItemToken(new UnformattedTextToken("b")),
										new ListItemToken(new UnformattedTextToken("c"))
								)
						),
						new ListItemToken(new UnformattedTextToken("d"))
				)
		};
		BasicTests.checkTest(expected, p.getAST());
		
		// ----------------------
		
		Parser p2 = new Parser(this.case4);
		p2.parse();
		Token[] expected2 = new Token[] {
				new ListToken(ItemType.ORDERED, false,
						new ListItemToken(
								new UnformattedTextToken("a"),
								new ListToken(ItemType.ORDERED, true,
										new ListItemToken(new UnformattedTextToken("b")),
										new ListItemToken(new UnformattedTextToken("c"))
								)
						),
						new ListItemToken(new UnformattedTextToken("d"))
				)
		};
		BasicTests.checkTest(expected2, p2.getAST());
	}
	
	@Test
	public void testListWithFormattedText() {
		Parser p = new Parser(this.case5);
		p.parse();
		Token[] expected = new Token[] {
				new ListToken(ItemType.ORDERED, false,
						new ListItemToken(
								new UnformattedTextToken("some text"),
								new ListToken(ItemType.UNORDERED, true,
										new ListItemToken(BasicTests.commonExpectedAST),
										new ListItemToken(new UnformattedTextToken("some text 2"))
								)
						),
						new ListItemToken(BasicTests.commonExpectedAST)
				)
		};
		BasicTests.checkTest(expected, p.getAST());
	}
	
	@Test
	public void testCode() {
		Parser p = new Parser(this.case7);
		p.parse();
		Token[] expected = new Token[] {
				new CodeToken(TokenDisplay.BLOCK, new UnformattedTextToken("def fact(n):<br>	if n < 2:<br>		return n<br>	else:<br>		return n * fact(n-1)<br>")),
				new ParagraphToken(new UnformattedTextToken("This is "), new CodeToken(TokenDisplay.INLINE, new UnformattedTextToken("code"))),
				new NewlineToken()
		};
		BasicTests.checkTest(expected, p.getAST());
	}

	@Test
	public void testLink() {
		Parser p = new Parser(this.case8);
		p.parse();
		Token[] expected = new Token[] {
				new ParagraphToken(
						new UnformattedTextToken("This is "),
						new URLToken(LinkType.HYPERLINK, new UnformattedTextToken("link"), "url")
				),
				new NewlineToken(),
				new URLToken(LinkType.IMAGE, new UnformattedTextToken("img"), "img_url"),
				new NewlineToken(),
				new URLToken(LinkType.HYPERLINK, new URLToken(LinkType.IMAGE, new UnformattedTextToken("img"), "img_url"), "url"),
				new NewlineToken()
		};
		BasicTests.checkTest(expected, p.getAST());
		
		// ----------------------
		
		Parser p2 = new Parser(this.case9);
		p2.parse();
		Token[] expected2 = new Token[] {
				new ParagraphToken(
						new FootNoteToken("0", "note"),
						new UnformattedTextToken(" "),
						new FootNoteToken("1", "note"),
						new UnformattedTextToken(" "),
						new FootNoteToken("2", "note"),
						new UnformattedTextToken(" "),
						new FootNoteToken("4", "note"),
						new UnformattedTextToken(" "),
						new FootNoteToken("3", "note")
				),
				new NewlineToken(),
				new SeparatorToken(),
				new TitleToken(new UnformattedTextToken("References:"), 3),
				new ListToken(ItemType.ORDERED, false, 
						new ListItemToken("1", new URLToken(LinkType.HYPERLINK, new UnformattedTextToken("note"), "note")),
						new ListItemToken("2", new URLToken(LinkType.HYPERLINK, new UnformattedTextToken("note"), "note")),
						new ListItemToken("3", new URLToken(LinkType.HYPERLINK, new UnformattedTextToken("note"), "note"))
				)
		};
		BasicTests.checkTest(expected2, p2.getAST());
	}
	
	@Test
	public void testInvalidMarkdown() {
		Parser p = new Parser(this.case6);
		p.parse();
		Token[] expected = new Token[] {
			new ListToken(ItemType.UNORDERED, false,
					new ListItemToken(),
					new ListItemToken(new UnformattedTextToken("b")),
					new ListItemToken(new UnformattedTextToken("c")),
					new ListItemToken(new UnformattedTextToken("d"))
			)
		};
		BasicTests.checkTest(expected, p.getAST());
	}
}
