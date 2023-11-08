package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
	private final AbstractCookingRecipe.RecipeFactory<T> recipeFactory;
	private final Codec<T> codec;

	public CookingRecipeSerializer(AbstractCookingRecipe.RecipeFactory<T> recipeFactory, int cookingTime) {
		this.recipeFactory = recipeFactory;
		this.codec = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
						CookingRecipeCategory.CODEC.fieldOf("category").orElse(CookingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
						Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
						Registries.ITEM.getCodec().xmap(ItemStack::new, ItemStack::getItem).fieldOf("result").forGetter(recipe -> recipe.result),
						Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(recipe -> recipe.experience),
						Codec.INT.fieldOf("cookingtime").orElse(cookingTime).forGetter(recipe -> recipe.cookingTime)
					)
					.apply(instance, recipeFactory::create)
		);
	}

	@Override
	public Codec<T> codec() {
		return this.codec;
	}

	public T read(PacketByteBuf packetByteBuf) {
		String string = packetByteBuf.readString();
		CookingRecipeCategory cookingRecipeCategory = packetByteBuf.readEnumConstant(CookingRecipeCategory.class);
		Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
		ItemStack itemStack = packetByteBuf.readItemStack();
		float f = packetByteBuf.readFloat();
		int i = packetByteBuf.readVarInt();
		return this.recipeFactory.create(string, cookingRecipeCategory, ingredient, itemStack, f, i);
	}

	public void write(PacketByteBuf packetByteBuf, T abstractCookingRecipe) {
		packetByteBuf.writeString(abstractCookingRecipe.group);
		packetByteBuf.writeEnumConstant(abstractCookingRecipe.getCategory());
		abstractCookingRecipe.ingredient.write(packetByteBuf);
		packetByteBuf.writeItemStack(abstractCookingRecipe.result);
		packetByteBuf.writeFloat(abstractCookingRecipe.experience);
		packetByteBuf.writeVarInt(abstractCookingRecipe.cookingTime);
	}

	public AbstractCookingRecipe create(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
		return this.recipeFactory.create(group, category, ingredient, result, experience, cookingTime);
	}
}
