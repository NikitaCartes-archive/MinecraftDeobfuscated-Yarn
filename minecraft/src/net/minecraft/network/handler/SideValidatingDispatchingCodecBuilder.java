package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class SideValidatingDispatchingCodecBuilder<B extends ByteBuf, L extends PacketListener> {
	private final PacketCodecDispatcher.Builder<B, Packet<? super L>, PacketType<? extends Packet<? super L>>> backingBuilder = PacketCodecDispatcher.builder(
		Packet::getPacketId
	);
	private final NetworkSide side;

	public SideValidatingDispatchingCodecBuilder(NetworkSide side) {
		this.side = side;
	}

	public <T extends Packet<? super L>> SideValidatingDispatchingCodecBuilder<B, L> add(PacketType<T> id, PacketCodec<? super B, T> codec) {
		if (id.side() != this.side) {
			throw new IllegalArgumentException("Invalid packet flow for packet " + id + ", expected " + this.side.name());
		} else {
			this.backingBuilder.add(id, codec);
			return this;
		}
	}

	public PacketCodec<B, Packet<? super L>> build() {
		return this.backingBuilder.build();
	}
}
