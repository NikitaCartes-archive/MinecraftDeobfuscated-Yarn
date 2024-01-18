package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record RemoveMessageS2CPacket(MessageSignatureData.Indexed messageSignature) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, RemoveMessageS2CPacket> CODEC = Packet.createCodec(RemoveMessageS2CPacket::write, RemoveMessageS2CPacket::new);

	private RemoveMessageS2CPacket(PacketByteBuf buf) {
		this(MessageSignatureData.Indexed.fromBuf(buf));
	}

	private void write(PacketByteBuf buf) {
		MessageSignatureData.Indexed.write(buf, this.messageSignature);
	}

	@Override
	public PacketType<RemoveMessageS2CPacket> getPacketId() {
		return PlayPackets.DELETE_CHAT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRemoveMessage(this);
	}
}
