package net.minecraft;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public record class_7519(boolean enabled) implements Packet<ClientPlayPacketListener> {
	public class_7519(PacketByteBuf packetByteBuf) {
		this(packetByteBuf.readBoolean());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.enabled);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_44286(this);
	}
}
