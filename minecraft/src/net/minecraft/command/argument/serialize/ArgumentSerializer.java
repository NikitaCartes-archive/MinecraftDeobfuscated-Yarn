package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;

/**
 * Serializes an argument type to be sent to the client.
 */
public interface ArgumentSerializer<A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> {
	void writePacket(T properties, PacketByteBuf buf);

	T fromPacket(PacketByteBuf buf);

	void writeJson(T properties, JsonObject json);

	T getArgumentTypeProperties(A argumentType);

	public interface ArgumentTypeProperties<A extends ArgumentType<?>> {
		A createType(CommandRegistryAccess commandRegistryAccess);

		ArgumentSerializer<A, ?> getSerializer();
	}
}
