package net.minecraft.entity.data;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface TrackedDataHandler<T> {
	PacketCodec<? super RegistryByteBuf, T> codec();

	default TrackedData<T> create(int id) {
		return new TrackedData<>(id, this);
	}

	T copy(T value);

	static <T> TrackedDataHandler<T> create(PacketCodec<? super RegistryByteBuf, T> codec) {
		return () -> codec;
	}

	public interface ImmutableHandler<T> extends TrackedDataHandler<T> {
		@Override
		default T copy(T object) {
			return object;
		}
	}
}
