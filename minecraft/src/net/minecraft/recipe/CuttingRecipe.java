package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
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
	public ItemStack getResult(DynamicRegistryManager registryManager) {
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
	public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
		return this.result.copy();
	}

	public static class Serializer<T extends CuttingRecipe> implements RecipeSerializer<T> {
		private static final MapCodec<ItemStack> RESULT_STACK_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Registries.ITEM.getCodec().fieldOf("result").forGetter(ItemStack::getItem), Codec.INT.fieldOf("count").forGetter(ItemStack::getCount)
					)
					.apply(instance, ItemStack::new)
		);
		final CuttingRecipe.Serializer.RecipeFactory<T> recipeFactory;
		private final Codec<T> codec;

		protected Serializer(CuttingRecipe.Serializer.RecipeFactory<T> recipeFactory) {
			this.recipeFactory = recipeFactory;
			this.codec = RecordCodecBuilder.create(
				instance -> instance.group(
							Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
							Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
							RESULT_STACK_CODEC.forGetter(recipe -> recipe.result)
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
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			return this.recipeFactory.create(string, ingredient, itemStack);
		}

		public void write(PacketByteBuf packetByteBuf, T cuttingRecipe) {
			packetByteBuf.writeString(cuttingRecipe.group);
			cuttingRecipe.ingredient.write(packetByteBuf);
			packetByteBuf.writeItemStack(cuttingRecipe.result);
		}

		interface RecipeFactory<T extends CuttingRecipe> {
			T create(String group, Ingredient ingredient, ItemStack result);
		}
	}
}
