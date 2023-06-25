import tokenizer.*;

public class App {
	
	public static void main(String[] args) {
		try {
			Token[] tokens = new Token[] {
				new TitleToken(new UnformattedTextToken("Titre 1"), 1),
				new TitleToken(new UnformattedTextToken("Titre 2"), 2),
				new ParagraphToken(
					new BoldToken(
							new UnformattedTextToken("bold"),
							new ItalicToken(
									new UnformattedTextToken("and italic")))
				),
				new QuoteToken(
						new UnformattedTextToken("IMPORTANT : This is important message "),
						new UnderlineToken(new UnformattedTextToken("and underlined")))
			};
			
			for(Token token : tokens)
				System.out.println(token.toHTML());
		} catch(TokenFormatException e) {
			System.out.println(e.getMessage());
		}
		
	}
}
