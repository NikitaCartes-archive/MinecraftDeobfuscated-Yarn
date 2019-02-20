package net.minecraft.recipe.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ArmorDyeRecipe extends SpecialCraftingRecipe {
	public ArmorDyeRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_17701(CraftingInventory craftingInventory, World world) {
		ItemStack itemStack = ItemStack.EMPTY;
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack2 = craftingInventory.getInvStack(i);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() instanceof DyeableItem) {
					if (!itemStack.isEmpty()) {
						return false;
					}

					itemStack = itemStack2;
				} else {
					if (!(itemStack2.getItem() instanceof DyeItem)) {
						return false;
					}

					list.add(itemStack2);
				}
			}
		}

		return !itemStack.isEmpty() && !list.isEmpty();
	}

	public ItemStack method_17700(CraftingInventory craftingInventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		DyeableItem dyeableItem = null;

		for (int k = 0; k < craftingInventory.getInvSize(); k++) {
			ItemStack itemStack2 = craftingInventory.getInvStack(k);
			if (!itemStack2.isEmpty()) {
				Item item = itemStack2.getItem();
				if (item instanceof DyeableItem) {
					dyeableItem = (DyeableItem)item;
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2.copy();
					itemStack.setAmount(1);
					if (dyeableItem.hasColor(itemStack2)) {
						int l = dyeableItem.getColor(itemStack);
						float f = (float)(l >> 16 & 0xFF) / 255.0F;
						float g = (float)(l >> 8 & 0xFF) / 255.0F;
						float h = (float)(l & 0xFF) / 255.0F;
						i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0F);
						is[0] = (int)((float)is[0] + f * 255.0F);
						is[1] = (int)((float)is[1] + g * 255.0F);
						is[2] = (int)((float)is[2] + h * 255.0F);
						j++;
					}
				} else {
					if (!(item instanceof DyeItem)) {
						return ItemStack.EMPTY;
					}

					float[] fs = ((DyeItem)item).getColor().getColorComponents();
					int m = (int)(fs[0] * 255.0F);
					int n = (int)(fs[1] * 255.0F);
					int o = (int)(fs[2] * 255.0F);
					i += Math.max(m, Math.max(n, o));
					is[0] += m;
					is[1] += n;
					is[2] += o;
					j++;
				}
			}
		}

		if (dyeableItem == null) {
			return ItemStack.EMPTY;
		} else {
			int kx = is[0] / j;
			int p = is[1] / j;
			int q = is[2] / j;
			float r = (float)i / (float)j;
			float f = (float)Math.max(kx, Math.max(p, q));
			kx = (int)((float)kx * r / f);
			p = (int)((float)p * r / f);
			q = (int)((float)q * r / f);
			int var25 = (kx << 8) + p;
			var25 = (var25 << 8) + q;
			dyeableItem.setColor(itemStack, var25);
			return itemStack;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.ARMOR_DYE;
	}
}
