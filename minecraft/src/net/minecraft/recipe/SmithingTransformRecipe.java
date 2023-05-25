package net.minecraft.recipe;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class SmithingTransformRecipe implements SmithingRecipe {
	private final Identifier id;
	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;
	final ItemStack result;

	public SmithingTransformRecipe(Identifier id, Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
		this.id = id;
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.template.test(inventory.getStack(0)) && this.base.test(inventory.getStack(1)) && this.addition.test(inventory.getStack(2));
	}

	@Override
	public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
		ItemStack itemStack = this.result.copy();
		NbtCompound nbtCompound = inventory.getStack(1).getNbt();
		if (nbtCompound != null) {
			itemStack.setNbt(nbtCompound.copy());
		}

		return itemStack;
	}

	@Override
	public ItemStack getOutput(DynamicRegistryManager registryManager) {
		return this.result;
	}

	@Override
	public boolean testTemplate(ItemStack stack) {
		return this.template.test(stack);
	}

	@Override
	public boolean testBase(ItemStack stack) {
		return this.base.test(stack);
	}

	@Override
	public boolean testAddition(ItemStack stack) {
		return this.addition.test(stack);
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMITHING_TRANSFORM;
	}

	@Override
	public boolean isEmpty() {
		return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
	}

	public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
		public SmithingTransformRecipe read(Identifier identifier, JsonObject jsonObject) {
			Ingredient ingredient = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "template"));
			Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "base"));
			Ingredient ingredient3 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "addition"));
			ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
			return new SmithingTransformRecipe(identifier, ingredient, ingredient2, ingredient3, itemStack);
		}

		public SmithingTransformRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
			Ingredient ingredient3 = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			return new SmithingTransformRecipe(identifier, ingredient, ingredient2, ingredient3, itemStack);
		}

		public void write(PacketByteBuf packetByteBuf, SmithingTransformRecipe smithingTransformRecipe) {
			smithingTransformRecipe.template.write(packetByteBuf);
			smithingTransformRecipe.base.write(packetByteBuf);
			smithingTransformRecipe.addition.write(packetByteBuf);
			packetByteBuf.writeItemStack(smithingTransformRecipe.result);
		}
	}
}
