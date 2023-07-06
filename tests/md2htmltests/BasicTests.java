package md2htmltests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import md2html.parser.Parser;
import md2html.parser.RenderType;
import md2html.tokenizer.*;

// For each test, the AST and the HTML code is checked
public class BasicTests {
	public static Token[] commonExpectedAST = new Token[] {
			new UnformattedTextToken("some text "),
			new BoldToken(new UnformattedTextToken("bold")),
			new UnformattedTextToken(" "),
			new ItalicToken(new UnformattedTextToken("italic")),
			new UnformattedTextToken(" and "),
			new BoldToken(new ItalicToken(new UnformattedTextToken("both"))),
			new UnformattedTextToken(" "),
			new UnderlineToken(new UnformattedTextToken("underline")),
			new UnformattedTextToken(" "),
			new BoldToken(
					new UnformattedTextToken("some text 2 "),
					new ItalicToken(new UnderlineToken(new UnformattedTextToken("bold, italic and underline"))),
					new UnformattedTextToken(" some text 3")
			),
			new UnformattedTextToken(" some text 4")
	};
	
	private String case1, case2, case3, case4, case5, case6, case7, case8, case9, case10, case11, case12, case13, case14, case15, case16;

	public static void checkTest(Token[] expected, Token[] actual) {
		assertEquals(
				Parser.renderTo(RenderType.AST, expected),
				Parser.renderTo(RenderType.AST, actual)
		);
		
		assertEquals(
				Parser.renderTo(RenderType.HTML, expected),
				Parser.renderTo(RenderType.HTML, actual)
		);
	}
	
	@BeforeEach
	public void initialization() {
		this.case1 = "some text";
		this.case2 = "**some text**\n";
		this.case3 = "*some text*\n";
		this.case4 = "_some text_\n";
		this.case5 = "> some text\n";
		this.case6 = "# title 1\n";
		this.case7 = "## title 2\n";
		this.case8 = "### title 3\n";
		this.case9 = "#### title 4\n";
		this.case10 = "##### title 5\n";
		this.case11 = "###### title 6\n";
		this.case12 = "some text **bold** *italic* and ***both*** _underline_ **some text 2 *_bold, italic and underline_* some text 3** some text 4\n";
		this.case13 = "#title 1\n";
		this.case14 = ">some text\n";
		this.case15 = "Some text\n"
				+ "> Some text 2\n"
				+ "> Some ***text* 3**\n\n"
				+ "Some text 4\n"
				+ ">> Some text 5\n"
				+ "> Some text 6\n"
				+ ">> *Some **text _7_***\n"
				+ "> \n";
		this.case16 = "Some text\n"
				+ "---\n"
				+ "Some text 2";
	}
	
	@Test
	public void testSimpleCode() {
		Parser p = new Parser(this.case1);
		p.parse();
		Token[] expected = new Token[] {
			new ParagraphToken(new UnformattedTextToken("some text")),
			new NewlineToken()
		};
		BasicTests.checkTest(expected, p.getAST());
		
		// ----------------------
		
		Parser p2 = new Parser(this.case2);
		p2.parse();
		Token[] expected2 = new Token[] {
				new ParagraphToken(new BoldToken(new UnformattedTextToken("some text"))),
				new NewlineToken()
		};
		BasicTests.checkTest(expected2, p2.getAST());
		
		// ----------------------
		
		Parser p3 = new Parser(this.case3);
		p3.parse();
		Token[] expected3 = new Token[] {
				new ParagraphToken(new ItalicToken(new UnformattedTextToken("some text"))),
				new NewlineToken()
		};
		BasicTests.checkTest(expected3, p3.getAST());
		
		// ----------------------
		
		Parser p4 = new Parser(this.case4);
		p4.parse();
		Token[] expected4 = new Token[] {
				new ParagraphToken(new UnderlineToken(new UnformattedTextToken("some text"))),
				new NewlineToken()
		};
		BasicTests.checkTest(expected4, p4.getAST());
		
		// ----------------------
		
		Parser p5 = new Parser(this.case5);
		p5.parse();
		Token[] expected5 = new Token[] {
				new QuoteToken(new UnformattedTextToken("some text"))
		};
		BasicTests.checkTest(expected5, p5.getAST());
		
		// ----------------------
		
		Parser p6 = new Parser(this.case16);
		p6.parse();
		Token[] expected6 = new Token[] {
				new ParagraphToken(new UnformattedTextToken("Some text")),
				new NewlineToken(),
				new SeparatorToken(),
				new ParagraphToken(new UnformattedTextToken("Some text 2")),
				new NewlineToken()
		};
		BasicTests.checkTest(expected6, p6.getAST());
	}
	
	@Test
	public void testTitle() throws NoSuchFieldException, IllegalAccessException {
		Field field;
		Parser p;
		Token[] expected;

		for(int i = 1; i <= 6; i++) {
			field = this.getClass().getDeclaredField("case" + (5+i));
			field.setAccessible(true);
			p = new Parser((String)field.get(this));
			p.parse();
			expected = new Token[] {
				new TitleToken(new UnformattedTextToken("title " + i), i)
			};
			BasicTests.checkTest(expected, p.getAST());
		}
	}
	
	@Test
	public void testAdvancedCode() {
		// Test advanced formatted text
		Parser p = new Parser(this.case12);
		p.parse();
		Token[] expected = new Token[] {
				new ParagraphToken(BasicTests.commonExpectedAST),
				new NewlineToken()
		};
		BasicTests.checkTest(expected, p.getAST());

		// ----------------------
		// Test simple quote
		Parser p2 = new Parser("> " + this.case12);
		p2.parse();
		Token[] expected2 = new Token[]{
				new QuoteToken(BasicTests.commonExpectedAST)
		};
		BasicTests.checkTest(expected2, p2.getAST());

		// ----------------------
		// Test advanced quote
		Parser p3 = new Parser(this.case15);
		p3.parse();
		Token[] expected3 = new Token[] {
				new ParagraphToken(new UnformattedTextToken("Some text")),
				new QuoteToken(
						new UnformattedTextToken("Some text 2"),
						new NewlineToken(),
						new UnformattedTextToken("Some "),
						new BoldToken(new ItalicToken(new UnformattedTextToken("text")), new UnformattedTextToken(" 3"))
				),
				new NewlineToken(),
				new ParagraphToken(new UnformattedTextToken("Some text 4")),
				new NewlineToken(),
				new ParagraphToken(new UnformattedTextToken(">> Some text 5")),
				new QuoteToken(
						new UnformattedTextToken("Some text 6"),
						new QuoteToken(new ItalicToken(new UnformattedTextToken("Some "), new BoldToken(new UnformattedTextToken("text "), new UnderlineToken(new UnformattedTextToken("7")))))
				)
		};
		BasicTests.checkTest(expected3, p3.getAST());
	}
	
	@Test
	public void testInvalidMarkdown() {
		Parser p = new Parser(this.case13);
		p.parse();
		Token[] expected = new Token[] {
				new ParagraphToken(new UnformattedTextToken("#title 1")),
				new NewlineToken()
		};
		BasicTests.checkTest(expected, p.getAST());
		
		// ----------------------
		
		Parser p2 = new Parser(this.case14);
		p2.parse();
		Token[] expected2 = new Token[] {
				new ParagraphToken(new UnformattedTextToken(">some text")),
				new NewlineToken()
		};
		BasicTests.checkTest(expected2, p2.getAST());
	}
	
}
