package net.minecraft.network.packet.c2s.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record ClientOptionsC2SPacket(SyncedClientOptions options) implements Packet<ServerCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, ClientOptionsC2SPacket> CODEC = Packet.createCodec(ClientOptionsC2SPacket::write, ClientOptionsC2SPacket::new);

	private ClientOptionsC2SPacket(PacketByteBuf buf) {
		this(new SyncedClientOptions(buf));
	}

	private void write(PacketByteBuf buf) {
		this.options.write(buf);
	}

	@Override
	public PacketType<ClientOptionsC2SPacket> getPacketId() {
		return CommonPackets.CLIENT_INFORMATION;
	}

	public void apply(ServerCommonPacketListener serverCommonPacketListener) {
		serverCommonPacketListener.onClientOptions(this);
	}
}
