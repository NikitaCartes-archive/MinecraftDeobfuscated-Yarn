package net.minecraft.recipe;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class InputSlotFiller<I extends RecipeInput, R extends Recipe<I>> implements RecipeGridAligner<Integer> {
	private static final int field_51523 = -1;
	protected final RecipeMatcher matcher = new RecipeMatcher();
	protected PlayerInventory inventory;
	protected AbstractRecipeScreenHandler<I, R> handler;

	public InputSlotFiller(AbstractRecipeScreenHandler<I, R> handler) {
		this.handler = handler;
	}

	public void fillInputSlots(ServerPlayerEntity entity, @Nullable RecipeEntry<R> recipe, boolean craftAll) {
		if (recipe != null && entity.getRecipeBook().contains(recipe)) {
			this.inventory = entity.getInventory();
			if (this.canReturnInputs() || entity.isCreative()) {
				this.matcher.clear();
				entity.getInventory().populateRecipeFinder(this.matcher);
				this.handler.populateRecipeFinder(this.matcher);
				if (this.matcher.match(recipe.value(), null)) {
					this.fillInputSlots(recipe, craftAll);
				} else {
					this.returnInputs();
					entity.networkHandler.sendPacket(new CraftFailedResponseS2CPacket(entity.currentScreenHandler.syncId, recipe));
				}

				entity.getInventory().markDirty();
			}
		}
	}

	protected void returnInputs() {
		for (int i = 0; i < this.handler.getCraftingSlotCount(); i++) {
			if (this.handler.canInsertIntoSlot(i)) {
				ItemStack itemStack = this.handler.getSlot(i).getStack().copy();
				this.inventory.offer(itemStack, false);
				this.handler.getSlot(i).setStackNoCallbacks(itemStack);
			}
		}

		this.handler.clearCraftingSlots();
	}

	protected void fillInputSlots(RecipeEntry<R> recipe, boolean craftAll) {
		boolean bl = this.handler.matches(recipe);
		int i = this.matcher.countCrafts(recipe, null);
		if (bl) {
			for (int j = 0; j < this.handler.getCraftingHeight() * this.handler.getCraftingWidth() + 1; j++) {
				if (j != this.handler.getCraftingResultSlotIndex()) {
					ItemStack itemStack = this.handler.getSlot(j).getStack();
					if (!itemStack.isEmpty() && Math.min(i, itemStack.getMaxCount()) < itemStack.getCount() + 1) {
						return;
					}
				}
			}
		}

		int jx = this.getAmountToFill(craftAll, i, bl);
		IntList intList = new IntArrayList();
		if (this.matcher.match(recipe.value(), intList, jx)) {
			int k = jx;

			for (int l : intList) {
				ItemStack itemStack2 = RecipeMatcher.getStackFromId(l);
				if (!itemStack2.isEmpty()) {
					int m = itemStack2.getMaxCount();
					if (m < k) {
						k = m;
					}
				}
			}

			if (this.matcher.match(recipe.value(), intList, k)) {
				this.returnInputs();
				this.alignRecipeToGrid(
					this.handler.getCraftingWidth(), this.handler.getCraftingHeight(), this.handler.getCraftingResultSlotIndex(), recipe, intList.iterator(), k
				);
			}
		}
	}

	public void acceptAlignedInput(Integer integer, int i, int j, int k, int l) {
		Slot slot = this.handler.getSlot(i);
		ItemStack itemStack = RecipeMatcher.getStackFromId(integer);
		if (!itemStack.isEmpty()) {
			int m = j;

			while (m > 0) {
				m = this.fillInputSlot(slot, itemStack, m);
				if (m == -1) {
					return;
				}
			}
		}
	}

	protected int getAmountToFill(boolean craftAll, int limit, boolean recipeInCraftingSlots) {
		int i = 1;
		if (craftAll) {
			i = limit;
		} else if (recipeInCraftingSlots) {
			i = Integer.MAX_VALUE;

			for (int j = 0; j < this.handler.getCraftingWidth() * this.handler.getCraftingHeight() + 1; j++) {
				if (j != this.handler.getCraftingResultSlotIndex()) {
					ItemStack itemStack = this.handler.getSlot(j).getStack();
					if (!itemStack.isEmpty() && i > itemStack.getCount()) {
						i = itemStack.getCount();
					}
				}
			}

			if (i != Integer.MAX_VALUE) {
				i++;
			}
		}

		return i;
	}

	protected int fillInputSlot(Slot slot, ItemStack stack, int i) {
		int j = this.inventory.indexOf(stack);
		if (j == -1) {
			return -1;
		} else {
			ItemStack itemStack = this.inventory.getStack(j);
			int k;
			if (i < itemStack.getCount()) {
				this.inventory.removeStack(j, i);
				k = i;
			} else {
				this.inventory.removeStack(j);
				k = itemStack.getCount();
			}

			if (slot.getStack().isEmpty()) {
				slot.setStackNoCallbacks(itemStack.copyWithCount(k));
			} else {
				slot.getStack().increment(k);
			}

			return i - k;
		}
	}

	private boolean canReturnInputs() {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		int i = this.getFreeInventorySlots();

		for (int j = 0; j < this.handler.getCraftingWidth() * this.handler.getCraftingHeight() + 1; j++) {
			if (j != this.handler.getCraftingResultSlotIndex()) {
				ItemStack itemStack = this.handler.getSlot(j).getStack().copy();
				if (!itemStack.isEmpty()) {
					int k = this.inventory.getOccupiedSlotWithRoomForStack(itemStack);
					if (k == -1 && list.size() <= i) {
						for (ItemStack itemStack2 : list) {
							if (ItemStack.areItemsEqual(itemStack2, itemStack)
								&& itemStack2.getCount() != itemStack2.getMaxCount()
								&& itemStack2.getCount() + itemStack.getCount() <= itemStack2.getMaxCount()) {
								itemStack2.increment(itemStack.getCount());
								itemStack.setCount(0);
								break;
							}
						}

						if (!itemStack.isEmpty()) {
							if (list.size() >= i) {
								return false;
							}

							list.add(itemStack);
						}
					} else if (k == -1) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private int getFreeInventorySlots() {
		int i = 0;

		for (ItemStack itemStack : this.inventory.main) {
			if (itemStack.isEmpty()) {
				i++;
			}
		}

		return i;
	}
}
