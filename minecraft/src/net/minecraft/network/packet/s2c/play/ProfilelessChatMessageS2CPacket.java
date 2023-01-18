package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public record ProfilelessChatMessageS2CPacket(Text message, MessageType.Serialized chatType) implements Packet<ClientPlayPacketListener> {
	public ProfilelessChatMessageS2CPacket(PacketByteBuf buf) {
		this(buf.readText(), new MessageType.Serialized(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.message);
		this.chatType.write(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onProfilelessChatMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
