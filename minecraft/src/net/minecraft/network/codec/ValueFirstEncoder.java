package net.minecraft.network.codec;

@FunctionalInterface
public interface ValueFirstEncoder<O, T> {
	void encode(T value, O buf);
}
