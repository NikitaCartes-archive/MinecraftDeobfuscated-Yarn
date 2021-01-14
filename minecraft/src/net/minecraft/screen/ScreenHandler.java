package net.minecraft.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public abstract class ScreenHandler {
	/**
	 * A list of item stacks that is used for tracking changes in {@link #sendContentUpdates()}.
	 */
	private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
	public final List<Slot> slots = Lists.<Slot>newArrayList();
	private final List<Property> properties = Lists.<Property>newArrayList();
	@Nullable
	private final ScreenHandlerType<?> type;
	public final int syncId;
	@Environment(EnvType.CLIENT)
	private short actionId;
	private int quickCraftButton = -1;
	private int quickCraftStage;
	private final Set<Slot> quickCraftSlots = Sets.<Slot>newHashSet();
	private final List<ScreenHandlerListener> listeners = Lists.<ScreenHandlerListener>newArrayList();
	private final Set<PlayerEntity> restrictedPlayers = Sets.<PlayerEntity>newHashSet();

	protected ScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
		this.type = type;
		this.syncId = syncId;
	}

	protected static boolean canUse(ScreenHandlerContext context, PlayerEntity player, Block block) {
		return context.get(
			(world, blockPos) -> !world.getBlockState(blockPos).isOf(block)
					? false
					: player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0,
			true
		);
	}

	public ScreenHandlerType<?> getType() {
		if (this.type == null) {
			throw new UnsupportedOperationException("Unable to construct this menu by type");
		} else {
			return this.type;
		}
	}

	/**
	 * Checks that the size of the provided inventory is at least as large as the {@code expectedSize}.
	 * 
	 * @throws IllegalArgumentException if the inventory size is smaller than {@code expectedSize}
	 */
	protected static void checkSize(Inventory inventory, int expectedSize) {
		int i = inventory.size();
		if (i < expectedSize) {
			throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + expectedSize);
		}
	}

	/**
	 * Checks that the size of the {@code data} is at least as large as the {@code expectedCount}.
	 * 
	 * @throws IllegalArgumentException if the {@code data} has a smaller size than {@code expectedCount}
	 */
	protected static void checkDataCount(PropertyDelegate data, int expectedCount) {
		int i = data.size();
		if (i < expectedCount) {
			throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + expectedCount);
		}
	}

	protected Slot addSlot(Slot slot) {
		slot.id = this.slots.size();
		this.slots.add(slot);
		this.trackedStacks.add(ItemStack.EMPTY);
		return slot;
	}

	protected Property addProperty(Property property) {
		this.properties.add(property);
		return property;
	}

	protected void addProperties(PropertyDelegate propertyDelegate) {
		for (int i = 0; i < propertyDelegate.size(); i++) {
			this.addProperty(Property.create(propertyDelegate, i));
		}
	}

	public void addListener(ScreenHandlerListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			listener.onHandlerRegistered(this, this.getStacks());
			this.sendContentUpdates();
		}
	}

	@Environment(EnvType.CLIENT)
	public void removeListener(ScreenHandlerListener listener) {
		this.listeners.remove(listener);
	}

	public DefaultedList<ItemStack> getStacks() {
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (int i = 0; i < this.slots.size(); i++) {
			defaultedList.add(((Slot)this.slots.get(i)).getStack());
		}

		return defaultedList;
	}

	/**
	 * Sends updates to listeners if any properties or slot stacks have changed.
	 */
	public void sendContentUpdates() {
		for (int i = 0; i < this.slots.size(); i++) {
			ItemStack itemStack = ((Slot)this.slots.get(i)).getStack();
			ItemStack itemStack2 = this.trackedStacks.get(i);
			if (!ItemStack.areEqual(itemStack2, itemStack)) {
				ItemStack itemStack3 = itemStack.copy();
				this.trackedStacks.set(i, itemStack3);

				for (ScreenHandlerListener screenHandlerListener : this.listeners) {
					screenHandlerListener.onSlotUpdate(this, i, itemStack3);
				}
			}
		}

		for (int ix = 0; ix < this.properties.size(); ix++) {
			Property property = (Property)this.properties.get(ix);
			if (property.hasChanged()) {
				for (ScreenHandlerListener screenHandlerListener2 : this.listeners) {
					screenHandlerListener2.onPropertyUpdate(this, ix, property.get());
				}
			}
		}
	}

	public boolean onButtonClick(PlayerEntity player, int id) {
		return false;
	}

	public Slot getSlot(int index) {
		return (Slot)this.slots.get(index);
	}

	public ItemStack transferSlot(PlayerEntity player, int index) {
		Slot slot = (Slot)this.slots.get(index);
		return slot != null ? slot.getStack() : ItemStack.EMPTY;
	}

	/**
	 * Performs a slot click. This can behave in many different ways depending mainly on the action type.
	 * @return The stack that was clicked on before anything changed, used mostly for verifying that the client and server are in sync
	 * 
	 * @param actionType The type of slot click. Check the docs for each SlotActionType value for details
	 */
	public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
		try {
			return this.method_30010(i, j, actionType, playerEntity);
		} catch (Exception var8) {
			CrashReport crashReport = CrashReport.create(var8, "Container click");
			CrashReportSection crashReportSection = crashReport.addElement("Click info");
			crashReportSection.add("Menu Type", (CrashCallable<String>)(() -> this.type != null ? Registry.SCREEN_HANDLER.getId(this.type).toString() : "<no type>"));
			crashReportSection.add("Menu Class", (CrashCallable<String>)(() -> this.getClass().getCanonicalName()));
			crashReportSection.add("Slot Count", this.slots.size());
			crashReportSection.add("Slot", i);
			crashReportSection.add("Button", j);
			crashReportSection.add("Type", actionType);
			throw new CrashException(crashReport);
		}
	}

	private ItemStack method_30010(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity) {
		ItemStack itemStack = ItemStack.EMPTY;
		PlayerInventory playerInventory = playerEntity.inventory;
		if (slotActionType == SlotActionType.QUICK_CRAFT) {
			int k = this.quickCraftStage;
			this.quickCraftStage = unpackQuickCraftStage(j);
			if ((k != 1 || this.quickCraftStage != 2) && k != this.quickCraftStage) {
				this.endQuickCraft();
			} else if (playerInventory.getCursorStack().isEmpty()) {
				this.endQuickCraft();
			} else if (this.quickCraftStage == 0) {
				this.quickCraftButton = unpackQuickCraftButton(j);
				if (shouldQuickCraftContinue(this.quickCraftButton, playerEntity)) {
					this.quickCraftStage = 1;
					this.quickCraftSlots.clear();
				} else {
					this.endQuickCraft();
				}
			} else if (this.quickCraftStage == 1) {
				Slot slot = (Slot)this.slots.get(i);
				ItemStack itemStack2 = playerInventory.getCursorStack();
				if (slot != null
					&& canInsertItemIntoSlot(slot, itemStack2, true)
					&& slot.canInsert(itemStack2)
					&& (this.quickCraftButton == 2 || itemStack2.getCount() > this.quickCraftSlots.size())
					&& this.canInsertIntoSlot(slot)) {
					this.quickCraftSlots.add(slot);
				}
			} else if (this.quickCraftStage == 2) {
				if (!this.quickCraftSlots.isEmpty()) {
					ItemStack itemStack3 = playerInventory.getCursorStack().copy();
					int l = playerInventory.getCursorStack().getCount();

					for (Slot slot2 : this.quickCraftSlots) {
						ItemStack itemStack4 = playerInventory.getCursorStack();
						if (slot2 != null
							&& canInsertItemIntoSlot(slot2, itemStack4, true)
							&& slot2.canInsert(itemStack4)
							&& (this.quickCraftButton == 2 || itemStack4.getCount() >= this.quickCraftSlots.size())
							&& this.canInsertIntoSlot(slot2)) {
							ItemStack itemStack5 = itemStack3.copy();
							int m = slot2.hasStack() ? slot2.getStack().getCount() : 0;
							calculateStackSize(this.quickCraftSlots, this.quickCraftButton, itemStack5, m);
							int n = Math.min(itemStack5.getMaxCount(), slot2.getMaxItemCount(itemStack5));
							if (itemStack5.getCount() > n) {
								itemStack5.setCount(n);
							}

							l -= itemStack5.getCount() - m;
							slot2.setStack(itemStack5);
						}
					}

					itemStack3.setCount(l);
					playerInventory.setCursorStack(itemStack3);
				}

				this.endQuickCraft();
			} else {
				this.endQuickCraft();
			}
		} else if (this.quickCraftStage != 0) {
			this.endQuickCraft();
		} else if ((slotActionType == SlotActionType.PICKUP || slotActionType == SlotActionType.QUICK_MOVE) && (j == 0 || j == 1)) {
			if (i == -999) {
				if (!playerInventory.getCursorStack().isEmpty()) {
					if (j == 0) {
						playerEntity.dropItem(playerInventory.getCursorStack(), true);
						playerInventory.setCursorStack(ItemStack.EMPTY);
					}

					if (j == 1) {
						playerEntity.dropItem(playerInventory.getCursorStack().split(1), true);
					}
				}
			} else if (slotActionType == SlotActionType.QUICK_MOVE) {
				if (i < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot3 = (Slot)this.slots.get(i);
				if (slot3 == null || !slot3.canTakeItems(playerEntity)) {
					return ItemStack.EMPTY;
				}

				for (ItemStack itemStack3 = this.transferSlot(playerEntity, i);
					!itemStack3.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot3.getStack(), itemStack3);
					itemStack3 = this.transferSlot(playerEntity, i)
				) {
					itemStack = itemStack3.copy();
				}
			} else {
				if (i < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot3 = (Slot)this.slots.get(i);
				if (slot3 != null) {
					ItemStack itemStack3 = slot3.getStack();
					ItemStack itemStack2 = playerInventory.getCursorStack();
					if (!itemStack3.isEmpty()) {
						itemStack = itemStack3.copy();
					}

					if (itemStack3.isEmpty()) {
						if (!itemStack2.isEmpty() && slot3.canInsert(itemStack2)) {
							int o = j == 0 ? itemStack2.getCount() : 1;
							if (o > slot3.getMaxItemCount(itemStack2)) {
								o = slot3.getMaxItemCount(itemStack2);
							}

							slot3.setStack(itemStack2.split(o));
						}
					} else if (slot3.canTakeItems(playerEntity)) {
						if (itemStack2.isEmpty()) {
							if (itemStack3.isEmpty()) {
								slot3.setStack(ItemStack.EMPTY);
								playerInventory.setCursorStack(ItemStack.EMPTY);
							} else {
								int o = j == 0 ? itemStack3.getCount() : (itemStack3.getCount() + 1) / 2;
								playerInventory.setCursorStack(slot3.takeStack(o));
								if (itemStack3.isEmpty()) {
									slot3.setStack(ItemStack.EMPTY);
								}

								slot3.onTakeItem(playerEntity, playerInventory.getCursorStack());
							}
						} else if (slot3.canInsert(itemStack2)) {
							if (canStacksCombine(itemStack3, itemStack2)) {
								int o = j == 0 ? itemStack2.getCount() : 1;
								if (o > slot3.getMaxItemCount(itemStack2) - itemStack3.getCount()) {
									o = slot3.getMaxItemCount(itemStack2) - itemStack3.getCount();
								}

								if (o > itemStack2.getMaxCount() - itemStack3.getCount()) {
									o = itemStack2.getMaxCount() - itemStack3.getCount();
								}

								itemStack2.decrement(o);
								itemStack3.increment(o);
							} else if (itemStack2.getCount() <= slot3.getMaxItemCount(itemStack2)) {
								slot3.setStack(itemStack2);
								playerInventory.setCursorStack(itemStack3);
							}
						} else if (itemStack2.getMaxCount() > 1 && canStacksCombine(itemStack3, itemStack2) && !itemStack3.isEmpty()) {
							int ox = itemStack3.getCount();
							if (ox + itemStack2.getCount() <= itemStack2.getMaxCount()) {
								itemStack2.increment(ox);
								itemStack3 = slot3.takeStack(ox);
								if (itemStack3.isEmpty()) {
									slot3.setStack(ItemStack.EMPTY);
								}

								slot3.onTakeItem(playerEntity, playerInventory.getCursorStack());
							}
						}
					}

					slot3.markDirty();
				}
			}
		} else if (slotActionType == SlotActionType.SWAP) {
			Slot slot3 = (Slot)this.slots.get(i);
			ItemStack itemStack3x = playerInventory.getStack(j);
			ItemStack itemStack2x = slot3.getStack();
			if (!itemStack3x.isEmpty() || !itemStack2x.isEmpty()) {
				if (itemStack3x.isEmpty()) {
					if (slot3.canTakeItems(playerEntity)) {
						playerInventory.setStack(j, itemStack2x);
						slot3.onTake(itemStack2x.getCount());
						slot3.setStack(ItemStack.EMPTY);
						slot3.onTakeItem(playerEntity, itemStack2x);
					}
				} else if (itemStack2x.isEmpty()) {
					if (slot3.canInsert(itemStack3x)) {
						int ox = slot3.getMaxItemCount(itemStack3x);
						if (itemStack3x.getCount() > ox) {
							slot3.setStack(itemStack3x.split(ox));
						} else {
							slot3.setStack(itemStack3x);
							playerInventory.setStack(j, ItemStack.EMPTY);
						}
					}
				} else if (slot3.canTakeItems(playerEntity) && slot3.canInsert(itemStack3x)) {
					int ox = slot3.getMaxItemCount(itemStack3x);
					if (itemStack3x.getCount() > ox) {
						slot3.setStack(itemStack3x.split(ox));
						slot3.onTakeItem(playerEntity, itemStack2x);
						if (!playerInventory.insertStack(itemStack2x)) {
							playerEntity.dropItem(itemStack2x, true);
						}
					} else {
						slot3.setStack(itemStack3x);
						playerInventory.setStack(j, itemStack2x);
						slot3.onTakeItem(playerEntity, itemStack2x);
					}
				}
			}
		} else if (slotActionType == SlotActionType.CLONE && playerEntity.abilities.creativeMode && playerInventory.getCursorStack().isEmpty() && i >= 0) {
			Slot slot3 = (Slot)this.slots.get(i);
			if (slot3 != null && slot3.hasStack()) {
				ItemStack itemStack3x = slot3.getStack().copy();
				itemStack3x.setCount(itemStack3x.getMaxCount());
				playerInventory.setCursorStack(itemStack3x);
			}
		} else if (slotActionType == SlotActionType.THROW && playerInventory.getCursorStack().isEmpty() && i >= 0) {
			Slot slot3 = (Slot)this.slots.get(i);
			if (slot3 != null && slot3.hasStack() && slot3.canTakeItems(playerEntity)) {
				ItemStack itemStack3x = slot3.takeStack(j == 0 ? 1 : slot3.getStack().getCount());
				slot3.onTakeItem(playerEntity, itemStack3x);
				playerEntity.dropItem(itemStack3x, true);
			}
		} else if (slotActionType == SlotActionType.PICKUP_ALL && i >= 0) {
			Slot slot3 = (Slot)this.slots.get(i);
			ItemStack itemStack3x = playerInventory.getCursorStack();
			if (!itemStack3x.isEmpty() && (slot3 == null || !slot3.hasStack() || !slot3.canTakeItems(playerEntity))) {
				int l = j == 0 ? 0 : this.slots.size() - 1;
				int ox = j == 0 ? 1 : -1;

				for (int p = 0; p < 2; p++) {
					for (int q = l; q >= 0 && q < this.slots.size() && itemStack3x.getCount() < itemStack3x.getMaxCount(); q += ox) {
						Slot slot4 = (Slot)this.slots.get(q);
						if (slot4.hasStack() && canInsertItemIntoSlot(slot4, itemStack3x, true) && slot4.canTakeItems(playerEntity) && this.canInsertIntoSlot(itemStack3x, slot4)
							)
						 {
							ItemStack itemStack6 = slot4.getStack();
							if (p != 0 || itemStack6.getCount() != itemStack6.getMaxCount()) {
								int n = Math.min(itemStack3x.getMaxCount() - itemStack3x.getCount(), itemStack6.getCount());
								ItemStack itemStack7 = slot4.takeStack(n);
								itemStack3x.increment(n);
								if (itemStack7.isEmpty()) {
									slot4.setStack(ItemStack.EMPTY);
								}

								slot4.onTakeItem(playerEntity, itemStack7);
							}
						}
					}
				}
			}

			this.sendContentUpdates();
		}

		return itemStack;
	}

	public static boolean canStacksCombine(ItemStack first, ItemStack second) {
		return first.getItem() == second.getItem() && ItemStack.areTagsEqual(first, second);
	}

	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return true;
	}

	public void close(PlayerEntity player) {
		PlayerInventory playerInventory = player.inventory;
		if (!playerInventory.getCursorStack().isEmpty()) {
			player.dropItem(playerInventory.getCursorStack(), false);
			playerInventory.setCursorStack(ItemStack.EMPTY);
		}
	}

	protected void dropInventory(PlayerEntity player, World world, Inventory inventory) {
		if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected()) {
			for (int i = 0; i < inventory.size(); i++) {
				player.dropItem(inventory.removeStack(i), false);
			}
		} else {
			for (int i = 0; i < inventory.size(); i++) {
				player.inventory.offerOrDrop(world, inventory.removeStack(i));
			}
		}
	}

	public void onContentChanged(Inventory inventory) {
		this.sendContentUpdates();
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		this.getSlot(slot).setStack(stack);
	}

	@Environment(EnvType.CLIENT)
	public void updateSlotStacks(List<ItemStack> stacks) {
		for (int i = 0; i < stacks.size(); i++) {
			this.getSlot(i).setStack((ItemStack)stacks.get(i));
		}
	}

	public void setProperty(int id, int value) {
		((Property)this.properties.get(id)).set(value);
	}

	@Environment(EnvType.CLIENT)
	public short getNextActionId(PlayerInventory playerInventory) {
		this.actionId++;
		return this.actionId;
	}

	public boolean isNotRestricted(PlayerEntity player) {
		return !this.restrictedPlayers.contains(player);
	}

	public void setPlayerRestriction(PlayerEntity player, boolean unrestricted) {
		if (unrestricted) {
			this.restrictedPlayers.remove(player);
		} else {
			this.restrictedPlayers.add(player);
		}
	}

	public abstract boolean canUse(PlayerEntity player);

	protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
		boolean bl = false;
		int i = startIndex;
		if (fromLast) {
			i = endIndex - 1;
		}

		if (stack.isStackable()) {
			while (!stack.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
				Slot slot = (Slot)this.slots.get(i);
				ItemStack itemStack = slot.getStack();
				if (!itemStack.isEmpty() && canStacksCombine(stack, itemStack)) {
					int j = itemStack.getCount() + stack.getCount();
					if (j <= stack.getMaxCount()) {
						stack.setCount(0);
						itemStack.setCount(j);
						slot.markDirty();
						bl = true;
					} else if (itemStack.getCount() < stack.getMaxCount()) {
						stack.decrement(stack.getMaxCount() - itemStack.getCount());
						itemStack.setCount(stack.getMaxCount());
						slot.markDirty();
						bl = true;
					}
				}

				if (fromLast) {
					i--;
				} else {
					i++;
				}
			}
		}

		if (!stack.isEmpty()) {
			if (fromLast) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while (fromLast ? i >= startIndex : i < endIndex) {
				Slot slotx = (Slot)this.slots.get(i);
				ItemStack itemStackx = slotx.getStack();
				if (itemStackx.isEmpty() && slotx.canInsert(stack)) {
					if (stack.getCount() > slotx.getMaxItemCount()) {
						slotx.setStack(stack.split(slotx.getMaxItemCount()));
					} else {
						slotx.setStack(stack.split(stack.getCount()));
					}

					slotx.markDirty();
					bl = true;
					break;
				}

				if (fromLast) {
					i--;
				} else {
					i++;
				}
			}
		}

		return bl;
	}

	public static int unpackQuickCraftButton(int quickCraftData) {
		return quickCraftData >> 2 & 3;
	}

	public static int unpackQuickCraftStage(int quickCraftData) {
		return quickCraftData & 3;
	}

	@Environment(EnvType.CLIENT)
	public static int packQuickCraftData(int quickCraftStage, int buttonId) {
		return quickCraftStage & 3 | (buttonId & 3) << 2;
	}

	public static boolean shouldQuickCraftContinue(int stage, PlayerEntity player) {
		if (stage == 0) {
			return true;
		} else {
			return stage == 1 ? true : stage == 2 && player.abilities.creativeMode;
		}
	}

	protected void endQuickCraft() {
		this.quickCraftStage = 0;
		this.quickCraftSlots.clear();
	}

	public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
		boolean bl = slot == null || !slot.hasStack();
		return !bl && stack.isItemEqualIgnoreDamage(slot.getStack()) && ItemStack.areTagsEqual(slot.getStack(), stack)
			? slot.getStack().getCount() + (allowOverflow ? 0 : stack.getCount()) <= stack.getMaxCount()
			: bl;
	}

	public static void calculateStackSize(Set<Slot> slots, int mode, ItemStack stack, int stackSize) {
		switch (mode) {
			case 0:
				stack.setCount(MathHelper.floor((float)stack.getCount() / (float)slots.size()));
				break;
			case 1:
				stack.setCount(1);
				break;
			case 2:
				stack.setCount(stack.getItem().getMaxCount());
		}

		stack.increment(stackSize);
	}

	public boolean canInsertIntoSlot(Slot slot) {
		return true;
	}

	public static int calculateComparatorOutput(@Nullable BlockEntity entity) {
		return entity instanceof Inventory ? calculateComparatorOutput((Inventory)entity) : 0;
	}

	public static int calculateComparatorOutput(@Nullable Inventory inventory) {
		if (inventory == null) {
			return 0;
		} else {
			int i = 0;
			float f = 0.0F;

			for (int j = 0; j < inventory.size(); j++) {
				ItemStack itemStack = inventory.getStack(j);
				if (!itemStack.isEmpty()) {
					f += (float)itemStack.getCount() / (float)Math.min(inventory.getMaxCountPerStack(), itemStack.getMaxCount());
					i++;
				}
			}

			f /= (float)inventory.size();
			return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
		}
	}
}
