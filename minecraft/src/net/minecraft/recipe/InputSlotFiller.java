package net.minecraft.recipe;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputSlotFiller<C extends Inventory> implements RecipeGridAligner<Integer> {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final RecipeMatcher matcher = new RecipeMatcher();
	protected PlayerInventory inventory;
	protected AbstractRecipeScreenHandler<C> handler;

	public InputSlotFiller(AbstractRecipeScreenHandler<C> handler) {
		this.handler = handler;
	}

	public void fillInputSlots(ServerPlayerEntity entity, @Nullable Recipe<C> recipe, boolean craftAll) {
		if (recipe != null && entity.getRecipeBook().contains(recipe)) {
			this.inventory = entity.getInventory();
			if (this.canReturnInputs() || entity.isCreative()) {
				this.matcher.clear();
				entity.getInventory().populateRecipeFinder(this.matcher);
				this.handler.populateRecipeFinder(this.matcher);
				if (this.matcher.match(recipe, null)) {
					this.fillInputSlots(recipe, craftAll);
				} else {
					this.returnInputs(true);
					entity.networkHandler.sendPacket(new CraftFailedResponseS2CPacket(entity.currentScreenHandler.syncId, recipe));
				}

				entity.getInventory().markDirty();
			}
		}
	}

	protected void returnInputs(boolean bl) {
		for (int i = 0; i < this.handler.getCraftingSlotCount(); i++) {
			if (this.handler.method_32339(i)) {
				ItemStack itemStack = this.handler.getSlot(i).getStack().copy();
				this.inventory.offer(itemStack, false);
				this.handler.getSlot(i).setStack(itemStack);
			}
		}

		this.handler.clearCraftingSlots();
	}

	protected void fillInputSlots(Recipe<C> recipe, boolean craftAll) {
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
		if (this.matcher.match(recipe, intList, jx)) {
			int k = jx;

			for (int l : intList) {
				int m = RecipeMatcher.getStackFromId(l).getMaxCount();
				if (m < k) {
					k = m;
				}
			}

			if (this.matcher.match(recipe, intList, k)) {
				this.returnInputs(false);
				this.alignRecipeToGrid(
					this.handler.getCraftingWidth(), this.handler.getCraftingHeight(), this.handler.getCraftingResultSlotIndex(), recipe, intList.iterator(), k
				);
			}
		}
	}

	@Override
	public void acceptAlignedInput(Iterator<Integer> inputs, int slot, int amount, int gridX, int gridY) {
		Slot slot2 = this.handler.getSlot(slot);
		ItemStack itemStack = RecipeMatcher.getStackFromId((Integer)inputs.next());
		if (!itemStack.isEmpty()) {
			for (int i = 0; i < amount; i++) {
				this.fillInputSlot(slot2, itemStack);
			}
		}
	}

	protected int getAmountToFill(boolean craftAll, int limit, boolean recipeInCraftingSlots) {
		int i = 1;
		if (craftAll) {
			i = limit;
		} else if (recipeInCraftingSlots) {
			i = 64;

			for (int j = 0; j < this.handler.getCraftingWidth() * this.handler.getCraftingHeight() + 1; j++) {
				if (j != this.handler.getCraftingResultSlotIndex()) {
					ItemStack itemStack = this.handler.getSlot(j).getStack();
					if (!itemStack.isEmpty() && i > itemStack.getCount()) {
						i = itemStack.getCount();
					}
				}
			}

			if (i < 64) {
				i++;
			}
		}

		return i;
	}

	protected void fillInputSlot(Slot slot, ItemStack stack) {
		int i = this.inventory.indexOf(stack);
		if (i != -1) {
			ItemStack itemStack = this.inventory.getStack(i).copy();
			if (!itemStack.isEmpty()) {
				if (itemStack.getCount() > 1) {
					this.inventory.removeStack(i, 1);
				} else {
					this.inventory.removeStack(i);
				}

				itemStack.setCount(1);
				if (slot.getStack().isEmpty()) {
					slot.setStack(itemStack);
				} else {
					slot.getStack().increment(1);
				}
			}
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
							if (itemStack2.isItemEqualIgnoreDamage(itemStack)
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
