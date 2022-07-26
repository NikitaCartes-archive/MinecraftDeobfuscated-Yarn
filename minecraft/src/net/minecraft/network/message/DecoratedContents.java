package net.minecraft.network.message;

import java.util.Objects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

/**
 * A pair of the decorated message content and its undecorated ("plain") message content.
 * Note that the two contents can be equal if no decoration is applied.
 */
public record DecoratedContents(String plain, Text decorated) {
	public DecoratedContents(String content) {
		this(content, Text.literal(content));
	}

	public boolean isDecorated() {
		return !this.decorated.equals(Text.literal(this.plain));
	}

	public static DecoratedContents read(PacketByteBuf buf) {
		String string = buf.readString(256);
		Text text = buf.readNullable(PacketByteBuf::readText);
		return new DecoratedContents(string, (Text)Objects.requireNonNullElse(text, Text.literal(string)));
	}

	public static void write(PacketByteBuf buf, DecoratedContents contents) {
		buf.writeString(contents.plain(), 256);
		Text text = contents.isDecorated() ? contents.decorated() : null;
		buf.writeNullable(text, PacketByteBuf::writeText);
	}
}
