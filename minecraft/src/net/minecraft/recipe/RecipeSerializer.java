package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public interface RecipeSerializer<T extends Recipe> {
	T read(Identifier identifier, JsonObject jsonObject);

	T read(Identifier identifier, PacketByteBuf packetByteBuf);

	void write(PacketByteBuf packetByteBuf, T recipe);

	String getId();
}
