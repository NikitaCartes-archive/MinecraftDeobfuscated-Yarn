package net.minecraft.entity.data;

import net.minecraft.util.PacketByteBuf;

public interface TrackedDataHandler<T> {
	void write(PacketByteBuf packetByteBuf, T object);

	T read(PacketByteBuf packetByteBuf);

	TrackedData<T> create(int i);

	T copy(T object);
}
