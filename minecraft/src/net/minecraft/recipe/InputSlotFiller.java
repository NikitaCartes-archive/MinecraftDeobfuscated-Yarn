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
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputSlotFiller<C extends Inventory> implements RecipeGridAligner<Integer> {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final RecipeFinder recipeFinder = new RecipeFinder();
	protected PlayerInventory inventory;
	protected AbstractRecipeScreenHandler<C> craftingScreenHandler;

	public InputSlotFiller(AbstractRecipeScreenHandler<C> abstractRecipeScreenHandler) {
		this.craftingScreenHandler = abstractRecipeScreenHandler;
	}

	public void fillInputSlots(ServerPlayerEntity entity, @Nullable Recipe<C> recipe, boolean craftAll) {
		if (recipe != null && entity.getRecipeBook().contains(recipe)) {
			this.inventory = entity.getInventory();
			if (this.canReturnInputs() || entity.isCreative()) {
				this.recipeFinder.clear();
				entity.getInventory().populateRecipeFinder(this.recipeFinder);
				this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
				if (this.recipeFinder.findRecipe(recipe, null)) {
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
		for (int i = 0; i < this.craftingScreenHandler.getCraftingWidth() * this.craftingScreenHandler.getCraftingHeight() + 1; i++) {
			if (i != this.craftingScreenHandler.getCraftingResultSlotIndex()
				|| !(this.craftingScreenHandler instanceof CraftingScreenHandler) && !(this.craftingScreenHandler instanceof PlayerScreenHandler)) {
				this.returnSlot(i);
			}
		}

		this.craftingScreenHandler.clearCraftingSlots();
	}

	protected void returnSlot(int i) {
		ItemStack itemStack = this.craftingScreenHandler.getSlot(i).getStack();
		if (!itemStack.isEmpty()) {
			for (; itemStack.getCount() > 0; this.craftingScreenHandler.getSlot(i).takeStack(1)) {
				int j = this.inventory.getOccupiedSlotWithRoomForStack(itemStack);
				if (j == -1) {
					j = this.inventory.getEmptySlot();
				}

				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setCount(1);
				if (!this.inventory.insertStack(j, itemStack2)) {
					LOGGER.error("Can't find any space for item in the inventory");
				}
			}
		}
	}

	protected void fillInputSlots(Recipe<C> recipe, boolean craftAll) {
		boolean bl = this.craftingScreenHandler.matches(recipe);
		int i = this.recipeFinder.countRecipeCrafts(recipe, null);
		if (bl) {
			for (int j = 0; j < this.craftingScreenHandler.getCraftingHeight() * this.craftingScreenHandler.getCraftingWidth() + 1; j++) {
				if (j != this.craftingScreenHandler.getCraftingResultSlotIndex()) {
					ItemStack itemStack = this.craftingScreenHandler.getSlot(j).getStack();
					if (!itemStack.isEmpty() && Math.min(i, itemStack.getMaxCount()) < itemStack.getCount() + 1) {
						return;
					}
				}
			}
		}

		int jx = this.getAmountToFill(craftAll, i, bl);
		IntList intList = new IntArrayList();
		if (this.recipeFinder.findRecipe(recipe, intList, jx)) {
			int k = jx;

			for (int l : intList) {
				int m = RecipeFinder.getStackFromId(l).getMaxCount();
				if (m < k) {
					k = m;
				}
			}

			if (this.recipeFinder.findRecipe(recipe, intList, k)) {
				this.returnInputs();
				this.alignRecipeToGrid(
					this.craftingScreenHandler.getCraftingWidth(),
					this.craftingScreenHandler.getCraftingHeight(),
					this.craftingScreenHandler.getCraftingResultSlotIndex(),
					recipe,
					intList.iterator(),
					k
				);
			}
		}
	}

	@Override
	public void acceptAlignedInput(Iterator<Integer> inputs, int slot, int amount, int gridX, int gridY) {
		Slot slot2 = this.craftingScreenHandler.getSlot(slot);
		ItemStack itemStack = RecipeFinder.getStackFromId((Integer)inputs.next());
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

			for (int j = 0; j < this.craftingScreenHandler.getCraftingWidth() * this.craftingScreenHandler.getCraftingHeight() + 1; j++) {
				if (j != this.craftingScreenHandler.getCraftingResultSlotIndex()) {
					ItemStack itemStack = this.craftingScreenHandler.getSlot(j).getStack();
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

	protected void fillInputSlot(Slot slot, ItemStack itemStack) {
		int i = this.inventory.method_7371(itemStack);
		if (i != -1) {
			ItemStack itemStack2 = this.inventory.getStack(i).copy();
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getCount() > 1) {
					this.inventory.removeStack(i, 1);
				} else {
					this.inventory.removeStack(i);
				}

				itemStack2.setCount(1);
				if (slot.getStack().isEmpty()) {
					slot.setStack(itemStack2);
				} else {
					slot.getStack().increment(1);
				}
			}
		}
	}

	private boolean canReturnInputs() {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		int i = this.getFreeInventorySlots();

		for (int j = 0; j < this.craftingScreenHandler.getCraftingWidth() * this.craftingScreenHandler.getCraftingHeight() + 1; j++) {
			if (j != this.craftingScreenHandler.getCraftingResultSlotIndex()) {
				ItemStack itemStack = this.craftingScreenHandler.getSlot(j).getStack().copy();
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
