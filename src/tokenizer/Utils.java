package tokenizer;

public class Utils {

	public static String renderTo(RenderType type, Token[] tokens) {
		String out = "";
		for(int i = 0; i < tokens.length; i++) {
			if(type == RenderType.AST) {
				out += tokens[i].toString();
				if(i != tokens.length-1)
					out += ", ";
			} else
				out += tokens[i].toHTML();
		}
		return out + (type == RenderType.AST ? ")" : "");
	}
}
