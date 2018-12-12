package net.minecraft.recipe.smelting;

import com.google.gson.JsonObject;
import net.minecraft.block.entity.SmokerBlockEntity;
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

public class SmokingRecipe extends AbstractSmeltingRecipe {
	public SmokingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		super(identifier, string, ingredient, itemStack, f, i);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return inventory instanceof SmokerBlockEntity && this.input.matches(inventory.getInvStack(0));
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializers.field_17085;
	}

	public static class class_3863 implements RecipeSerializer<SmokingRecipe> {
		public SmokingRecipe method_16950(Identifier identifier, JsonObject jsonObject) {
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
				return new SmokingRecipe(identifier, string, ingredient, itemStack, f, i);
			} else {
				throw new IllegalStateException(string2 + " did not exist");
			}
		}

		public SmokingRecipe method_16951(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString(32767);
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			float f = packetByteBuf.readFloat();
			int i = packetByteBuf.readVarInt();
			return new SmokingRecipe(identifier, string, ingredient, itemStack, f, i);
		}

		public void method_16949(PacketByteBuf packetByteBuf, SmokingRecipe smokingRecipe) {
			packetByteBuf.writeString(smokingRecipe.group);
			smokingRecipe.input.write(packetByteBuf);
			packetByteBuf.writeItemStack(smokingRecipe.output);
			packetByteBuf.writeFloat(smokingRecipe.experience);
			packetByteBuf.writeVarInt(smokingRecipe.cookTime);
		}

		@Override
		public String getId() {
			return "smoking";
		}
	}
}
