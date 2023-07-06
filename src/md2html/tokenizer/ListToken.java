package md2html.tokenizer;

import md2html.parser.Parser;
import md2html.parser.RenderType;
import md2html.tokenizer.enums.ItemType;

public class ListToken extends Token {
	private ItemType itemType;
	private boolean sublist;
	
	public ListToken(ItemType type, boolean sublist, ListItemToken... items) {
		super(items);
		this.itemType = type;
		this.sublist = sublist;
	}
	
	public ItemType getType() {
		return this.itemType;
	}

	public boolean isSublist() {
		return this.sublist;
	}
	
	@Override
	public String getSymbol(boolean closed) {
		String tag = this.itemType == ItemType.ORDERED ? "ol" : "ul";
		return closed ? "</" + tag + ">" : "<" + tag + ">";
	}

	@Override
	public String toHTML() {
		String out = this.getSymbol(false);
		for(Token token : this.tokens)
			out += token.toHTML();
		return out + this.getSymbol(true);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(type=" + this.itemType + ", sublist=" + this.sublist + ", " + Parser.renderTo(RenderType.AST, this.tokens);
	}
}
