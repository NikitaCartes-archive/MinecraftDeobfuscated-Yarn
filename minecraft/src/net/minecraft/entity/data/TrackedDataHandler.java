package net.minecraft.entity.data;

import net.minecraft.util.PacketByteBuf;

public interface TrackedDataHandler<T> {
	void write(PacketByteBuf packetByteBuf, T object);

	T read(PacketByteBuf packetByteBuf);

	default TrackedData<T> create(int i) {
		return new TrackedData<>(i, this);
	}

	T copy(T object);
}
