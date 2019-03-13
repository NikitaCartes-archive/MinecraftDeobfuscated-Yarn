package net.minecraft.util.palette;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.PacketByteBuf;

public interface Palette<T> {
	int getIndex(T object);

	@Nullable
	T getByIndex(int i);

	@Environment(EnvType.CLIENT)
	void method_12289(PacketByteBuf packetByteBuf);

	void method_12287(PacketByteBuf packetByteBuf);

	int getPacketSize();

	void method_12286(ListTag listTag);
}
