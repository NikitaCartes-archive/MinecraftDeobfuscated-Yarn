package net.minecraft.entity.data;

import net.minecraft.network.PacketByteBuf;

public interface TrackedDataHandler<T> {
	void write(PacketByteBuf buf, T value);

	T read(PacketByteBuf buf);

	default TrackedData<T> create(int i) {
		return new TrackedData<>(i, this);
	}

	T copy(T value);
}
