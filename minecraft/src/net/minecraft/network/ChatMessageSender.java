package net.minecraft.network;

import java.util.UUID;
import net.minecraft.text.Text;

/**
 * The sender, or the source, of a chat message.
 * 
 * <p>An instance can be obtained via
 * {@link net.minecraft.entity.player.PlayerEntity#asChatMessageSender}.
 */
public record ChatMessageSender(UUID uuid, Text name) {
	public ChatMessageSender(PacketByteBuf buf) {
		this(buf.readUuid(), buf.readText());
	}

	public void write(PacketByteBuf buf) {
		buf.writeUuid(this.uuid);
		buf.writeText(this.name);
	}
}
