package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.book.CookingRecipeCategory;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
	private final AbstractCookingRecipe.RecipeFactory<T> recipeFactory;
	private final MapCodec<T> codec;
	private final PacketCodec<RegistryByteBuf, T> packetCodec;

	public CookingRecipeSerializer(AbstractCookingRecipe.RecipeFactory<T> recipeFactory, int cookingTime) {
		this.recipeFactory = recipeFactory;
		this.codec = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
						CookingRecipeCategory.CODEC.fieldOf("category").orElse(CookingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
						Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
						ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
						Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(recipe -> recipe.experience),
						Codec.INT.fieldOf("cookingtime").orElse(cookingTime).forGetter(recipe -> recipe.cookingTime)
					)
					.apply(instance, recipeFactory::create)
		);
		this.packetCodec = PacketCodec.ofStatic(this::write, this::read);
	}

	@Override
	public MapCodec<T> codec() {
		return this.codec;
	}

	@Override
	public PacketCodec<RegistryByteBuf, T> packetCodec() {
		return this.packetCodec;
	}

	private T read(RegistryByteBuf buf) {
		String string = buf.readString();
		CookingRecipeCategory cookingRecipeCategory = buf.readEnumConstant(CookingRecipeCategory.class);
		Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
		ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
		float f = buf.readFloat();
		int i = buf.readVarInt();
		return this.recipeFactory.create(string, cookingRecipeCategory, ingredient, itemStack, f, i);
	}

	private void write(RegistryByteBuf buf, T recipe) {
		buf.writeString(recipe.group);
		buf.writeEnumConstant(recipe.getCategory());
		Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient);
		ItemStack.PACKET_CODEC.encode(buf, recipe.result);
		buf.writeFloat(recipe.experience);
		buf.writeVarInt(recipe.cookingTime);
	}

	public AbstractCookingRecipe create(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
		return this.recipeFactory.create(group, category, ingredient, result, experience, cookingTime);
	}
}
