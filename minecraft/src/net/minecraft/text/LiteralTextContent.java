package net.minecraft.text;

import java.util.Optional;

public record LiteralTextContent(String string) implements TextContent {
	@Override
	public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		return visitor.accept(this.string);
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
		return visitor.accept(style, this.string);
	}

	public String toString() {
		return "literal{" + this.string + "}";
	}
}
