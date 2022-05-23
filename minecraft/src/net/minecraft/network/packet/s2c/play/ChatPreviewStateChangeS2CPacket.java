package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public record ChatPreviewStateChangeS2CPacket(boolean enabled) implements Packet<ClientPlayPacketListener> {
	public ChatPreviewStateChangeS2CPacket(PacketByteBuf buf) {
		this(buf.readBoolean());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.enabled);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatPreviewStateChange(this);
	}
}
