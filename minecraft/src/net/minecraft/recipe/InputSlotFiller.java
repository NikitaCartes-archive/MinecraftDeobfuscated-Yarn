package net.minecraft.recipe;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.CraftResponseClientPacket;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputSlotFiller implements RecipeGridAligner<Integer> {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final RecipeFinder recipeFinder = new RecipeFinder();
	protected PlayerInventory inventory;
	protected CraftingContainer craftingContainer;

	public void fillInputSlots(ServerPlayerEntity serverPlayerEntity, @Nullable Recipe recipe, boolean bl) {
		if (recipe != null && serverPlayerEntity.getRecipeBook().contains(recipe)) {
			this.inventory = serverPlayerEntity.inventory;
			this.craftingContainer = (CraftingContainer)serverPlayerEntity.container;
			if (this.canReturnInputs() || serverPlayerEntity.isCreative()) {
				this.recipeFinder.clear();
				serverPlayerEntity.inventory.populateRecipeFinder(this.recipeFinder);
				this.craftingContainer.populateRecipeFinder(this.recipeFinder);
				if (this.recipeFinder.findRecipe(recipe, null)) {
					this.fillInputSlots(recipe, bl);
				} else {
					this.returnInputs();
					serverPlayerEntity.networkHandler.sendPacket(new CraftResponseClientPacket(serverPlayerEntity.container.syncId, recipe));
				}

				serverPlayerEntity.inventory.markDirty();
			}
		}
	}

	protected void returnInputs() {
		for (int i = 0; i < this.craftingContainer.getCraftingWidth() * this.craftingContainer.getCraftingHeight() + 1; i++) {
			if (i != this.craftingContainer.getCraftingResultSlotIndex()
				|| !(this.craftingContainer instanceof CraftingTableContainer) && !(this.craftingContainer instanceof PlayerContainer)) {
				this.returnSlot(i);
			}
		}

		this.craftingContainer.clearCraftingSlots();
	}

	protected void returnSlot(int i) {
		ItemStack itemStack = this.craftingContainer.getSlot(i).getStack();
		if (!itemStack.isEmpty()) {
			for (; itemStack.getAmount() > 0; this.craftingContainer.getSlot(i).takeStack(1)) {
				int j = this.inventory.getOccupiedSlotWithRoomForStack(itemStack);
				if (j == -1) {
					j = this.inventory.getEmptySlot();
				}

				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setAmount(1);
				if (!this.inventory.insertStack(j, itemStack2)) {
					LOGGER.error("Can't find any space for item in the inventory");
				}
			}
		}
	}

	protected void fillInputSlots(Recipe recipe, boolean bl) {
		boolean bl2 = this.craftingContainer.matches(recipe);
		int i = this.recipeFinder.countRecipeCrafts(recipe, null);
		if (bl2) {
			for (int j = 0; j < this.craftingContainer.getCraftingHeight() * this.craftingContainer.getCraftingWidth() + 1; j++) {
				if (j != this.craftingContainer.getCraftingResultSlotIndex()) {
					ItemStack itemStack = this.craftingContainer.getSlot(j).getStack();
					if (!itemStack.isEmpty() && Math.min(i, itemStack.getMaxAmount()) < itemStack.getAmount() + 1) {
						return;
					}
				}
			}
		}

		int jx = this.getAmountToFill(bl, i, bl2);
		IntList intList = new IntArrayList();
		if (this.recipeFinder.findRecipe(recipe, intList, jx)) {
			int k = jx;

			for (int l : intList) {
				int m = RecipeFinder.getStackFromId(l).getMaxAmount();
				if (m < k) {
					k = m;
				}
			}

			if (this.recipeFinder.findRecipe(recipe, intList, k)) {
				this.returnInputs();
				this.alignRecipeToGrid(
					this.craftingContainer.getCraftingWidth(),
					this.craftingContainer.getCraftingHeight(),
					this.craftingContainer.getCraftingResultSlotIndex(),
					recipe,
					intList.iterator(),
					k
				);
			}
		}
	}

	@Override
	public void acceptAlignedInput(Iterator<Integer> iterator, int i, int j, int k, int l) {
		Slot slot = this.craftingContainer.getSlot(i);
		ItemStack itemStack = RecipeFinder.getStackFromId((Integer)iterator.next());
		if (!itemStack.isEmpty()) {
			for (int m = 0; m < j; m++) {
				this.fillInputSlot(slot, itemStack);
			}
		}
	}

	protected int getAmountToFill(boolean bl, int i, boolean bl2) {
		int j = 1;
		if (bl) {
			j = i;
		} else if (bl2) {
			j = 64;

			for (int k = 0; k < this.craftingContainer.getCraftingWidth() * this.craftingContainer.getCraftingHeight() + 1; k++) {
				if (k != this.craftingContainer.getCraftingResultSlotIndex()) {
					ItemStack itemStack = this.craftingContainer.getSlot(k).getStack();
					if (!itemStack.isEmpty() && j > itemStack.getAmount()) {
						j = itemStack.getAmount();
					}
				}
			}

			if (j < 64) {
				j++;
			}
		}

		return j;
	}

	protected void fillInputSlot(Slot slot, ItemStack itemStack) {
		int i = this.inventory.method_7371(itemStack);
		if (i != -1) {
			ItemStack itemStack2 = this.inventory.getInvStack(i).copy();
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getAmount() > 1) {
					this.inventory.takeInvStack(i, 1);
				} else {
					this.inventory.removeInvStack(i);
				}

				itemStack2.setAmount(1);
				if (slot.getStack().isEmpty()) {
					slot.setStack(itemStack2);
				} else {
					slot.getStack().addAmount(1);
				}
			}
		}
	}

	private boolean canReturnInputs() {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		int i = this.getFreeInventorySlots();

		for (int j = 0; j < this.craftingContainer.getCraftingWidth() * this.craftingContainer.getCraftingHeight() + 1; j++) {
			if (j != this.craftingContainer.getCraftingResultSlotIndex()) {
				ItemStack itemStack = this.craftingContainer.getSlot(j).getStack().copy();
				if (!itemStack.isEmpty()) {
					int k = this.inventory.getOccupiedSlotWithRoomForStack(itemStack);
					if (k == -1 && list.size() <= i) {
						for (ItemStack itemStack2 : list) {
							if (itemStack2.isEqualIgnoreTags(itemStack)
								&& itemStack2.getAmount() != itemStack2.getMaxAmount()
								&& itemStack2.getAmount() + itemStack.getAmount() <= itemStack2.getMaxAmount()) {
								itemStack2.addAmount(itemStack.getAmount());
								itemStack.setAmount(0);
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
