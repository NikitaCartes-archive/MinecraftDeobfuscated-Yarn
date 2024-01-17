package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;

public record ProfilelessChatMessageS2CPacket(Text message, MessageType.Serialized chatType) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ProfilelessChatMessageS2CPacket> CODEC = Packet.createCodec(
		ProfilelessChatMessageS2CPacket::write, ProfilelessChatMessageS2CPacket::new
	);

	private ProfilelessChatMessageS2CPacket(PacketByteBuf buf) {
		this(buf.readUnlimitedText(), new MessageType.Serialized(buf));
	}

	private void write(PacketByteBuf buf) {
		buf.writeText(this.message);
		this.chatType.write(buf);
	}

	@Override
	public PacketIdentifier<ProfilelessChatMessageS2CPacket> getPacketId() {
		return PlayPackets.DISGUISED_CHAT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onProfilelessChatMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
