package net.minecraft.recipe;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * A serializer for hardcoded recipes. The recipes with this serializer don't
 * transport any extra data besides their ID when read from JSON or synchronized
 * over network.
 * 
 * <p>The name "special" comes from the fact that in vanilla, recipes using this
 * serializer have IDs starting with {@code crafting_special_}. All of their logic and ingredients
 * are also defined in code, which distinguishes them from "non-special" recipes.
 */
public class SpecialRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
	private final Function<Identifier, T> factory;

	public SpecialRecipeSerializer(Function<Identifier, T> factory) {
		this.factory = factory;
	}

	@Override
	public T read(Identifier id, JsonObject json) {
		return (T)this.factory.apply(id);
	}

	@Override
	public T read(Identifier id, PacketByteBuf buf) {
		return (T)this.factory.apply(id);
	}

	@Override
	public void write(PacketByteBuf buf, T recipe) {
	}
}
