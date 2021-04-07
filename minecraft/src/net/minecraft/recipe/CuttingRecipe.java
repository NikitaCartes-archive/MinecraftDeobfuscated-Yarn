package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public abstract class CuttingRecipe implements Recipe<Inventory> {
	protected final Ingredient input;
	protected final ItemStack output;
	private final RecipeType<?> type;
	private final RecipeSerializer<?> serializer;
	protected final Identifier id;
	protected final String group;

	public CuttingRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, Identifier id, String group, Ingredient input, ItemStack output) {
		this.type = type;
		this.serializer = serializer;
		this.id = id;
		this.group = group;
		this.input = input;
		this.output = output;
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
	public Identifier getId() {
		return this.id;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.of();
		defaultedList.add(this.input);
		return defaultedList;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		return this.output.copy();
	}

	public static class Serializer<T extends CuttingRecipe> implements RecipeSerializer<T> {
		final CuttingRecipe.Serializer.RecipeFactory<T> recipeFactory;

		protected Serializer(CuttingRecipe.Serializer.RecipeFactory<T> recipeFactory) {
			this.recipeFactory = recipeFactory;
		}

		public T read(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			Ingredient ingredient;
			if (JsonHelper.hasArray(jsonObject, "ingredient")) {
				ingredient = Ingredient.fromJson(JsonHelper.getArray(jsonObject, "ingredient"));
			} else {
				ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
			}

			String string2 = JsonHelper.getString(jsonObject, "result");
			int i = JsonHelper.getInt(jsonObject, "count");
			ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(string2)), i);
			return this.recipeFactory.create(identifier, string, ingredient, itemStack);
		}

		public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString();
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			return this.recipeFactory.create(identifier, string, ingredient, itemStack);
		}

		public void write(PacketByteBuf packetByteBuf, T cuttingRecipe) {
			packetByteBuf.writeString(cuttingRecipe.group);
			cuttingRecipe.input.write(packetByteBuf);
			packetByteBuf.writeItemStack(cuttingRecipe.output);
		}

		interface RecipeFactory<T extends CuttingRecipe> {
			T create(Identifier id, String group, Ingredient input, ItemStack output);
		}
	}
}
