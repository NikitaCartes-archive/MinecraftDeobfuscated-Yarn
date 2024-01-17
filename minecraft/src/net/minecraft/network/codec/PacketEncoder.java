package net.minecraft.network.codec;

@FunctionalInterface
public interface PacketEncoder<O, T> {
	void encode(O buf, T value);
}
