package net.minecraft.recipe;

import com.google.gson.JsonObject;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.item.trim.ArmorTrimPatterns;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class SmithingTrimRecipe implements SmithingRecipe {
	private final Identifier id;
	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;

	public SmithingTrimRecipe(Identifier id, Ingredient template, Ingredient base, Ingredient addition) {
		this.id = id;
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.template.test(inventory.getStack(0)) && this.base.test(inventory.getStack(1)) && this.addition.test(inventory.getStack(2));
	}

	@Override
	public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
		ItemStack itemStack = inventory.getStack(1);
		if (this.base.test(itemStack)) {
			Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional = ArmorTrimMaterials.get(registryManager, inventory.getStack(2));
			Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional2 = ArmorTrimPatterns.get(registryManager, inventory.getStack(0));
			if (optional.isPresent() && optional2.isPresent()) {
				Optional<ArmorTrim> optional3 = ArmorTrim.getTrim(registryManager, itemStack);
				if (optional3.isPresent()
					&& ((ArmorTrim)optional3.get()).equals((RegistryEntry<ArmorTrimPattern>)optional2.get(), (RegistryEntry<ArmorTrimMaterial>)optional.get())) {
					return ItemStack.EMPTY;
				}

				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setCount(1);
				ArmorTrim armorTrim = new ArmorTrim((RegistryEntry<ArmorTrimMaterial>)optional.get(), (RegistryEntry<ArmorTrimPattern>)optional2.get());
				if (ArmorTrim.apply(registryManager, itemStack2, armorTrim)) {
					return itemStack2;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getOutput(DynamicRegistryManager registryManager) {
		ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
		Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional = registryManager.get(RegistryKeys.TRIM_PATTERN).streamEntries().findFirst();
		if (optional.isPresent()) {
			Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional2 = registryManager.get(RegistryKeys.TRIM_MATERIAL).getEntry(ArmorTrimMaterials.REDSTONE);
			if (optional2.isPresent()) {
				ArmorTrim armorTrim = new ArmorTrim((RegistryEntry<ArmorTrimMaterial>)optional2.get(), (RegistryEntry<ArmorTrimPattern>)optional.get());
				ArmorTrim.apply(registryManager, itemStack, armorTrim);
			}
		}

		return itemStack;
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
		return RecipeSerializer.SMITHING_TRIM;
	}

	@Override
	public boolean isEmpty() {
		return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
	}

	public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
		public SmithingTrimRecipe read(Identifier identifier, JsonObject jsonObject) {
			Ingredient ingredient = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "template"));
			Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "base"));
			Ingredient ingredient3 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "addition"));
			return new SmithingTrimRecipe(identifier, ingredient, ingredient2, ingredient3);
		}

		public SmithingTrimRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
			Ingredient ingredient3 = Ingredient.fromPacket(packetByteBuf);
			return new SmithingTrimRecipe(identifier, ingredient, ingredient2, ingredient3);
		}

		public void write(PacketByteBuf packetByteBuf, SmithingTrimRecipe smithingTrimRecipe) {
			smithingTrimRecipe.template.write(packetByteBuf);
			smithingTrimRecipe.base.write(packetByteBuf);
			smithingTrimRecipe.addition.write(packetByteBuf);
		}
	}
}
