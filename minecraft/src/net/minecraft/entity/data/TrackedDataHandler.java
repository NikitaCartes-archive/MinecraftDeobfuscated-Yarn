package net.minecraft.entity.data;

import net.minecraft.network.PacketByteBuf;

public interface TrackedDataHandler<T> {
	void write(PacketByteBuf data, T object);

	T read(PacketByteBuf packetByteBuf);

	default TrackedData<T> create(int i) {
		return new TrackedData<>(i, this);
	}

	T copy(T object);
}
