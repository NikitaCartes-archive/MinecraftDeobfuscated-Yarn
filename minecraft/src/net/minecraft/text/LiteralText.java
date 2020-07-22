package net.minecraft.text;

public class LiteralText extends BaseText {
	public static final Text EMPTY = new LiteralText("");
	private final String string;

	public LiteralText(String string) {
		this.string = string;
	}

	public String getRawString() {
		return this.string;
	}

	@Override
	public String asString() {
		return this.string;
	}

	public LiteralText copy() {
		return new LiteralText(this.string);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof LiteralText)) {
			return false;
		} else {
			LiteralText literalText = (LiteralText)object;
			return this.string.equals(literalText.getRawString()) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "TextComponent{text='" + this.string + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}
}
