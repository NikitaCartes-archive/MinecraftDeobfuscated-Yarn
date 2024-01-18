package net.minecraft.network.codec;

/**
 * A functional interface that, given a value and a buffer, encodes it.
 * 
 * <p>An instance method taking {@link net.minecraft.network.PacketByteBuf} as an
 * argument can be used as a value-first encoder.
 * 
 * @see PacketDecoder
 * @see PacketEncoder
 */
@FunctionalInterface
public interface ValueFirstEncoder<O, T> {
	void encode(T value, O buf);
}
