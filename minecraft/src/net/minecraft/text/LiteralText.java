package net.minecraft.text;

import javax.annotation.Nullable;
import net.minecraft.util.Language;

public class LiteralText extends BaseText {
	public static final Text EMPTY = new LiteralText("");
	private final String string;
	@Nullable
	private Language field_25315;
	private String field_25316;

	public LiteralText(String string) {
		this.string = string;
		this.field_25316 = string;
	}

	public String getRawString() {
		return this.string;
	}

	@Override
	public String asString() {
		if (this.string.isEmpty()) {
			return this.string;
		} else {
			Language language = Language.getInstance();
			if (this.field_25315 != language) {
				this.field_25316 = language.reorder(this.string, false);
				this.field_25315 = language;
			}

			return this.field_25316;
		}
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
