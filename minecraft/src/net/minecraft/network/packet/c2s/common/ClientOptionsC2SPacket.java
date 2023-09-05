package net.minecraft.network.packet.c2s.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.Packet;

public record ClientOptionsC2SPacket(SyncedClientOptions options) implements Packet<ServerCommonPacketListener> {
	public ClientOptionsC2SPacket(PacketByteBuf buf) {
		this(new SyncedClientOptions(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		this.options.write(buf);
	}

	public void apply(ServerCommonPacketListener serverCommonPacketListener) {
		serverCommonPacketListener.onClientOptions(this);
	}
}
