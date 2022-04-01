package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.PacketByteBuf;

/**
 * Serializes an argument type to be sent to the client.
 */
public interface ArgumentSerializer<T extends ArgumentType<?>> {
	void toPacket(T type, PacketByteBuf buf);

	T fromPacket(PacketByteBuf buf);

	void toJson(T type, JsonObject json);
}
