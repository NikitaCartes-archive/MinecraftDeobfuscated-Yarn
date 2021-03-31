package net.minecraft.recipe;

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

	public boolean matches(CraftingInventory craftingInventory, World world) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;

		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.isOf(Blocks.BROWN_MUSHROOM.asItem()) && !bl3) {
					bl3 = true;
				} else if (itemStack.isOf(Blocks.RED_MUSHROOM.asItem()) && !bl2) {
					bl2 = true;
				} else if (itemStack.isIn(ItemTags.SMALL_FLOWERS) && !bl) {
					bl = true;
				} else {
					if (!itemStack.isOf(Items.BOWL) || bl4) {
						return false;
					}

					bl4 = true;
				}
			}
		}

		return bl && bl3 && bl2 && bl4;
	}

	public ItemStack craft(CraftingInventory craftingInventory) {
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack2 = craftingInventory.getStack(i);
			if (!itemStack2.isEmpty() && itemStack2.isIn(ItemTags.SMALL_FLOWERS)) {
				itemStack = itemStack2;
				break;
			}
		}

		ItemStack itemStack3 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
		if (itemStack.getItem() instanceof BlockItem && ((BlockItem)itemStack.getItem()).getBlock() instanceof FlowerBlock) {
			FlowerBlock flowerBlock = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
			StatusEffect statusEffect = flowerBlock.getEffectInStew();
			SuspiciousStewItem.addEffectToStew(itemStack3, statusEffect, flowerBlock.getEffectInStewDuration());
		}

		return itemStack3;
	}

	@Override
	public boolean fits(int width, int height) {
		return width >= 2 && height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SUSPICIOUS_STEW;
	}
}
