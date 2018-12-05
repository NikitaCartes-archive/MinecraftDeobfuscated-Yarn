package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.block.BannerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShieldDecorationRecipe extends AbstractRecipe {
	public ShieldDecorationRecipe(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			ItemStack itemStack = ItemStack.EMPTY;
			ItemStack itemStack2 = ItemStack.EMPTY;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				if (!itemStack3.isEmpty()) {
					if (itemStack3.getItem() instanceof BannerItem) {
						if (!itemStack2.isEmpty()) {
							return false;
						}

						itemStack2 = itemStack3;
					} else {
						if (itemStack3.getItem() != Items.field_8255) {
							return false;
						}

						if (!itemStack.isEmpty()) {
							return false;
						}

						if (itemStack3.getSubCompoundTag("BlockEntityTag") != null) {
							return false;
						}

						itemStack = itemStack3;
					}
				}
			}

			return !itemStack.isEmpty() && !itemStack2.isEmpty();
		}
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		ItemStack itemStack2 = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack3 = inventory.getInvStack(i);
			if (!itemStack3.isEmpty()) {
				if (itemStack3.getItem() instanceof BannerItem) {
					itemStack = itemStack3;
				} else if (itemStack3.getItem() == Items.field_8255) {
					itemStack2 = itemStack3.copy();
				}
			}
		}

		if (itemStack2.isEmpty()) {
			return itemStack2;
		} else {
			CompoundTag compoundTag = itemStack.getSubCompoundTag("BlockEntityTag");
			CompoundTag compoundTag2 = compoundTag == null ? new CompoundTag() : compoundTag.copy();
			compoundTag2.putInt("Base", ((BannerItem)itemStack.getItem()).getColor().getId());
			itemStack2.setChildTag("BlockEntityTag", compoundTag2);
			return itemStack2;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializers.SHIELD_DECORATION;
	}
}
