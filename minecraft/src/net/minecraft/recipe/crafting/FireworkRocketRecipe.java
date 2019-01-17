package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FireworkRocketRecipe extends SpecialCraftingRecipe {
	private static final Ingredient field_9007 = Ingredient.ofItems(Items.field_8407);
	private static final Ingredient field_9006 = Ingredient.ofItems(Items.field_8054);
	private static final Ingredient field_9008 = Ingredient.ofItems(Items.field_8450);

	public FireworkRocketRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_17709(CraftingInventory craftingInventory, World world) {
		boolean bl = false;
		int i = 0;

		for (int j = 0; j < craftingInventory.getInvSize(); j++) {
			ItemStack itemStack = craftingInventory.getInvStack(j);
			if (!itemStack.isEmpty()) {
				if (field_9007.matches(itemStack)) {
					if (bl) {
						return false;
					}

					bl = true;
				} else if (field_9006.matches(itemStack)) {
					if (++i > 3) {
						return false;
					}
				} else if (!field_9008.matches(itemStack)) {
					return false;
				}
			}
		}

		return bl && i >= 1;
	}

	public ItemStack method_17708(CraftingInventory craftingInventory) {
		ItemStack itemStack = new ItemStack(Items.field_8639, 3);
		CompoundTag compoundTag = itemStack.getOrCreateSubCompoundTag("Fireworks");
		ListTag listTag = new ListTag();
		int i = 0;

		for (int j = 0; j < craftingInventory.getInvSize(); j++) {
			ItemStack itemStack2 = craftingInventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (field_9006.matches(itemStack2)) {
					i++;
				} else if (field_9008.matches(itemStack2)) {
					CompoundTag compoundTag2 = itemStack2.getSubCompoundTag("Explosion");
					if (compoundTag2 != null) {
						listTag.add(compoundTag2);
					}
				}
			}
		}

		compoundTag.putByte("Flight", (byte)i);
		if (!listTag.isEmpty()) {
			compoundTag.put("Explosions", listTag);
		}

		return itemStack;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public ItemStack getOutput() {
		return new ItemStack(Items.field_8639);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.FIREWORK_ROCKET;
	}
}
