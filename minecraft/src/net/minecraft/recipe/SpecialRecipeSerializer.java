package net.minecraft.recipe;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class SpecialRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
	private final Function<Identifier, T> id;

	public SpecialRecipeSerializer(Function<Identifier, T> function) {
		this.id = function;
	}

	@Override
	public T method_8121(Identifier identifier, JsonObject jsonObject) {
		return (T)this.id.apply(identifier);
	}

	@Override
	public T method_8122(Identifier identifier, PacketByteBuf packetByteBuf) {
		return (T)this.id.apply(identifier);
	}

	@Override
	public void method_8124(PacketByteBuf packetByteBuf, T recipe) {
	}
}
