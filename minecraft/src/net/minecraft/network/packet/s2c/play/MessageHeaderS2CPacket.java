package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;

public record MessageHeaderS2CPacket(MessageHeader header, MessageSignatureData headerSignature, byte[] bodyDigest) implements Packet<ClientPlayPacketListener> {
	public MessageHeaderS2CPacket(SignedMessage message) {
		this(message.signedHeader(), message.headerSignature(), message.signedBody().digest().asBytes());
	}

	public MessageHeaderS2CPacket(PacketByteBuf buf) {
		this(new MessageHeader(buf), new MessageSignatureData(buf), buf.readByteArray());
	}

	@Override
	public void write(PacketByteBuf buf) {
		this.header.write(buf);
		this.headerSignature.write(buf);
		buf.writeByteArray(this.bodyDigest);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMessageHeader(this);
	}
}
