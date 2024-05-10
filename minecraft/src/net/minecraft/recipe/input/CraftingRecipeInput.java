package net.minecraft.recipe.input;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;

public class CraftingRecipeInput implements RecipeInput {
	public static final CraftingRecipeInput EMPTY = new CraftingRecipeInput(0, 0, List.of());
	private final int width;
	private final int height;
	private final List<ItemStack> stacks;
	private final RecipeMatcher matcher = new RecipeMatcher();
	private final int stackCount;

	private CraftingRecipeInput(int width, int height, List<ItemStack> stacks) {
		this.width = width;
		this.height = height;
		this.stacks = stacks;
		int i = 0;

		for (ItemStack itemStack : stacks) {
			if (!itemStack.isEmpty()) {
				i++;
				this.matcher.addInput(itemStack, 1);
			}
		}

		this.stackCount = i;
	}

	public static CraftingRecipeInput create(int width, int height, List<ItemStack> stacks) {
		return method_60505(width, height, stacks).input();
	}

	public static CraftingRecipeInput.class_9765 method_60505(int i, int j, List<ItemStack> list) {
		if (i != 0 && j != 0) {
			int k = i - 1;
			int l = 0;
			int m = j - 1;
			int n = 0;

			for (int o = 0; o < j; o++) {
				boolean bl = true;

				for (int p = 0; p < i; p++) {
					ItemStack itemStack = (ItemStack)list.get(p + o * i);
					if (!itemStack.isEmpty()) {
						k = Math.min(k, p);
						l = Math.max(l, p);
						bl = false;
					}
				}

				if (!bl) {
					m = Math.min(m, o);
					n = Math.max(n, o);
				}
			}

			int o = l - k + 1;
			int q = n - m + 1;
			if (o <= 0 || q <= 0) {
				return CraftingRecipeInput.class_9765.field_51896;
			} else if (o == i && q == j) {
				return new CraftingRecipeInput.class_9765(new CraftingRecipeInput(i, j, list), k, m);
			} else {
				List<ItemStack> list2 = new ArrayList(o * q);

				for (int r = 0; r < q; r++) {
					for (int s = 0; s < o; s++) {
						int t = s + k + (r + m) * i;
						list2.add((ItemStack)list.get(t));
					}
				}

				return new CraftingRecipeInput.class_9765(new CraftingRecipeInput(o, q, list2), k, m);
			}
		} else {
			return CraftingRecipeInput.class_9765.field_51896;
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return (ItemStack)this.stacks.get(slot);
	}

	public ItemStack getStackInSlot(int x, int y) {
		return (ItemStack)this.stacks.get(x + y * this.width);
	}

	@Override
	public int getSize() {
		return this.stacks.size();
	}

	@Override
	public boolean isEmpty() {
		return this.stackCount == 0;
	}

	public RecipeMatcher getRecipeMatcher() {
		return this.matcher;
	}

	public List<ItemStack> getStacks() {
		return this.stacks;
	}

	public int getStackCount() {
		return this.stackCount;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else {
			return !(o instanceof CraftingRecipeInput craftingRecipeInput)
				? false
				: this.width == craftingRecipeInput.width
					&& this.height == craftingRecipeInput.height
					&& this.stackCount == craftingRecipeInput.stackCount
					&& ItemStack.stacksEqual(this.stacks, craftingRecipeInput.stacks);
		}
	}

	public int hashCode() {
		int i = ItemStack.listHashCode(this.stacks);
		i = 31 * i + this.width;
		return 31 * i + this.height;
	}

	public static record class_9765(CraftingRecipeInput input, int left, int top) {
		public static final CraftingRecipeInput.class_9765 field_51896 = new CraftingRecipeInput.class_9765(CraftingRecipeInput.EMPTY, 0, 0);
	}
}
