package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;

/**
 * A recipe that has only one input ingredient. It can be used by any type
 * of recipe as long as its subclass implements the proper interface.
 */
public abstract class CuttingRecipe implements Recipe<Inventory> {
	protected final Ingredient ingredient;
	protected final ItemStack result;
	private final RecipeType<?> type;
	private final RecipeSerializer<?> serializer;
	protected final String group;

	public CuttingRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, String group, Ingredient ingredient, ItemStack result) {
		this.type = type;
		this.serializer = serializer;
		this.group = group;
		this.ingredient = ingredient;
		this.result = result;
	}

	@Override
	public RecipeType<?> getType() {
		return this.type;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return this.serializer;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
		return this.result;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.of();
		defaultedList.add(this.ingredient);
		return defaultedList;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack craft(Inventory inventory, RegistryWrapper.WrapperLookup lookup) {
		return this.result.copy();
	}

	public interface RecipeFactory<T extends CuttingRecipe> {
		T create(String group, Ingredient ingredient, ItemStack result);
	}

	public static class Serializer<T extends CuttingRecipe> implements RecipeSerializer<T> {
		final CuttingRecipe.RecipeFactory<T> recipeFactory;
		private final Codec<T> codec;
		private final PacketCodec<RegistryByteBuf, T> packetCodec;

		protected Serializer(CuttingRecipe.RecipeFactory<T> recipeFactory) {
			this.recipeFactory = recipeFactory;
			this.codec = RecordCodecBuilder.create(
				instance -> instance.group(
							Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
							Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
							ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
						)
						.apply(instance, recipeFactory::create)
			);
			this.packetCodec = PacketCodec.tuple(
				PacketCodecs.STRING,
				recipe -> recipe.group,
				Ingredient.PACKET_CODEC,
				recipe -> recipe.ingredient,
				ItemStack.PACKET_CODEC,
				recipe -> recipe.result,
				recipeFactory::create
			);
		}

		@Override
		public Codec<T> codec() {
			return this.codec;
		}

		@Override
		public PacketCodec<RegistryByteBuf, T> packetCodec() {
			return this.packetCodec;
		}
	}
}
