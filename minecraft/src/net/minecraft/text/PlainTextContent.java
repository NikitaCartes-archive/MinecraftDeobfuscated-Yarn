package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public interface PlainTextContent extends TextContent {
	MapCodec<PlainTextContent> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codec.STRING.fieldOf("text").forGetter(PlainTextContent::string)).apply(instance, PlainTextContent::of)
	);
	TextContent.Type<PlainTextContent> TYPE = new TextContent.Type<>(CODEC, "text");
	PlainTextContent EMPTY = new PlainTextContent() {
		public String toString() {
			return "empty";
		}

		@Override
		public String string() {
			return "";
		}
	};

	static PlainTextContent of(String string) {
		return (PlainTextContent)(string.isEmpty() ? EMPTY : new PlainTextContent.Literal(string));
	}

	String string();

	@Override
	default TextContent.Type<?> getType() {
		return TYPE;
	}

	public static record Literal(String string) implements PlainTextContent {
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
}
