package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.util.PacketByteBuf;

public interface ArgumentSerializer<T extends ArgumentType<?>> {
	void method_10007(T argumentType, PacketByteBuf packetByteBuf);

	T method_10005(PacketByteBuf packetByteBuf);

	void toJson(T argumentType, JsonObject jsonObject);
}
