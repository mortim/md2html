package tokenizer;

import java.util.Arrays;
import java.util.List;

public class Utils {
	
	public static boolean hasCorrectFormat(Token[] tokens, String[] allowedTypes) {
		List<String> allowed = Arrays.asList(allowedTypes);
		for(Token token : tokens) {
			if(!allowed.contains(token.getClass().getSimpleName()))
				return false;
		}
		return true;
	}
	
	public static String renderTo(RenderType type, Token[] tokens) {
		String out = "";
		for(Token token : tokens) {
			if(type == RenderType.AST)
				out += token.toString();
			else
				out += token.toHTML();
		}
		return out;
	}
}
