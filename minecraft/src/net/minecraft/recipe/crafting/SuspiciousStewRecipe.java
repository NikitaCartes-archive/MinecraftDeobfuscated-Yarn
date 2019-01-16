package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.item.block.BlockItem;
import net.minecraft.recipe.RecipeSerializer;
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
				if (itemStack.getItem() == Blocks.field_10251.getItem() && !bl3) {
					bl3 = true;
				} else if (itemStack.getItem() == Blocks.field_10559.getItem() && !bl2) {
					bl2 = true;
				} else if (itemStack.getItem().matches(ItemTags.field_15543) && !bl) {
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
			if (!itemStack2.isEmpty() && itemStack2.getItem().matches(ItemTags.field_15543)) {
				itemStack = itemStack2;
				break;
			}
		}

		ItemStack itemStack3 = new ItemStack(Items.field_8766, 1);
		if (itemStack.getItem() instanceof BlockItem && ((BlockItem)itemStack.getItem()).getBlock() instanceof FlowerBlock) {
			FlowerBlock flowerBlock = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
			StatusEffect statusEffect = flowerBlock.method_10188();
			SuspiciousStewItem.method_8021(itemStack3, statusEffect, flowerBlock.method_10187());
		}

		return itemStack3;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i >= 2 && j >= 2;
	}

	@Override
	public RecipeSerializer<?> method_8119() {
		return RecipeSerializer.field_9030;
	}
}
