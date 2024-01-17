package net.minecraft.network.codec;

@FunctionalInterface
public interface PacketDecoder<I, T> {
	T decode(I buf);
}
