import parser.Parser;

public class App {
	
	public static void main(String[] args) {
		String text = "# titre\n"
				+ "abc **d** ***e* f _m_** ***_g_***\n"
				+ "> IMPORTANT : This is important message _and underlined_\n";
		
		Parser p = new Parser(text);
		p.parse();
		System.out.println(p.toHTML());
	}
}
