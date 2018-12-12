package net.minecraft.recipe.smelting;

import com.google.gson.JsonObject;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class BlastingRecipe extends AbstractSmeltingRecipe {
	public BlastingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		super(identifier, string, ingredient, itemStack, f, i);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return inventory instanceof BlastFurnaceBlockEntity && this.input.matches(inventory.getInvStack(0));
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializers.field_17084;
	}

	public static class class_3860 implements RecipeSerializer<BlastingRecipe> {
		public BlastingRecipe method_16947(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			Ingredient ingredient;
			if (JsonHelper.hasArray(jsonObject, "ingredient")) {
				ingredient = Ingredient.fromJson(JsonHelper.getArray(jsonObject, "ingredient"));
			} else {
				ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
			}

			String string2 = JsonHelper.getString(jsonObject, "result");
			Item item = Registry.ITEM.get(new Identifier(string2));
			if (item != null) {
				ItemStack itemStack = new ItemStack(item);
				float f = JsonHelper.getFloat(jsonObject, "experience", 0.0F);
				int i = JsonHelper.getInt(jsonObject, "cookingtime", 100);
				return new BlastingRecipe(identifier, string, ingredient, itemStack, f, i);
			} else {
				throw new IllegalStateException(string2 + " did not exist");
			}
		}

		public BlastingRecipe method_16948(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString(32767);
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			float f = packetByteBuf.readFloat();
			int i = packetByteBuf.readVarInt();
			return new BlastingRecipe(identifier, string, ingredient, itemStack, f, i);
		}

		public void method_16946(PacketByteBuf packetByteBuf, BlastingRecipe blastingRecipe) {
			packetByteBuf.writeString(blastingRecipe.group);
			blastingRecipe.input.write(packetByteBuf);
			packetByteBuf.writeItemStack(blastingRecipe.output);
			packetByteBuf.writeFloat(blastingRecipe.experience);
			packetByteBuf.writeVarInt(blastingRecipe.cookTime);
		}

		@Override
		public String getId() {
			return "blasting";
		}
	}
}
