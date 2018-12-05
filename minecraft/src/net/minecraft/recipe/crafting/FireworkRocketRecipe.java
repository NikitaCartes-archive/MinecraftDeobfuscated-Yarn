package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.recipe.AbstractRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FireworkRocketRecipe extends AbstractRecipe {
	private static final Ingredient field_9007 = Ingredient.ofItems(Items.field_8407);
	private static final Ingredient field_9006 = Ingredient.ofItems(Items.field_8054);
	private static final Ingredient field_9008 = Ingredient.ofItems(Items.field_8450);

	public FireworkRocketRecipe(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			boolean bl = false;
			int i = 0;

			for (int j = 0; j < inventory.getInvSize(); j++) {
				ItemStack itemStack = inventory.getInvStack(j);
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
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		ItemStack itemStack = new ItemStack(Items.field_8639, 3);
		CompoundTag compoundTag = itemStack.getOrCreateSubCompoundTag("Fireworks");
		ListTag listTag = new ListTag();
		int i = 0;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (field_9006.matches(itemStack2)) {
					i++;
				} else if (field_9008.matches(itemStack2)) {
					CompoundTag compoundTag2 = itemStack2.getSubCompoundTag("Explosion");
					if (compoundTag2 != null) {
						listTag.add((Tag)compoundTag2);
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
		return RecipeSerializers.FIREWORK_ROCKET;
	}
}
