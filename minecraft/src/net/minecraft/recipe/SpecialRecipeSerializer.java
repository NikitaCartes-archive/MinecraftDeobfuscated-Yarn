package net.minecraft.recipe;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SpecialRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
	private final Function<Identifier, T> id;

	public SpecialRecipeSerializer(Function<Identifier, T> id) {
		this.id = id;
	}

	@Override
	public T read(Identifier id, JsonObject json) {
		return (T)this.id.apply(id);
	}

	@Override
	public T read(Identifier id, PacketByteBuf buf) {
		return (T)this.id.apply(id);
	}

	@Override
	public void write(PacketByteBuf buf, T recipe) {
	}
}
