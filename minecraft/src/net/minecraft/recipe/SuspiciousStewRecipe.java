package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SuspiciousStewRecipe extends SpecialCraftingRecipe {
	public SuspiciousStewRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_17739(CraftingInventory craftingInventory, World world) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack = craftingInventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() == Blocks.field_10251.asItem() && !bl3) {
					bl3 = true;
				} else if (itemStack.getItem() == Blocks.field_10559.asItem() && !bl2) {
					bl2 = true;
				} else if (itemStack.getItem().isIn(ItemTags.field_15543) && !bl) {
					bl = true;
				} else {
					if (itemStack.getItem() != Items.field_8428 || bl4) {
						return false;
					}

					bl4 = true;
				}
			}
		}

		return bl && bl3 && bl2 && bl4;
	}

	public ItemStack method_17738(CraftingInventory craftingInventory) {
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack2 = craftingInventory.getInvStack(i);
			if (!itemStack2.isEmpty() && itemStack2.getItem().isIn(ItemTags.field_15543)) {
				itemStack = itemStack2;
				break;
			}
		}

		ItemStack itemStack3 = new ItemStack(Items.field_8766, 1);
		if (itemStack.getItem() instanceof BlockItem && ((BlockItem)itemStack.getItem()).getBlock() instanceof FlowerBlock) {
			FlowerBlock flowerBlock = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
			StatusEffect statusEffect = flowerBlock.getEffectInStew();
			SuspiciousStewItem.addEffectToStew(itemStack3, statusEffect, flowerBlock.getEffectInStewDuration());
		}

		return itemStack3;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i >= 2 && j >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SUSPICIOUS_STEW;
	}
}
