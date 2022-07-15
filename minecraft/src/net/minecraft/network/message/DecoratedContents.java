package net.minecraft.network.message;

import java.util.Objects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.text.Text;

/**
 * A pair of the decorated message content and its undecorated ("plain") message content.
 * Note that the two contents can be equal if no decoration is applied.
 */
public record DecoratedContents(Text plain, Text decorated) {
	public DecoratedContents(Text content) {
		this(content, content);
	}

	public DecoratedContents(String plain, Text decorated) {
		this(Text.literal(plain), decorated);
	}

	public DecoratedContents(String content) {
		this(Text.literal(content));
	}

	public static FilteredMessage<DecoratedContents> of(FilteredMessage<String> message) {
		return message.map(DecoratedContents::new);
	}

	public static FilteredMessage<DecoratedContents> of(FilteredMessage<String> plain, FilteredMessage<Text> decorated) {
		return plain.map(
			rawMessage -> new DecoratedContents(rawMessage, decorated.raw()),
			filteredMessage -> decorated.filtered() != null ? new DecoratedContents(filteredMessage, decorated.filtered()) : null
		);
	}

	public boolean isDecorated() {
		return !this.decorated.equals(this.plain);
	}

	public static DecoratedContents read(PacketByteBuf buf) {
		Text text = buf.readText();
		Text text2 = buf.readNullable(PacketByteBuf::readText);
		return new DecoratedContents(text, (Text)Objects.requireNonNullElse(text2, text));
	}

	public static void write(PacketByteBuf buf, DecoratedContents contents) {
		buf.writeText(contents.plain());
		Text text = contents.isDecorated() ? contents.decorated() : null;
		buf.writeNullable(text, PacketByteBuf::writeText);
	}
}
