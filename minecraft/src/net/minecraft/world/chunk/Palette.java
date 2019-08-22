package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.PacketByteBuf;

public interface Palette<T> {
	int getIndex(T object);

	boolean accepts(T object);

	@Nullable
	T getByIndex(int i);

	@Environment(EnvType.CLIENT)
	void fromPacket(PacketByteBuf packetByteBuf);

	void toPacket(PacketByteBuf packetByteBuf);

	int getPacketSize();

	void fromTag(ListTag listTag);
}
