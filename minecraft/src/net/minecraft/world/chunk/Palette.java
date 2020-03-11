package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;

public interface Palette<T> {
	int getIndex(T object);

	boolean accepts(T object);

	@Nullable
	T getByIndex(int index);

	@Environment(EnvType.CLIENT)
	void fromPacket(PacketByteBuf buf);

	void toPacket(PacketByteBuf buf);

	int getPacketSize();

	void fromTag(ListTag tag);
}
