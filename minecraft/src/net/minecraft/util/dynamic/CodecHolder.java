package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record CodecHolder<A>(Codec<A> codec) {
	public static <A> CodecHolder<A> of(Codec<A> codec) {
		return new CodecHolder<>(codec);
	}

	public static <A> CodecHolder<A> of(MapCodec<A> mapCodec) {
		return new CodecHolder<>(mapCodec.codec());
	}
}
