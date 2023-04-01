package net.minecraft;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record class_8484(float speed) implements Packet<ServerPlayPacketListener> {
	public class_8484(PacketByteBuf packetByteBuf) {
		this(packetByteBuf.readFloat());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.speed);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_50045(this);
	}
}
