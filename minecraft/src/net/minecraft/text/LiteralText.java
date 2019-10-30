package net.minecraft.text;

public class LiteralText extends BaseText {
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

	public LiteralText method_10992() {
		return new LiteralText(this.string);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof LiteralText)) {
			return false;
		} else {
			LiteralText literalText = (LiteralText)o;
			return this.string.equals(literalText.getRawString()) && super.equals(o);
		}
	}

	@Override
	public String toString() {
		return "TextComponent{text='" + this.string + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}
}
