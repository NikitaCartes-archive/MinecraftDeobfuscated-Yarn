package net.minecraft.network.codec;

/**
 * A functional interface that, given a buffer and a value, encodes it.
 * 
 * <p>A static method taking {@link net.minecraft.network.PacketByteBuf} and the
 * value as the arguments can be used as an encoder.
 * 
 * @see PacketDecoder
 * @see ValueFirstEncoder
 */
@FunctionalInterface
public interface PacketEncoder<O, T> {
	void encode(O buf, T value);
}
