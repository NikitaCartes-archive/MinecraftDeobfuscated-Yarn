package net.minecraft.recipe.smelting;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class SmeltingRecipe implements Recipe {
	private final Identifier id;
	private final String group;
	private final Ingredient input;
	private final ItemStack output;
	private final float experience;
	private final int cookTime;

	public SmeltingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		this.id = identifier;
		this.group = string;
		this.input = ingredient;
		this.output = itemStack;
		this.experience = f;
		this.cookTime = i;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return inventory instanceof FurnaceBlockEntity && this.input.matches(inventory.getInvStack(0));
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		return this.output.copy();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializers.SMELTING;
	}

	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.create();
		defaultedList.add(this.input);
		return defaultedList;
	}

	public float getExperience() {
		return this.experience;
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String getGroup() {
		return this.group;
	}

	public int getCookTime() {
		return this.cookTime;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public static class Serializer implements RecipeSerializer<SmeltingRecipe> {
		public SmeltingRecipe method_8174(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			Ingredient ingredient;
			if (JsonHelper.isArray(jsonObject, "ingredient")) {
				ingredient = Ingredient.fromJson(JsonHelper.getArray(jsonObject, "ingredient"));
			} else {
				ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
			}

			String string2 = JsonHelper.getString(jsonObject, "result");
			Item item = Registry.ITEM.get(new Identifier(string2));
			if (item != null) {
				ItemStack itemStack = new ItemStack(item);
				float f = JsonHelper.getFloat(jsonObject, "experience", 0.0F);
				int i = JsonHelper.getInt(jsonObject, "cookingtime", 200);
				return new SmeltingRecipe(identifier, string, ingredient, itemStack, f, i);
			} else {
				throw new IllegalStateException(string2 + " did not exist");
			}
		}

		public SmeltingRecipe method_8175(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString(32767);
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			float f = packetByteBuf.readFloat();
			int i = packetByteBuf.readVarInt();
			return new SmeltingRecipe(identifier, string, ingredient, itemStack, f, i);
		}

		public void method_8173(PacketByteBuf packetByteBuf, SmeltingRecipe smeltingRecipe) {
			packetByteBuf.writeString(smeltingRecipe.group);
			smeltingRecipe.input.write(packetByteBuf);
			packetByteBuf.writeItemStack(smeltingRecipe.output);
			packetByteBuf.writeFloat(smeltingRecipe.experience);
			packetByteBuf.writeVarInt(smeltingRecipe.cookTime);
		}

		@Override
		public String getId() {
			return "smelting";
		}
	}
}
