package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageSignatureData;

public record HideMessageS2CPacket(MessageSignatureData messageSignature) implements Packet<ClientPlayPacketListener> {
	public HideMessageS2CPacket(PacketByteBuf buf) {
		this(new MessageSignatureData(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		this.messageSignature.write(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onHideMessage(this);
	}
}
