package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2955 implements class_2952<Integer> {
	protected static final Logger field_13349 = LogManager.getLogger();
	protected final class_1662 field_13347 = new class_1662();
	protected PlayerInventory field_13350;
	protected CraftingContainer field_13348;

	public void method_12826(ServerPlayerEntity serverPlayerEntity, @Nullable Recipe recipe, boolean bl) {
		if (recipe != null && serverPlayerEntity.getRecipeBook().method_14878(recipe)) {
			this.field_13350 = serverPlayerEntity.inventory;
			this.field_13348 = (CraftingContainer)serverPlayerEntity.container;
			if (this.method_12825() || serverPlayerEntity.isCreative()) {
				this.field_13347.method_7409();
				serverPlayerEntity.inventory.method_7387(this.field_13347);
				this.field_13348.method_7654(this.field_13347);
				if (this.field_13347.method_7402(recipe, null)) {
					this.method_12821(recipe, bl);
				} else {
					this.method_12822();
					serverPlayerEntity.networkHandler.sendPacket(new class_2695(serverPlayerEntity.container.syncId, recipe));
				}

				serverPlayerEntity.inventory.markDirty();
			}
		}
	}

	protected void method_12822() {
		for (int i = 0; i < this.field_13348.getCraftingWidth() * this.field_13348.getCrafitngHeight() + 1; i++) {
			if (i != this.field_13348.getCraftingResultSlotIndex()
				|| !(this.field_13348 instanceof CraftingTableContainer) && !(this.field_13348 instanceof PlayerContainer)) {
				this.method_12820(i);
			}
		}

		this.field_13348.clearCraftingSlots();
	}

	protected void method_12820(int i) {
		ItemStack itemStack = this.field_13348.getSlot(i).getStack();
		if (!itemStack.isEmpty()) {
			for (; itemStack.getAmount() > 0; this.field_13348.getSlot(i).takeStack(1)) {
				int j = this.field_13350.getOccupiedSlotWithRoomForStack(itemStack);
				if (j == -1) {
					j = this.field_13350.getEmptySlot();
				}

				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setAmount(1);
				if (!this.field_13350.insertStack(j, itemStack2)) {
					field_13349.error("Can't find any space for item in the inventory");
				}
			}
		}
	}

	protected void method_12821(Recipe recipe, boolean bl) {
		boolean bl2 = this.field_13348.matches(recipe);
		int i = this.field_13347.method_7407(recipe, null);
		if (bl2) {
			for (int j = 0; j < this.field_13348.getCrafitngHeight() * this.field_13348.getCraftingWidth() + 1; j++) {
				if (j != this.field_13348.getCraftingResultSlotIndex()) {
					ItemStack itemStack = this.field_13348.getSlot(j).getStack();
					if (!itemStack.isEmpty() && Math.min(i, itemStack.getMaxAmount()) < itemStack.getAmount() + 1) {
						return;
					}
				}
			}
		}

		int jx = this.method_12819(bl, i, bl2);
		IntList intList = new IntArrayList();
		if (this.field_13347.method_7406(recipe, intList, jx)) {
			int k = jx;

			for (int l : intList) {
				int m = class_1662.method_7405(l).getMaxAmount();
				if (m < k) {
					k = m;
				}
			}

			if (this.field_13347.method_7406(recipe, intList, k)) {
				this.method_12822();
				this.method_12816(
					this.field_13348.getCraftingWidth(), this.field_13348.getCrafitngHeight(), this.field_13348.getCraftingResultSlotIndex(), recipe, intList.iterator(), k
				);
			}
		}
	}

	@Override
	public void method_12815(Iterator<Integer> iterator, int i, int j, int k, int l) {
		Slot slot = this.field_13348.getSlot(i);
		ItemStack itemStack = class_1662.method_7405((Integer)iterator.next());
		if (!itemStack.isEmpty()) {
			for (int m = 0; m < j; m++) {
				this.method_12824(slot, itemStack);
			}
		}
	}

	protected int method_12819(boolean bl, int i, boolean bl2) {
		int j = 1;
		if (bl) {
			j = i;
		} else if (bl2) {
			j = 64;

			for (int k = 0; k < this.field_13348.getCraftingWidth() * this.field_13348.getCrafitngHeight() + 1; k++) {
				if (k != this.field_13348.getCraftingResultSlotIndex()) {
					ItemStack itemStack = this.field_13348.getSlot(k).getStack();
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

	protected void method_12824(Slot slot, ItemStack itemStack) {
		int i = this.field_13350.method_7371(itemStack);
		if (i != -1) {
			ItemStack itemStack2 = this.field_13350.getInvStack(i).copy();
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getAmount() > 1) {
					this.field_13350.takeInvStack(i, 1);
				} else {
					this.field_13350.removeInvStack(i);
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

	private boolean method_12825() {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		int i = this.method_12823();

		for (int j = 0; j < this.field_13348.getCraftingWidth() * this.field_13348.getCrafitngHeight() + 1; j++) {
			if (j != this.field_13348.getCraftingResultSlotIndex()) {
				ItemStack itemStack = this.field_13348.getSlot(j).getStack().copy();
				if (!itemStack.isEmpty()) {
					int k = this.field_13350.getOccupiedSlotWithRoomForStack(itemStack);
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

	private int method_12823() {
		int i = 0;

		for (ItemStack itemStack : this.field_13350.main) {
			if (itemStack.isEmpty()) {
				i++;
			}
		}

		return i;
	}
}
