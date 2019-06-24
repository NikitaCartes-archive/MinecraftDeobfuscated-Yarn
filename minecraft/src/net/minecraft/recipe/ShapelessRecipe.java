package net.minecraft.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class ShapelessRecipe implements CraftingRecipe {
	private final Identifier id;
	private final String group;
	private final ItemStack output;
	private final DefaultedList<Ingredient> input;

	public ShapelessRecipe(Identifier identifier, String string, ItemStack itemStack, DefaultedList<Ingredient> defaultedList) {
		this.id = identifier;
		this.group = string;
		this.output = itemStack;
		this.input = defaultedList;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.CRAFTING_SHAPELESS;
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

	public boolean method_17730(CraftingInventory craftingInventory, World world) {
		RecipeFinder recipeFinder = new RecipeFinder();
		int i = 0;

		for (int j = 0; j < craftingInventory.getInvSize(); j++) {
			ItemStack itemStack = craftingInventory.getInvStack(j);
			if (!itemStack.isEmpty()) {
				i++;
				recipeFinder.method_20478(itemStack, 1);
			}
		}

		return i == this.input.size() && recipeFinder.findRecipe(this, null);
	}

	public ItemStack method_17729(CraftingInventory craftingInventory) {
		return this.output.copy();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= this.input.size();
	}

	public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
		public ShapelessRecipe method_8142(Identifier identifier, JsonObject jsonObject) {
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

		private static DefaultedList<Ingredient> getIngredients(JsonArray jsonArray) {
			DefaultedList<Ingredient> defaultedList = DefaultedList.of();

			for (int i = 0; i < jsonArray.size(); i++) {
				Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
				if (!ingredient.isEmpty()) {
					defaultedList.add(ingredient);
				}
			}

			return defaultedList;
		}

		public ShapelessRecipe method_8141(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString(32767);
			int i = packetByteBuf.readVarInt();
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);

			for (int j = 0; j < defaultedList.size(); j++) {
				defaultedList.set(j, Ingredient.fromPacket(packetByteBuf));
			}

			ItemStack itemStack = packetByteBuf.readItemStack();
			return new ShapelessRecipe(identifier, string, itemStack, defaultedList);
		}

		public void method_8143(PacketByteBuf packetByteBuf, ShapelessRecipe shapelessRecipe) {
			packetByteBuf.writeString(shapelessRecipe.group);
			packetByteBuf.writeVarInt(shapelessRecipe.input.size());

			for (Ingredient ingredient : shapelessRecipe.input) {
				ingredient.write(packetByteBuf);
			}

			packetByteBuf.writeItemStack(shapelessRecipe.output);
		}
	}
}
