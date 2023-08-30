package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.CraftingRecipeCategory;

/**
 * A serializer for hardcoded recipes. The recipes with this serializer don't
 * transport any extra data besides their ID when read from JSON or synchronized
 * over network.
 * 
 * <p>The name "special" comes from the fact that in vanilla, recipes using this
 * serializer have IDs starting with {@code crafting_special_}. All of their logic and ingredients
 * are also defined in code, which distinguishes them from "non-special" recipes.
 */
public class SpecialRecipeSerializer<T extends CraftingRecipe> implements RecipeSerializer<T> {
	private final SpecialRecipeSerializer.Factory<T> factory;
	private final Codec<T> codec;

	public SpecialRecipeSerializer(SpecialRecipeSerializer.Factory<T> factory) {
		this.factory = factory;
		this.codec = RecordCodecBuilder.create(
			instance -> instance.group(CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(CraftingRecipe::getCategory))
					.apply(instance, factory::create)
		);
	}

	@Override
	public Codec<T> codec() {
		return this.codec;
	}

	public T read(PacketByteBuf packetByteBuf) {
		CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
		return this.factory.create(craftingRecipeCategory);
	}

	public void write(PacketByteBuf packetByteBuf, T craftingRecipe) {
		packetByteBuf.writeEnumConstant(craftingRecipe.getCategory());
	}

	@FunctionalInterface
	public interface Factory<T extends CraftingRecipe> {
		T create(CraftingRecipeCategory category);
	}
}
