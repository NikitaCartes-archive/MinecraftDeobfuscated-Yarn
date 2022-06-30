package net.minecraft.network.message;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/**
 * The sender, or the source, of a message.
 * 
 * <p>An instance can be obtained via {@link net.minecraft.entity.Entity#asMessageSender}.
 */
public record MessageSender(UUID profileId, Text name, @Nullable Text teamName) {
	public MessageSender(UUID profileId, Text name) {
		this(profileId, name, null);
	}

	public MessageSender(PacketByteBuf buf) {
		this(buf.readUuid(), buf.readText(), buf.readNullable(PacketByteBuf::readText));
	}

	public static MessageSender of(Text name) {
		return new MessageSender(Util.NIL_UUID, name);
	}

	public void write(PacketByteBuf buf) {
		buf.writeUuid(this.profileId);
		buf.writeText(this.name);
		buf.writeNullable(this.teamName, PacketByteBuf::writeText);
	}

	public MessageSender withTeamName(Text teamName) {
		return new MessageSender(this.profileId, this.name, teamName);
	}

	public boolean hasProfileId() {
		return !this.profileId.equals(Util.NIL_UUID);
	}
}
