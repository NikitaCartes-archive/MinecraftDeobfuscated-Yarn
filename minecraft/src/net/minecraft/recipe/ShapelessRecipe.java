package net.minecraft.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapelessRecipe implements CraftingRecipe {
	private final Identifier id;
	private final String group;
	private final ItemStack output;
	private final DefaultedList<Ingredient> input;

	public ShapelessRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
		this.id = id;
		this.group = group;
		this.output = output;
		this.input = input;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SHAPELESS;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		return this.input;
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		RecipeFinder recipeFinder = new RecipeFinder();
		int i = 0;

		for (int j = 0; j < craftingInventory.size(); j++) {
			ItemStack itemStack = craftingInventory.getStack(j);
			if (!itemStack.isEmpty()) {
				i++;
				recipeFinder.addItem(itemStack, 1);
			}
		}

		return i == this.input.size() && recipeFinder.findRecipe(this, null);
	}

	public ItemStack craft(CraftingInventory craftingInventory) {
		return this.output.copy();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int width, int height) {
		return width * height >= this.input.size();
	}

	public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
		public ShapelessRecipe read(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			DefaultedList<Ingredient> defaultedList = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
			if (defaultedList.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else if (defaultedList.size() > 9) {
				throw new JsonParseException("Too many ingredients for shapeless recipe");
			} else {
				ItemStack itemStack = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
				return new ShapelessRecipe(identifier, string, itemStack, defaultedList);
			}
		}

		private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
			DefaultedList<Ingredient> defaultedList = DefaultedList.of();

			for (int i = 0; i < json.size(); i++) {
				Ingredient ingredient = Ingredient.fromJson(json.get(i));
				if (!ingredient.isEmpty()) {
					defaultedList.add(ingredient);
				}
			}

			return defaultedList;
		}

		public ShapelessRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString(32767);
			int i = packetByteBuf.readVarInt();
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);

			for (int j = 0; j < defaultedList.size(); j++) {
				defaultedList.set(j, Ingredient.fromPacket(packetByteBuf));
			}

			ItemStack itemStack = packetByteBuf.readItemStack();
			return new ShapelessRecipe(identifier, string, itemStack, defaultedList);
		}

		public void write(PacketByteBuf packetByteBuf, ShapelessRecipe shapelessRecipe) {
			packetByteBuf.writeString(shapelessRecipe.group);
			packetByteBuf.writeVarInt(shapelessRecipe.input.size());

			for (Ingredient ingredient : shapelessRecipe.input) {
				ingredient.write(packetByteBuf);
			}

			packetByteBuf.writeItemStack(shapelessRecipe.output);
		}
	}
}
