package net.minecraft.text;

import java.util.Optional;
import net.minecraft.class_7417;

public record LiteralText(String string) implements class_7417 {
	@Override
	public <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
		return visitor.accept(this.string);
	}

	@Override
	public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
		return styledVisitor.accept(style, this.string);
	}

	public String toString() {
		return "literal{" + this.string + "}";
	}
}
