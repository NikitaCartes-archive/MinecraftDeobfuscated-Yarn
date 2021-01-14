package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class SmithingRecipe implements Recipe<Inventory> {
	private final Ingredient base;
	private final Ingredient addition;
	private final ItemStack result;
	private final Identifier id;

	public SmithingRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
		this.id = id;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.base.test(inventory.getStack(0)) && this.addition.test(inventory.getStack(1));
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		ItemStack itemStack = this.result.copy();
		NbtCompound nbtCompound = inventory.getStack(0).getTag();
		if (nbtCompound != null) {
			itemStack.setTag(nbtCompound.copy());
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
		return this.result;
	}

	public boolean method_30029(ItemStack itemStack) {
		return this.addition.test(itemStack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack createIcon() {
		return new ItemStack(Blocks.SMITHING_TABLE);
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMITHING;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeType.SMITHING;
	}

	public static class Serializer implements RecipeSerializer<SmithingRecipe> {
		public SmithingRecipe read(Identifier identifier, JsonObject jsonObject) {
			Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
			Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
			ItemStack itemStack = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
			return new SmithingRecipe(identifier, ingredient, ingredient2, itemStack);
		}

		public SmithingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			return new SmithingRecipe(identifier, ingredient, ingredient2, itemStack);
		}

		public void write(PacketByteBuf packetByteBuf, SmithingRecipe smithingRecipe) {
			smithingRecipe.base.write(packetByteBuf);
			smithingRecipe.addition.write(packetByteBuf);
			packetByteBuf.writeItemStack(smithingRecipe.result);
		}
	}
}
