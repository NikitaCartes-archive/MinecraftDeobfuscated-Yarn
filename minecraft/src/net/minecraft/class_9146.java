package net.minecraft;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;

public class class_9146<B extends ByteBuf, L extends PacketListener> {
	private final class_9136.class_9137<B, Packet<? super L>, PacketIdentifier<? extends Packet<? super L>>> field_48614 = class_9136.method_56427(
		Packet::getPacketId
	);
	private final NetworkSide field_48615;

	public class_9146(NetworkSide networkSide) {
		this.field_48615 = networkSide;
	}

	public <T extends Packet<? super L>> class_9146<B, L> method_56446(PacketIdentifier<T> packetIdentifier, PacketCodec<? super B, T> packetCodec) {
		if (packetIdentifier.side() != this.field_48615) {
			throw new IllegalArgumentException("Invalid packet flow for packet " + packetIdentifier + ", expected " + this.field_48615.name());
		} else {
			this.field_48614.method_56429(packetIdentifier, packetCodec);
			return this;
		}
	}

	public PacketCodec<B, Packet<? super L>> method_56445() {
		return this.field_48614.method_56428();
	}
}
