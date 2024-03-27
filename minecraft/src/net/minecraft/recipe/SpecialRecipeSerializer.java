package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
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
	private final MapCodec<T> codec;
	private final PacketCodec<RegistryByteBuf, T> packetCodec;

	public SpecialRecipeSerializer(SpecialRecipeSerializer.Factory<T> factory) {
		this.codec = RecordCodecBuilder.mapCodec(
			instance -> instance.group(CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(CraftingRecipe::getCategory))
					.apply(instance, factory::create)
		);
		this.packetCodec = PacketCodec.tuple(CraftingRecipeCategory.PACKET_CODEC, CraftingRecipe::getCategory, factory::create);
	}

	@Override
	public MapCodec<T> codec() {
		return this.codec;
	}

	@Override
	public PacketCodec<RegistryByteBuf, T> packetCodec() {
		return this.packetCodec;
	}

	@FunctionalInterface
	public interface Factory<T extends CraftingRecipe> {
		T create(CraftingRecipeCategory category);
	}
}
