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
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public abstract class ScreenHandler {
	/**
	 * A list of item stacks that is used for tracking changes in {@link #sendContentUpdates()}.
	 */
	private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
	public final DefaultedList<Slot> slots = DefaultedList.of();
	private final List<Property> properties = Lists.<Property>newArrayList();
	@Nullable
	private final ScreenHandlerType<?> type;
	public final int syncId;
	@Environment(EnvType.CLIENT)
	private short actionId;
	private int quickCraftStage = -1;
	private int quickCraftButton;
	private final Set<Slot> quickCraftSlots = Sets.<Slot>newHashSet();
	private final List<ScreenHandlerListener> listeners = Lists.<ScreenHandlerListener>newArrayList();
	private final Set<PlayerEntity> restrictedPlayers = Sets.<PlayerEntity>newHashSet();

	protected ScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
		this.type = type;
		this.syncId = syncId;
	}

	protected static boolean canUse(ScreenHandlerContext context, PlayerEntity player, Block block) {
		return context.run(
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
			defaultedList.add(this.slots.get(i).getStack());
		}

		return defaultedList;
	}

	/**
	 * Sends updates to listeners if any properties or slot stacks have changed.
	 */
	public void sendContentUpdates() {
		for (int i = 0; i < this.slots.size(); i++) {
			ItemStack itemStack = this.slots.get(i).getStack();
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
		return this.slots.get(index);
	}

	public ItemStack transferSlot(PlayerEntity player, int index) {
		return this.slots.get(index).getStack();
	}

	/**
	 * Performs a slot click. This can behave in many different ways depending mainly on the action type.
	 * @return The stack that was clicked on before anything changed, used mostly for verifying that the client and server are in sync
	 * 
	 * @param actionType The type of slot click. Check the docs for each SlotActionType value for details
	 */
	public ItemStack onSlotClick(int slotIndex, int clickData, SlotActionType actionType, PlayerEntity player) {
		try {
			return this.removeStack(slotIndex, clickData, actionType, player);
		} catch (Exception var8) {
			CrashReport crashReport = CrashReport.create(var8, "Container click");
			CrashReportSection crashReportSection = crashReport.addElement("Click info");
			crashReportSection.add("Menu Type", (CrashCallable<String>)(() -> this.type != null ? Registry.SCREEN_HANDLER.getId(this.type).toString() : "<no type>"));
			crashReportSection.add("Menu Class", (CrashCallable<String>)(() -> this.getClass().getCanonicalName()));
			crashReportSection.add("Slot Count", this.slots.size());
			crashReportSection.add("Slot", slotIndex);
			crashReportSection.add("Button", clickData);
			crashReportSection.add("Type", actionType);
			throw new CrashException(crashReport);
		}
	}

	private ItemStack removeStack(int slotIndex, int clickData, SlotActionType actionType, PlayerEntity player) {
		ItemStack itemStack = ItemStack.EMPTY;
		PlayerInventory playerInventory = player.getInventory();
		if (actionType == SlotActionType.QUICK_CRAFT) {
			int i = this.quickCraftButton;
			this.quickCraftButton = unpackQuickCraftStage(clickData);
			if ((i != 1 || this.quickCraftButton != 2) && i != this.quickCraftButton) {
				this.endQuickCraft();
			} else if (playerInventory.getCursorStack().isEmpty()) {
				this.endQuickCraft();
			} else if (this.quickCraftButton == 0) {
				this.quickCraftStage = unpackQuickCraftButton(clickData);
				if (shouldQuickCraftContinue(this.quickCraftStage, player)) {
					this.quickCraftButton = 1;
					this.quickCraftSlots.clear();
				} else {
					this.endQuickCraft();
				}
			} else if (this.quickCraftButton == 1) {
				Slot slot = this.slots.get(slotIndex);
				ItemStack itemStack2 = playerInventory.getCursorStack();
				if (canInsertItemIntoSlot(slot, itemStack2, true)
					&& slot.canInsert(itemStack2)
					&& (this.quickCraftStage == 2 || itemStack2.getCount() > this.quickCraftSlots.size())
					&& this.canInsertIntoSlot(slot)) {
					this.quickCraftSlots.add(slot);
				}
			} else if (this.quickCraftButton == 2) {
				if (!this.quickCraftSlots.isEmpty()) {
					if (this.quickCraftSlots.size() == 1) {
						int j = ((Slot)this.quickCraftSlots.iterator().next()).id;
						this.endQuickCraft();
						return this.removeStack(j, this.quickCraftStage, SlotActionType.PICKUP, player);
					}

					ItemStack itemStack3 = playerInventory.getCursorStack().copy();
					int k = playerInventory.getCursorStack().getCount();

					for (Slot slot2 : this.quickCraftSlots) {
						ItemStack itemStack4 = playerInventory.getCursorStack();
						if (slot2 != null
							&& canInsertItemIntoSlot(slot2, itemStack4, true)
							&& slot2.canInsert(itemStack4)
							&& (this.quickCraftStage == 2 || itemStack4.getCount() >= this.quickCraftSlots.size())
							&& this.canInsertIntoSlot(slot2)) {
							ItemStack itemStack5 = itemStack3.copy();
							int l = slot2.hasStack() ? slot2.getStack().getCount() : 0;
							calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack5, l);
							int m = Math.min(itemStack5.getMaxCount(), slot2.getMaxItemCount(itemStack5));
							if (itemStack5.getCount() > m) {
								itemStack5.setCount(m);
							}

							k -= itemStack5.getCount() - l;
							slot2.setStack(itemStack5);
						}
					}

					itemStack3.setCount(k);
					playerInventory.setCursorStack(itemStack3);
				}

				this.endQuickCraft();
			} else {
				this.endQuickCraft();
			}
		} else if (this.quickCraftButton != 0) {
			this.endQuickCraft();
		} else if ((actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE) && (clickData == 0 || clickData == 1)) {
			ClickType clickType = clickData == 0 ? ClickType.LEFT : ClickType.RIGHT;
			if (slotIndex == -999) {
				if (!playerInventory.getCursorStack().isEmpty()) {
					if (clickType == ClickType.LEFT) {
						player.dropItem(playerInventory.getCursorStack(), true);
						playerInventory.setCursorStack(ItemStack.EMPTY);
					} else {
						player.dropItem(playerInventory.getCursorStack().split(1), true);
					}
				}
			} else if (actionType == SlotActionType.QUICK_MOVE) {
				if (slotIndex < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot = this.slots.get(slotIndex);
				if (!slot.canTakeItems(player)) {
					return ItemStack.EMPTY;
				}

				for (ItemStack itemStack2 = this.transferSlot(player, slotIndex);
					!itemStack2.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot.getStack(), itemStack2);
					itemStack2 = this.transferSlot(player, slotIndex)
				) {
					itemStack = itemStack2.copy();
				}
			} else {
				if (slotIndex < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot = this.slots.get(slotIndex);
				ItemStack itemStack2 = slot.getStack();
				ItemStack itemStack6 = playerInventory.getCursorStack();
				if (!itemStack2.isEmpty()) {
					itemStack = itemStack2.copy();
				}

				if (!itemStack6.onStackClicked(slot, clickType, playerInventory) && !itemStack2.onClicked(itemStack6, slot, clickType, playerInventory)) {
					if (itemStack2.isEmpty()) {
						if (!itemStack6.isEmpty()) {
							int n = clickType == ClickType.LEFT ? itemStack6.getCount() : 1;
							playerInventory.setCursorStack(slot.method_32755(itemStack6, n));
						}
					} else if (slot.canTakeItems(player)) {
						if (itemStack6.isEmpty()) {
							int n = clickType == ClickType.LEFT ? itemStack2.getCount() : (itemStack2.getCount() + 1) / 2;
							playerInventory.setCursorStack(slot.method_32753(n, Integer.MAX_VALUE, player));
						} else if (slot.canInsert(itemStack6)) {
							if (ItemStack.canCombine(itemStack2, itemStack6)) {
								int n = clickType == ClickType.LEFT ? itemStack6.getCount() : 1;
								playerInventory.setCursorStack(slot.method_32755(itemStack6, n));
							} else if (itemStack6.getCount() <= slot.getMaxItemCount(itemStack6)) {
								slot.setStack(itemStack6);
								playerInventory.setCursorStack(itemStack2);
							}
						} else if (ItemStack.canCombine(itemStack2, itemStack6)) {
							ItemStack itemStack7 = slot.method_32753(itemStack2.getCount(), itemStack6.getMaxCount() - itemStack6.getCount(), player);
							itemStack6.increment(itemStack7.getCount());
						}
					}
				}

				slot.markDirty();
			}
		} else if (actionType == SlotActionType.SWAP) {
			Slot slot3 = this.slots.get(slotIndex);
			ItemStack itemStack3 = playerInventory.getStack(clickData);
			ItemStack itemStack2x = slot3.getStack();
			if (!itemStack3.isEmpty() || !itemStack2x.isEmpty()) {
				if (itemStack3.isEmpty()) {
					if (slot3.canTakeItems(player)) {
						playerInventory.setStack(clickData, itemStack2x);
						slot3.onTake(itemStack2x.getCount());
						slot3.setStack(ItemStack.EMPTY);
						slot3.onTakeItem(player, itemStack2x);
					}
				} else if (itemStack2x.isEmpty()) {
					if (slot3.canInsert(itemStack3)) {
						int o = slot3.getMaxItemCount(itemStack3);
						if (itemStack3.getCount() > o) {
							slot3.setStack(itemStack3.split(o));
						} else {
							slot3.setStack(itemStack3);
							playerInventory.setStack(clickData, ItemStack.EMPTY);
						}
					}
				} else if (slot3.canTakeItems(player) && slot3.canInsert(itemStack3)) {
					int o = slot3.getMaxItemCount(itemStack3);
					if (itemStack3.getCount() > o) {
						slot3.setStack(itemStack3.split(o));
						slot3.onTakeItem(player, itemStack2x);
						if (!playerInventory.insertStack(itemStack2x)) {
							player.dropItem(itemStack2x, true);
						}
					} else {
						slot3.setStack(itemStack3);
						playerInventory.setStack(clickData, itemStack2x);
						slot3.onTakeItem(player, itemStack2x);
					}
				}
			}
		} else if (actionType == SlotActionType.CLONE && player.getAbilities().creativeMode && playerInventory.getCursorStack().isEmpty() && slotIndex >= 0) {
			Slot slot3 = this.slots.get(slotIndex);
			if (slot3.hasStack()) {
				ItemStack itemStack3 = slot3.getStack().copy();
				itemStack3.setCount(itemStack3.getMaxCount());
				playerInventory.setCursorStack(itemStack3);
			}
		} else if (actionType == SlotActionType.THROW && playerInventory.getCursorStack().isEmpty() && slotIndex >= 0) {
			Slot slot3 = this.slots.get(slotIndex);
			int j = clickData == 0 ? 1 : slot3.getStack().getCount();
			ItemStack itemStack2x = slot3.method_32753(j, Integer.MAX_VALUE, player);
			player.dropItem(itemStack2x, true);
		} else if (actionType == SlotActionType.PICKUP_ALL && slotIndex >= 0) {
			Slot slot3 = this.slots.get(slotIndex);
			ItemStack itemStack3 = playerInventory.getCursorStack();
			if (!itemStack3.isEmpty() && (!slot3.hasStack() || !slot3.canTakeItems(player))) {
				int k = clickData == 0 ? 0 : this.slots.size() - 1;
				int o = clickData == 0 ? 1 : -1;

				for (int n = 0; n < 2; n++) {
					for (int p = k; p >= 0 && p < this.slots.size() && itemStack3.getCount() < itemStack3.getMaxCount(); p += o) {
						Slot slot4 = this.slots.get(p);
						if (slot4.hasStack() && canInsertItemIntoSlot(slot4, itemStack3, true) && slot4.canTakeItems(player) && this.canInsertIntoSlot(itemStack3, slot4)) {
							ItemStack itemStack8 = slot4.getStack();
							if (n != 0 || itemStack8.getCount() != itemStack8.getMaxCount()) {
								ItemStack itemStack9 = slot4.method_32753(itemStack8.getCount(), itemStack3.getMaxCount() - itemStack3.getCount(), player);
								itemStack3.increment(itemStack9.getCount());
							}
						}
					}
				}
			}

			this.sendContentUpdates();
		}

		return itemStack;
	}

	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return true;
	}

	public void close(PlayerEntity player) {
		PlayerInventory playerInventory = player.getInventory();
		if (!playerInventory.getCursorStack().isEmpty()) {
			player.dropItem(playerInventory.getCursorStack(), false);
			playerInventory.setCursorStack(ItemStack.EMPTY);
		}
	}

	protected void dropInventory(PlayerEntity player, Inventory inventory) {
		if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected()) {
			for (int i = 0; i < inventory.size(); i++) {
				player.dropItem(inventory.removeStack(i), false);
			}
		} else {
			for (int i = 0; i < inventory.size(); i++) {
				PlayerInventory playerInventory = player.getInventory();
				if (playerInventory.player instanceof ServerPlayerEntity) {
					playerInventory.offerOrDrop(inventory.removeStack(i));
				}
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
				Slot slot = this.slots.get(i);
				ItemStack itemStack = slot.getStack();
				if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
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
				Slot slotx = this.slots.get(i);
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
			return stage == 1 ? true : stage == 2 && player.getAbilities().creativeMode;
		}
	}

	protected void endQuickCraft() {
		this.quickCraftButton = 0;
		this.quickCraftSlots.clear();
	}

	public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
		boolean bl = slot == null || !slot.hasStack();
		return !bl && ItemStack.canCombine(stack, slot.getStack()) ? slot.getStack().getCount() + (allowOverflow ? 0 : stack.getCount()) <= stack.getMaxCount() : bl;
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
