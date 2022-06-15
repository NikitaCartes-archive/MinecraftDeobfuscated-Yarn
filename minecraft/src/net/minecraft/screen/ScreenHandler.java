package net.minecraft.screen;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
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
import org.slf4j.Logger;

public abstract class ScreenHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
	/**
	 * A special slot index value ({@value}) indicating that the player has clicked outside the main panel
	 * of a screen. Used for dropping the cursor stack.
	 */
	public static final int EMPTY_SPACE_SLOT_INDEX = -999;
	public static final int field_30731 = 0;
	public static final int field_30732 = 1;
	public static final int field_30733 = 2;
	public static final int field_30734 = 0;
	public static final int field_30735 = 1;
	public static final int field_30736 = 2;
	public static final int field_30737 = Integer.MAX_VALUE;
	/**
	 * A list of item stacks that is used for tracking changes in {@link #sendContentUpdates()}.
	 */
	private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
	public final DefaultedList<Slot> slots = DefaultedList.of();
	private final List<Property> properties = Lists.<Property>newArrayList();
	private ItemStack cursorStack = ItemStack.EMPTY;
	private final DefaultedList<ItemStack> previousTrackedStacks = DefaultedList.of();
	private final IntList trackedPropertyValues = new IntArrayList();
	private ItemStack previousCursorStack = ItemStack.EMPTY;
	private int revision;
	@Nullable
	private final ScreenHandlerType<?> type;
	public final int syncId;
	private int quickCraftButton = -1;
	private int quickCraftStage;
	private final Set<Slot> quickCraftSlots = Sets.<Slot>newHashSet();
	private final List<ScreenHandlerListener> listeners = Lists.<ScreenHandlerListener>newArrayList();
	@Nullable
	private ScreenHandlerSyncHandler syncHandler;
	private boolean disableSync;

	protected ScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
		this.type = type;
		this.syncId = syncId;
	}

	protected static boolean canUse(ScreenHandlerContext context, PlayerEntity player, Block block) {
		return context.get(
			(world, pos) -> !world.getBlockState(pos).isOf(block)
					? false
					: player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0,
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

	public boolean isValid(int slot) {
		return slot == -1 || slot == -999 || slot < this.slots.size();
	}

	protected Slot addSlot(Slot slot) {
		slot.id = this.slots.size();
		this.slots.add(slot);
		this.trackedStacks.add(ItemStack.EMPTY);
		this.previousTrackedStacks.add(ItemStack.EMPTY);
		return slot;
	}

	protected Property addProperty(Property property) {
		this.properties.add(property);
		this.trackedPropertyValues.add(0);
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
			this.sendContentUpdates();
		}
	}

	public void updateSyncHandler(ScreenHandlerSyncHandler handler) {
		this.syncHandler = handler;
		this.syncState();
	}

	public void syncState() {
		int i = 0;

		for (int j = this.slots.size(); i < j; i++) {
			this.previousTrackedStacks.set(i, this.slots.get(i).getStack().copy());
		}

		this.previousCursorStack = this.getCursorStack().copy();
		i = 0;

		for (int j = this.properties.size(); i < j; i++) {
			this.trackedPropertyValues.set(i, ((Property)this.properties.get(i)).get());
		}

		if (this.syncHandler != null) {
			this.syncHandler.updateState(this, this.previousTrackedStacks, this.previousCursorStack, this.trackedPropertyValues.toIntArray());
		}
	}

	public void removeListener(ScreenHandlerListener listener) {
		this.listeners.remove(listener);
	}

	public DefaultedList<ItemStack> getStacks() {
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (Slot slot : this.slots) {
			defaultedList.add(slot.getStack());
		}

		return defaultedList;
	}

	/**
	 * Sends updates to listeners if any properties or slot stacks have changed.
	 */
	public void sendContentUpdates() {
		for (int i = 0; i < this.slots.size(); i++) {
			ItemStack itemStack = this.slots.get(i).getStack();
			Supplier<ItemStack> supplier = Suppliers.memoize(itemStack::copy);
			this.updateTrackedSlot(i, itemStack, supplier);
			this.checkSlotUpdates(i, itemStack, supplier);
		}

		this.checkCursorStackUpdates();

		for (int i = 0; i < this.properties.size(); i++) {
			Property property = (Property)this.properties.get(i);
			int j = property.get();
			if (property.hasChanged()) {
				this.notifyPropertyUpdate(i, j);
			}

			this.checkPropertyUpdates(i, j);
		}
	}

	public void updateToClient() {
		for (int i = 0; i < this.slots.size(); i++) {
			ItemStack itemStack = this.slots.get(i).getStack();
			this.updateTrackedSlot(i, itemStack, itemStack::copy);
		}

		for (int i = 0; i < this.properties.size(); i++) {
			Property property = (Property)this.properties.get(i);
			if (property.hasChanged()) {
				this.notifyPropertyUpdate(i, property.get());
			}
		}

		this.syncState();
	}

	private void notifyPropertyUpdate(int index, int value) {
		for (ScreenHandlerListener screenHandlerListener : this.listeners) {
			screenHandlerListener.onPropertyUpdate(this, index, value);
		}
	}

	private void updateTrackedSlot(int slot, ItemStack stack, Supplier<ItemStack> copySupplier) {
		ItemStack itemStack = this.trackedStacks.get(slot);
		if (!ItemStack.areEqual(itemStack, stack)) {
			ItemStack itemStack2 = (ItemStack)copySupplier.get();
			this.trackedStacks.set(slot, itemStack2);

			for (ScreenHandlerListener screenHandlerListener : this.listeners) {
				screenHandlerListener.onSlotUpdate(this, slot, itemStack2);
			}
		}
	}

	private void checkSlotUpdates(int slot, ItemStack stack, Supplier<ItemStack> copySupplier) {
		if (!this.disableSync) {
			ItemStack itemStack = this.previousTrackedStacks.get(slot);
			if (!ItemStack.areEqual(itemStack, stack)) {
				ItemStack itemStack2 = (ItemStack)copySupplier.get();
				this.previousTrackedStacks.set(slot, itemStack2);
				if (this.syncHandler != null) {
					this.syncHandler.updateSlot(this, slot, itemStack2);
				}
			}
		}
	}

	private void checkPropertyUpdates(int id, int value) {
		if (!this.disableSync) {
			int i = this.trackedPropertyValues.getInt(id);
			if (i != value) {
				this.trackedPropertyValues.set(id, value);
				if (this.syncHandler != null) {
					this.syncHandler.updateProperty(this, id, value);
				}
			}
		}
	}

	private void checkCursorStackUpdates() {
		if (!this.disableSync) {
			if (!ItemStack.areEqual(this.getCursorStack(), this.previousCursorStack)) {
				this.previousCursorStack = this.getCursorStack().copy();
				if (this.syncHandler != null) {
					this.syncHandler.updateCursorStack(this, this.previousCursorStack);
				}
			}
		}
	}

	public void setPreviousTrackedSlot(int slot, ItemStack stack) {
		this.previousTrackedStacks.set(slot, stack.copy());
	}

	public void setPreviousTrackedSlotMutable(int slot, ItemStack stack) {
		if (slot >= 0 && slot < this.previousTrackedStacks.size()) {
			this.previousTrackedStacks.set(slot, stack);
		} else {
			LOGGER.debug("Incorrect slot index: {} available slots: {}", slot, this.previousTrackedStacks.size());
		}
	}

	public void setPreviousCursorStack(ItemStack stack) {
		this.previousCursorStack = stack.copy();
	}

	public boolean onButtonClick(PlayerEntity player, int id) {
		return false;
	}

	public Slot getSlot(int index) {
		return this.slots.get(index);
	}

	public abstract ItemStack transferSlot(PlayerEntity player, int index);

	/**
	 * Performs a slot click. This can behave in many different ways depending mainly on the action type.
	 * 
	 * @param actionType the type of slot click, check the docs for each {@link SlotActionType} value for details
	 */
	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		try {
			this.internalOnSlotClick(slotIndex, button, actionType, player);
		} catch (Exception var8) {
			CrashReport crashReport = CrashReport.create(var8, "Container click");
			CrashReportSection crashReportSection = crashReport.addElement("Click info");
			crashReportSection.add("Menu Type", (CrashCallable<String>)(() -> this.type != null ? Registry.SCREEN_HANDLER.getId(this.type).toString() : "<no type>"));
			crashReportSection.add("Menu Class", (CrashCallable<String>)(() -> this.getClass().getCanonicalName()));
			crashReportSection.add("Slot Count", this.slots.size());
			crashReportSection.add("Slot", slotIndex);
			crashReportSection.add("Button", button);
			crashReportSection.add("Type", actionType);
			throw new CrashException(crashReport);
		}
	}

	/**
	 * The actual logic that handles a slot click. Called by {@link #onSlotClick
	 * (int, int, SlotActionType, PlayerEntity)} in a try-catch block that wraps
	 * exceptions from this method into a crash report.
	 */
	private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		PlayerInventory playerInventory = player.getInventory();
		if (actionType == SlotActionType.QUICK_CRAFT) {
			int i = this.quickCraftStage;
			this.quickCraftStage = unpackQuickCraftStage(button);
			if ((i != 1 || this.quickCraftStage != 2) && i != this.quickCraftStage) {
				this.endQuickCraft();
			} else if (this.getCursorStack().isEmpty()) {
				this.endQuickCraft();
			} else if (this.quickCraftStage == 0) {
				this.quickCraftButton = unpackQuickCraftButton(button);
				if (shouldQuickCraftContinue(this.quickCraftButton, player)) {
					this.quickCraftStage = 1;
					this.quickCraftSlots.clear();
				} else {
					this.endQuickCraft();
				}
			} else if (this.quickCraftStage == 1) {
				Slot slot = this.slots.get(slotIndex);
				ItemStack itemStack = this.getCursorStack();
				if (canInsertItemIntoSlot(slot, itemStack, true)
					&& slot.canInsert(itemStack)
					&& (this.quickCraftButton == 2 || itemStack.getCount() > this.quickCraftSlots.size())
					&& this.canInsertIntoSlot(slot)) {
					this.quickCraftSlots.add(slot);
				}
			} else if (this.quickCraftStage == 2) {
				if (!this.quickCraftSlots.isEmpty()) {
					if (this.quickCraftSlots.size() == 1) {
						int j = ((Slot)this.quickCraftSlots.iterator().next()).id;
						this.endQuickCraft();
						this.internalOnSlotClick(j, this.quickCraftButton, SlotActionType.PICKUP, player);
						return;
					}

					ItemStack itemStack2 = this.getCursorStack().copy();
					int k = this.getCursorStack().getCount();

					for (Slot slot2 : this.quickCraftSlots) {
						ItemStack itemStack3 = this.getCursorStack();
						if (slot2 != null
							&& canInsertItemIntoSlot(slot2, itemStack3, true)
							&& slot2.canInsert(itemStack3)
							&& (this.quickCraftButton == 2 || itemStack3.getCount() >= this.quickCraftSlots.size())
							&& this.canInsertIntoSlot(slot2)) {
							ItemStack itemStack4 = itemStack2.copy();
							int l = slot2.hasStack() ? slot2.getStack().getCount() : 0;
							calculateStackSize(this.quickCraftSlots, this.quickCraftButton, itemStack4, l);
							int m = Math.min(itemStack4.getMaxCount(), slot2.getMaxItemCount(itemStack4));
							if (itemStack4.getCount() > m) {
								itemStack4.setCount(m);
							}

							k -= itemStack4.getCount() - l;
							slot2.setStack(itemStack4);
						}
					}

					itemStack2.setCount(k);
					this.setCursorStack(itemStack2);
				}

				this.endQuickCraft();
			} else {
				this.endQuickCraft();
			}
		} else if (this.quickCraftStage != 0) {
			this.endQuickCraft();
		} else if ((actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE) && (button == 0 || button == 1)) {
			ClickType clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;
			if (slotIndex == EMPTY_SPACE_SLOT_INDEX) {
				if (!this.getCursorStack().isEmpty()) {
					if (clickType == ClickType.LEFT) {
						player.dropItem(this.getCursorStack(), true);
						this.setCursorStack(ItemStack.EMPTY);
					} else {
						player.dropItem(this.getCursorStack().split(1), true);
					}
				}
			} else if (actionType == SlotActionType.QUICK_MOVE) {
				if (slotIndex < 0) {
					return;
				}

				Slot slot = this.slots.get(slotIndex);
				if (!slot.canTakeItems(player)) {
					return;
				}

				ItemStack itemStack = this.transferSlot(player, slotIndex);

				while (!itemStack.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot.getStack(), itemStack)) {
					itemStack = this.transferSlot(player, slotIndex);
				}
			} else {
				if (slotIndex < 0) {
					return;
				}

				Slot slot = this.slots.get(slotIndex);
				ItemStack itemStack = slot.getStack();
				ItemStack itemStack5 = this.getCursorStack();
				player.onPickupSlotClick(itemStack5, slot.getStack(), clickType);
				if (!itemStack5.onStackClicked(slot, clickType, player) && !itemStack.onClicked(itemStack5, slot, clickType, player, this.getCursorStackReference())) {
					if (itemStack.isEmpty()) {
						if (!itemStack5.isEmpty()) {
							int n = clickType == ClickType.LEFT ? itemStack5.getCount() : 1;
							this.setCursorStack(slot.insertStack(itemStack5, n));
						}
					} else if (slot.canTakeItems(player)) {
						if (itemStack5.isEmpty()) {
							int n = clickType == ClickType.LEFT ? itemStack.getCount() : (itemStack.getCount() + 1) / 2;
							Optional<ItemStack> optional = slot.tryTakeStackRange(n, Integer.MAX_VALUE, player);
							optional.ifPresent(stack -> {
								this.setCursorStack(stack);
								slot.onTakeItem(player, stack);
							});
						} else if (slot.canInsert(itemStack5)) {
							if (ItemStack.canCombine(itemStack, itemStack5)) {
								int n = clickType == ClickType.LEFT ? itemStack5.getCount() : 1;
								this.setCursorStack(slot.insertStack(itemStack5, n));
							} else if (itemStack5.getCount() <= slot.getMaxItemCount(itemStack5)) {
								this.setCursorStack(itemStack);
								slot.setStack(itemStack5);
							}
						} else if (ItemStack.canCombine(itemStack, itemStack5)) {
							Optional<ItemStack> optional2 = slot.tryTakeStackRange(itemStack.getCount(), itemStack5.getMaxCount() - itemStack5.getCount(), player);
							optional2.ifPresent(stack -> {
								itemStack5.increment(stack.getCount());
								slot.onTakeItem(player, stack);
							});
						}
					}
				}

				slot.markDirty();
			}
		} else if (actionType == SlotActionType.SWAP) {
			Slot slot3 = this.slots.get(slotIndex);
			ItemStack itemStack2 = playerInventory.getStack(button);
			ItemStack itemStack = slot3.getStack();
			if (!itemStack2.isEmpty() || !itemStack.isEmpty()) {
				if (itemStack2.isEmpty()) {
					if (slot3.canTakeItems(player)) {
						playerInventory.setStack(button, itemStack);
						slot3.onTake(itemStack.getCount());
						slot3.setStack(ItemStack.EMPTY);
						slot3.onTakeItem(player, itemStack);
					}
				} else if (itemStack.isEmpty()) {
					if (slot3.canInsert(itemStack2)) {
						int o = slot3.getMaxItemCount(itemStack2);
						if (itemStack2.getCount() > o) {
							slot3.setStack(itemStack2.split(o));
						} else {
							playerInventory.setStack(button, ItemStack.EMPTY);
							slot3.setStack(itemStack2);
						}
					}
				} else if (slot3.canTakeItems(player) && slot3.canInsert(itemStack2)) {
					int o = slot3.getMaxItemCount(itemStack2);
					if (itemStack2.getCount() > o) {
						slot3.setStack(itemStack2.split(o));
						slot3.onTakeItem(player, itemStack);
						if (!playerInventory.insertStack(itemStack)) {
							player.dropItem(itemStack, true);
						}
					} else {
						playerInventory.setStack(button, itemStack);
						slot3.setStack(itemStack2);
						slot3.onTakeItem(player, itemStack);
					}
				}
			}
		} else if (actionType == SlotActionType.CLONE && player.getAbilities().creativeMode && this.getCursorStack().isEmpty() && slotIndex >= 0) {
			Slot slot3 = this.slots.get(slotIndex);
			if (slot3.hasStack()) {
				ItemStack itemStack2 = slot3.getStack().copy();
				itemStack2.setCount(itemStack2.getMaxCount());
				this.setCursorStack(itemStack2);
			}
		} else if (actionType == SlotActionType.THROW && this.getCursorStack().isEmpty() && slotIndex >= 0) {
			Slot slot3 = this.slots.get(slotIndex);
			int j = button == 0 ? 1 : slot3.getStack().getCount();
			ItemStack itemStack = slot3.takeStackRange(j, Integer.MAX_VALUE, player);
			player.dropItem(itemStack, true);
		} else if (actionType == SlotActionType.PICKUP_ALL && slotIndex >= 0) {
			Slot slot3 = this.slots.get(slotIndex);
			ItemStack itemStack2 = this.getCursorStack();
			if (!itemStack2.isEmpty() && (!slot3.hasStack() || !slot3.canTakeItems(player))) {
				int k = button == 0 ? 0 : this.slots.size() - 1;
				int o = button == 0 ? 1 : -1;

				for (int n = 0; n < 2; n++) {
					for (int p = k; p >= 0 && p < this.slots.size() && itemStack2.getCount() < itemStack2.getMaxCount(); p += o) {
						Slot slot4 = this.slots.get(p);
						if (slot4.hasStack() && canInsertItemIntoSlot(slot4, itemStack2, true) && slot4.canTakeItems(player) && this.canInsertIntoSlot(itemStack2, slot4)) {
							ItemStack itemStack6 = slot4.getStack();
							if (n != 0 || itemStack6.getCount() != itemStack6.getMaxCount()) {
								ItemStack itemStack7 = slot4.takeStackRange(itemStack6.getCount(), itemStack2.getMaxCount() - itemStack2.getCount(), player);
								itemStack2.increment(itemStack7.getCount());
							}
						}
					}
				}
			}
		}
	}

	private StackReference getCursorStackReference() {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return ScreenHandler.this.getCursorStack();
			}

			@Override
			public boolean set(ItemStack stack) {
				ScreenHandler.this.setCursorStack(stack);
				return true;
			}
		};
	}

	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return true;
	}

	public void close(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			ItemStack itemStack = this.getCursorStack();
			if (!itemStack.isEmpty()) {
				if (player.isAlive() && !((ServerPlayerEntity)player).isDisconnected()) {
					player.getInventory().offerOrDrop(itemStack);
				} else {
					player.dropItem(itemStack, false);
				}

				this.setCursorStack(ItemStack.EMPTY);
			}
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

	public void setStackInSlot(int slot, int revision, ItemStack stack) {
		this.getSlot(slot).setStack(stack);
		this.revision = revision;
	}

	public void updateSlotStacks(int revision, List<ItemStack> stacks, ItemStack cursorStack) {
		for (int i = 0; i < stacks.size(); i++) {
			this.getSlot(i).setStackNoCallbacks((ItemStack)stacks.get(i));
		}

		this.cursorStack = cursorStack;
		this.revision = revision;
	}

	public void setProperty(int id, int value) {
		((Property)this.properties.get(id)).set(value);
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
		this.quickCraftStage = 0;
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

	public void setCursorStack(ItemStack stack) {
		this.cursorStack = stack;
	}

	public ItemStack getCursorStack() {
		return this.cursorStack;
	}

	public void disableSyncing() {
		this.disableSync = true;
	}

	public void enableSyncing() {
		this.disableSync = false;
	}

	public void copySharedSlots(ScreenHandler handler) {
		Table<Inventory, Integer, Integer> table = HashBasedTable.create();

		for (int i = 0; i < handler.slots.size(); i++) {
			Slot slot = handler.slots.get(i);
			table.put(slot.inventory, slot.getIndex(), i);
		}

		for (int i = 0; i < this.slots.size(); i++) {
			Slot slot = this.slots.get(i);
			Integer integer = table.get(slot.inventory, slot.getIndex());
			if (integer != null) {
				this.trackedStacks.set(i, handler.trackedStacks.get(integer));
				this.previousTrackedStacks.set(i, handler.previousTrackedStacks.get(integer));
			}
		}
	}

	public OptionalInt getSlotIndex(Inventory inventory, int index) {
		for (int i = 0; i < this.slots.size(); i++) {
			Slot slot = this.slots.get(i);
			if (slot.inventory == inventory && index == slot.getIndex()) {
				return OptionalInt.of(i);
			}
		}

		return OptionalInt.empty();
	}

	public int getRevision() {
		return this.revision;
	}

	public int nextRevision() {
		this.revision = this.revision + 1 & 32767;
		return this.revision;
	}
}
