package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FireworkRocketRecipe extends SpecialCraftingRecipe {
	private static final Ingredient PAPER = Ingredient.ofItems(Items.PAPER);
	private static final Ingredient DURATION_MODIFIER = Ingredient.ofItems(Items.GUNPOWDER);
	private static final Ingredient FIREWORK_STAR = Ingredient.ofItems(Items.FIREWORK_STAR);

	public FireworkRocketRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		boolean bl = false;
		int i = 0;

		for (int j = 0; j < craftingInventory.size(); j++) {
			ItemStack itemStack = craftingInventory.getStack(j);
			if (!itemStack.isEmpty()) {
				if (PAPER.test(itemStack)) {
					if (bl) {
						return false;
					}

					bl = true;
				} else if (DURATION_MODIFIER.test(itemStack)) {
					if (++i > 3) {
						return false;
					}
				} else if (!FIREWORK_STAR.test(itemStack)) {
					return false;
				}
			}
		}

		return bl && i >= 1;
	}

	public ItemStack craft(CraftingInventory craftingInventory) {
		ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 3);
		NbtCompound nbtCompound = itemStack.getOrCreateSubTag("Fireworks");
		NbtList nbtList = new NbtList();
		int i = 0;

		for (int j = 0; j < craftingInventory.size(); j++) {
			ItemStack itemStack2 = craftingInventory.getStack(j);
			if (!itemStack2.isEmpty()) {
				if (DURATION_MODIFIER.test(itemStack2)) {
					i++;
				} else if (FIREWORK_STAR.test(itemStack2)) {
					NbtCompound nbtCompound2 = itemStack2.getSubTag("Explosion");
					if (nbtCompound2 != null) {
						nbtList.add(nbtCompound2);
					}
				}
			}
		}

		nbtCompound.putByte("Flight", (byte)i);
		if (!nbtList.isEmpty()) {
			nbtCompound.put("Explosions", nbtList);
		}

		return itemStack;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getOutput() {
		return new ItemStack(Items.FIREWORK_ROCKET);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.FIREWORK_ROCKET;
	}
}
