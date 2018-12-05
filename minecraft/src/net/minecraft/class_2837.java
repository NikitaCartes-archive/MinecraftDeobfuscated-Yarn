package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.PacketByteBuf;

public interface class_2837<T> {
	int method_12291(T object);

	@Nullable
	T method_12288(int i);

	@Environment(EnvType.CLIENT)
	void method_12289(PacketByteBuf packetByteBuf);

	void method_12287(PacketByteBuf packetByteBuf);

	int method_12290();

	void method_12286(ListTag listTag);
}
