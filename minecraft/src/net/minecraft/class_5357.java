package net.minecraft;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class class_5357 implements Recipe<Inventory> {
	private final Ingredient field_25389;
	private final Ingredient field_25390;
	private final ItemStack field_25391;
	private final Identifier field_25392;

	public class_5357(Identifier identifier, Ingredient ingredient, Ingredient ingredient2, ItemStack itemStack) {
		this.field_25392 = identifier;
		this.field_25389 = ingredient;
		this.field_25390 = ingredient2;
		this.field_25391 = itemStack;
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return this.field_25389.test(inv.getStack(0)) && this.field_25390.test(inv.getStack(1));
	}

	@Override
	public ItemStack craft(Inventory inv) {
		ItemStack itemStack = this.field_25391.copy();
		CompoundTag compoundTag = inv.getStack(0).getTag();
		if (compoundTag != null) {
			itemStack.setTag(compoundTag.copy());
		}

		return itemStack;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getOutput() {
		return this.field_25391;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.SMITHING_TABLE);
	}

	@Override
	public Identifier getId() {
		return this.field_25392;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMITHING;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeType.SMITHING;
	}

	public static class class_5358 implements RecipeSerializer<class_5357> {
		public class_5357 read(Identifier identifier, JsonObject jsonObject) {
			Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
			Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
			ItemStack itemStack = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
			return new class_5357(identifier, ingredient, ingredient2, itemStack);
		}

		public class_5357 read(Identifier identifier, PacketByteBuf packetByteBuf) {
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			return new class_5357(identifier, ingredient, ingredient2, itemStack);
		}

		public void write(PacketByteBuf packetByteBuf, class_5357 arg) {
			arg.field_25389.write(packetByteBuf);
			arg.field_25390.write(packetByteBuf);
			packetByteBuf.writeItemStack(arg.field_25391);
		}
	}
}
