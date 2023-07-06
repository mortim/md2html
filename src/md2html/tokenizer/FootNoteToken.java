package md2html.tokenizer;

public class FootNoteToken extends Token {
	private String id;
	private String desc;
	
	public FootNoteToken(String id, String desc) {
		super();
		this.id = id;
		this.desc = desc;
	}

	@Override
	public String getSymbol(boolean closed) {
		return closed ? "</sup>" : "<sup style=\"color: blue; cursor: pointer\" onclick=customScroll('" + this.id + "')>";
	}
	
	@Override
	public String toHTML() {
		return this.desc + this.getSymbol(false) + this.id + this.getSymbol(true);
	}
	
	// Default implementation, can be overrided
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(id="+ this.id + ", desc=" + this.desc + ")";
	}

}
