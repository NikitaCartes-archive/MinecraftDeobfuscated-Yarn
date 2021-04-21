/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CommandItemSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public abstract class ScreenHandler {
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
    private final List<Property> properties = Lists.newArrayList();
    private ItemStack cursorStack = ItemStack.EMPTY;
    private final DefaultedList<ItemStack> previousTrackedStacks = DefaultedList.of();
    private final IntList trackedPropertyValues = new IntArrayList();
    private ItemStack previousCursorStack = ItemStack.EMPTY;
    @Nullable
    private final ScreenHandlerType<?> type;
    public final int syncId;
    private int quickCraftButton = -1;
    private int quickCraftStage;
    private final Set<Slot> quickCraftSlots = Sets.newHashSet();
    private final List<ScreenHandlerListener> listeners = Lists.newArrayList();
    @Nullable
    private ScreenHandlerSyncHandler syncHandler;
    private boolean disableSync;

    protected ScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        this.type = type;
        this.syncId = syncId;
    }

    protected static boolean canUse(ScreenHandlerContext context, PlayerEntity player, Block block) {
        return context.get((world, pos) -> {
            if (!world.getBlockState((BlockPos)pos).isOf(block)) {
                return false;
            }
            return player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0;
        }, true);
    }

    public ScreenHandlerType<?> getType() {
        if (this.type == null) {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        }
        return this.type;
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
        this.previousTrackedStacks.add(ItemStack.EMPTY);
        return slot;
    }

    protected Property addProperty(Property property) {
        this.properties.add(property);
        this.trackedPropertyValues.add(0);
        return property;
    }

    protected void addProperties(PropertyDelegate propertyDelegate) {
        for (int i = 0; i < propertyDelegate.size(); ++i) {
            this.addProperty(Property.create(propertyDelegate, i));
        }
    }

    public void addListener(ScreenHandlerListener listener) {
        if (this.listeners.contains(listener)) {
            return;
        }
        this.listeners.add(listener);
        this.sendContentUpdates();
    }

    public void updateSyncHandler(ScreenHandlerSyncHandler handler) {
        this.syncHandler = handler;
        this.syncState();
    }

    public void syncState() {
        int i;
        int j = this.slots.size();
        for (i = 0; i < j; ++i) {
            this.previousTrackedStacks.set(i, this.slots.get(i).getStack().copy());
        }
        this.previousCursorStack = this.getCursorStack().copy();
        j = this.properties.size();
        for (i = 0; i < j; ++i) {
            this.trackedPropertyValues.set(i, this.properties.get(i).get());
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
        int i;
        for (i = 0; i < this.slots.size(); ++i) {
            ItemStack itemStack = this.slots.get(i).getStack();
            Supplier<ItemStack> supplier = Suppliers.memoize(itemStack::copy);
            this.updateTrackedSlot(i, itemStack, supplier);
            this.checkSlotUpdates(i, itemStack, supplier);
        }
        this.checkCursorStackUpdates();
        for (i = 0; i < this.properties.size(); ++i) {
            Property property = this.properties.get(i);
            int j = property.get();
            if (property.hasChanged()) {
                for (ScreenHandlerListener screenHandlerListener : this.listeners) {
                    screenHandlerListener.onPropertyUpdate(this, i, j);
                }
            }
            this.checkPropertyUpdates(i, j);
        }
    }

    private void updateTrackedSlot(int slot, ItemStack stack, java.util.function.Supplier<ItemStack> copySupplier) {
        ItemStack itemStack = this.trackedStacks.get(slot);
        if (!ItemStack.areEqual(itemStack, stack)) {
            ItemStack itemStack2 = copySupplier.get();
            this.trackedStacks.set(slot, itemStack2);
            for (ScreenHandlerListener screenHandlerListener : this.listeners) {
                screenHandlerListener.onSlotUpdate(this, slot, itemStack2);
            }
        }
    }

    private void checkSlotUpdates(int slot, ItemStack stack, java.util.function.Supplier<ItemStack> copySupplier) {
        if (this.disableSync) {
            return;
        }
        ItemStack itemStack = this.previousTrackedStacks.get(slot);
        if (!ItemStack.areEqual(itemStack, stack)) {
            ItemStack itemStack2 = copySupplier.get();
            this.previousTrackedStacks.set(slot, itemStack2);
            if (this.syncHandler != null) {
                this.syncHandler.updateSlot(this, slot, itemStack2);
            }
        }
    }

    private void checkPropertyUpdates(int id, int value) {
        if (this.disableSync) {
            return;
        }
        int i = this.trackedPropertyValues.getInt(id);
        if (i != value) {
            this.trackedPropertyValues.set(id, value);
            if (this.syncHandler != null) {
                this.syncHandler.updateProperty(this, id, value);
            }
        }
    }

    private void checkCursorStackUpdates() {
        if (this.disableSync) {
            return;
        }
        if (!ItemStack.areEqual(this.getCursorStack(), this.previousCursorStack)) {
            this.previousCursorStack = this.getCursorStack().copy();
            if (this.syncHandler != null) {
                this.syncHandler.updateCursorStack(this, this.previousCursorStack);
            }
        }
    }

    public void setPreviousTrackedSlot(int slot, ItemStack stack) {
        this.previousTrackedStacks.set(slot, stack);
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

    public ItemStack transferSlot(PlayerEntity player, int index) {
        return this.slots.get(index).getStack();
    }

    /**
     * Performs a slot click. This can behave in many different ways depending mainly on the action type.
     * 
     * @param actionType the type of slot click, check the docs for each {@link SlotActionType} value for details
     */
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        try {
            this.internalOnSlotClick(slotIndex, button, actionType, player);
        } catch (Exception exception) {
            CrashReport crashReport = CrashReport.create(exception, "Container click");
            CrashReportSection crashReportSection = crashReport.addElement("Click info");
            crashReportSection.add("Menu Type", () -> this.type != null ? Registry.SCREEN_HANDLER.getId(this.type).toString() : "<no type>");
            crashReportSection.add("Menu Class", () -> this.getClass().getCanonicalName());
            crashReportSection.add("Slot Count", this.slots.size());
            crashReportSection.add("Slot", slotIndex);
            crashReportSection.add("Button", button);
            crashReportSection.add("Type", (Object)actionType);
            throw new CrashException(crashReport);
        }
    }

    /**
     * The actual logic that handles a slot click. Called by {@link #onSlotClick
     * (int, int, SlotActionType, PlayerEntity)} in a try-catch block that wraps
     * exceptions from this method into a crash report.
     */
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        block39: {
            block50: {
                block46: {
                    ItemStack itemStack3;
                    ItemStack itemStack22;
                    Slot slot3;
                    PlayerInventory playerInventory;
                    block49: {
                        block48: {
                            block47: {
                                block44: {
                                    ClickType clickType;
                                    block45: {
                                        block43: {
                                            block37: {
                                                block42: {
                                                    ItemStack itemStack4;
                                                    block41: {
                                                        block40: {
                                                            block38: {
                                                                playerInventory = player.getInventory();
                                                                if (actionType != SlotActionType.QUICK_CRAFT) break block37;
                                                                int i = this.quickCraftStage;
                                                                this.quickCraftStage = ScreenHandler.unpackQuickCraftStage(button);
                                                                if (i == 1 && this.quickCraftStage == 2 || i == this.quickCraftStage) break block38;
                                                                this.endQuickCraft();
                                                                break block39;
                                                            }
                                                            if (!this.getCursorStack().isEmpty()) break block40;
                                                            this.endQuickCraft();
                                                            break block39;
                                                        }
                                                        if (this.quickCraftStage != 0) break block41;
                                                        this.quickCraftButton = ScreenHandler.unpackQuickCraftButton(button);
                                                        if (ScreenHandler.shouldQuickCraftContinue(this.quickCraftButton, player)) {
                                                            this.quickCraftStage = 1;
                                                            this.quickCraftSlots.clear();
                                                        } else {
                                                            this.endQuickCraft();
                                                        }
                                                        break block39;
                                                    }
                                                    if (this.quickCraftStage != 1) break block42;
                                                    Slot slot = this.slots.get(slotIndex);
                                                    if (!ScreenHandler.canInsertItemIntoSlot(slot, itemStack4 = this.getCursorStack(), true) || !slot.canInsert(itemStack4) || this.quickCraftButton != 2 && itemStack4.getCount() <= this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot)) break block39;
                                                    this.quickCraftSlots.add(slot);
                                                    break block39;
                                                }
                                                if (this.quickCraftStage == 2) {
                                                    if (!this.quickCraftSlots.isEmpty()) {
                                                        if (this.quickCraftSlots.size() == 1) {
                                                            int j = this.quickCraftSlots.iterator().next().id;
                                                            this.endQuickCraft();
                                                            this.internalOnSlotClick(j, this.quickCraftButton, SlotActionType.PICKUP, player);
                                                            return;
                                                        }
                                                        ItemStack itemStack23 = this.getCursorStack().copy();
                                                        int k = this.getCursorStack().getCount();
                                                        for (Slot slot2 : this.quickCraftSlots) {
                                                            ItemStack itemStack32 = this.getCursorStack();
                                                            if (slot2 == null || !ScreenHandler.canInsertItemIntoSlot(slot2, itemStack32, true) || !slot2.canInsert(itemStack32) || this.quickCraftButton != 2 && itemStack32.getCount() < this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot2)) continue;
                                                            ItemStack itemStack4 = itemStack23.copy();
                                                            int l = slot2.hasStack() ? slot2.getStack().getCount() : 0;
                                                            ScreenHandler.calculateStackSize(this.quickCraftSlots, this.quickCraftButton, itemStack4, l);
                                                            int m = Math.min(itemStack4.getMaxCount(), slot2.getMaxItemCount(itemStack4));
                                                            if (itemStack4.getCount() > m) {
                                                                itemStack4.setCount(m);
                                                            }
                                                            k -= itemStack4.getCount() - l;
                                                            slot2.setStack(itemStack4);
                                                        }
                                                        itemStack23.setCount(k);
                                                        this.setCursorStack(itemStack23);
                                                    }
                                                    this.endQuickCraft();
                                                } else {
                                                    this.endQuickCraft();
                                                }
                                                break block39;
                                            }
                                            if (this.quickCraftStage == 0) break block43;
                                            this.endQuickCraft();
                                            break block39;
                                        }
                                        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE || button != 0 && button != 1) break block44;
                                        ClickType clickType2 = clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;
                                        if (slotIndex != EMPTY_SPACE_SLOT_INDEX) break block45;
                                        if (this.getCursorStack().isEmpty()) break block39;
                                        if (clickType == ClickType.LEFT) {
                                            player.dropItem(this.getCursorStack(), true);
                                            this.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            player.dropItem(this.getCursorStack().split(1), true);
                                        }
                                        break block39;
                                    }
                                    if (actionType == SlotActionType.QUICK_MOVE) {
                                        if (slotIndex < 0) {
                                            return;
                                        }
                                        Slot slot = this.slots.get(slotIndex);
                                        if (!slot.canTakeItems(player)) {
                                            return;
                                        }
                                        ItemStack itemStack5 = this.transferSlot(player, slotIndex);
                                        while (!itemStack5.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot.getStack(), itemStack5)) {
                                            itemStack5 = this.transferSlot(player, slotIndex);
                                        }
                                    } else {
                                        if (slotIndex < 0) {
                                            return;
                                        }
                                        Slot slot = this.slots.get(slotIndex);
                                        ItemStack itemStack6 = slot.getStack();
                                        ItemStack itemStack5 = this.getCursorStack();
                                        player.onPickupSlotClick(itemStack5, slot.getStack(), clickType);
                                        if (!itemStack5.onStackClicked(slot, clickType, player) && !itemStack6.onClicked(itemStack5, slot, clickType, player, this.getCursorCommandItemSlot())) {
                                            if (itemStack6.isEmpty()) {
                                                if (!itemStack5.isEmpty()) {
                                                    int n = clickType == ClickType.LEFT ? itemStack5.getCount() : 1;
                                                    this.setCursorStack(slot.insertStack(itemStack5, n));
                                                }
                                            } else if (slot.canTakeItems(player)) {
                                                if (itemStack5.isEmpty()) {
                                                    int n = clickType == ClickType.LEFT ? itemStack6.getCount() : (itemStack6.getCount() + 1) / 2;
                                                    Optional<ItemStack> optional = slot.tryTakeStackRange(n, Integer.MAX_VALUE, player);
                                                    optional.ifPresent(itemStack -> {
                                                        this.setCursorStack((ItemStack)itemStack);
                                                        slot.onTakeItem(player, (ItemStack)itemStack);
                                                    });
                                                } else if (slot.canInsert(itemStack5)) {
                                                    if (ItemStack.canCombine(itemStack6, itemStack5)) {
                                                        int n = clickType == ClickType.LEFT ? itemStack5.getCount() : 1;
                                                        this.setCursorStack(slot.insertStack(itemStack5, n));
                                                    } else if (itemStack5.getCount() <= slot.getMaxItemCount(itemStack5)) {
                                                        slot.setStack(itemStack5);
                                                        this.setCursorStack(itemStack6);
                                                    }
                                                } else if (ItemStack.canCombine(itemStack6, itemStack5)) {
                                                    Optional<ItemStack> optional2 = slot.tryTakeStackRange(itemStack6.getCount(), itemStack5.getMaxCount() - itemStack5.getCount(), player);
                                                    optional2.ifPresent(itemStack2 -> {
                                                        itemStack5.increment(itemStack2.getCount());
                                                        slot.onTakeItem(player, (ItemStack)itemStack2);
                                                    });
                                                }
                                            }
                                        }
                                        slot.markDirty();
                                    }
                                    break block39;
                                }
                                if (actionType != SlotActionType.SWAP) break block46;
                                slot3 = this.slots.get(slotIndex);
                                itemStack22 = playerInventory.getStack(button);
                                itemStack3 = slot3.getStack();
                                if (itemStack22.isEmpty() && itemStack3.isEmpty()) break block39;
                                if (!itemStack22.isEmpty()) break block47;
                                if (!slot3.canTakeItems(player)) break block39;
                                playerInventory.setStack(button, itemStack3);
                                slot3.onTake(itemStack3.getCount());
                                slot3.setStack(ItemStack.EMPTY);
                                slot3.onTakeItem(player, itemStack3);
                                break block39;
                            }
                            if (!itemStack3.isEmpty()) break block48;
                            if (!slot3.canInsert(itemStack22)) break block39;
                            int o = slot3.getMaxItemCount(itemStack22);
                            if (itemStack22.getCount() > o) {
                                slot3.setStack(itemStack22.split(o));
                            } else {
                                slot3.setStack(itemStack22);
                                playerInventory.setStack(button, ItemStack.EMPTY);
                            }
                            break block39;
                        }
                        if (!slot3.canTakeItems(player) || !slot3.canInsert(itemStack22)) break block39;
                        int o = slot3.getMaxItemCount(itemStack22);
                        if (itemStack22.getCount() <= o) break block49;
                        slot3.setStack(itemStack22.split(o));
                        slot3.onTakeItem(player, itemStack3);
                        if (playerInventory.insertStack(itemStack3)) break block39;
                        player.dropItem(itemStack3, true);
                        break block39;
                    }
                    slot3.setStack(itemStack22);
                    playerInventory.setStack(button, itemStack3);
                    slot3.onTakeItem(player, itemStack3);
                    break block39;
                }
                if (actionType != SlotActionType.CLONE || !player.getAbilities().creativeMode || !this.getCursorStack().isEmpty() || slotIndex < 0) break block50;
                Slot slot3 = this.slots.get(slotIndex);
                if (!slot3.hasStack()) break block39;
                ItemStack itemStack24 = slot3.getStack().copy();
                itemStack24.setCount(itemStack24.getMaxCount());
                this.setCursorStack(itemStack24);
                break block39;
            }
            if (actionType == SlotActionType.THROW && this.getCursorStack().isEmpty() && slotIndex >= 0) {
                Slot slot3 = this.slots.get(slotIndex);
                int j = button == 0 ? 1 : slot3.getStack().getCount();
                ItemStack itemStack7 = slot3.takeStackRange(j, Integer.MAX_VALUE, player);
                player.dropItem(itemStack7, true);
            } else if (actionType == SlotActionType.PICKUP_ALL && slotIndex >= 0) {
                Slot slot3 = this.slots.get(slotIndex);
                ItemStack itemStack25 = this.getCursorStack();
                if (!(itemStack25.isEmpty() || slot3.hasStack() && slot3.canTakeItems(player))) {
                    int k = button == 0 ? 0 : this.slots.size() - 1;
                    int o = button == 0 ? 1 : -1;
                    for (int n = 0; n < 2; ++n) {
                        for (int p = k; p >= 0 && p < this.slots.size() && itemStack25.getCount() < itemStack25.getMaxCount(); p += o) {
                            Slot slot4 = this.slots.get(p);
                            if (!slot4.hasStack() || !ScreenHandler.canInsertItemIntoSlot(slot4, itemStack25, true) || !slot4.canTakeItems(player) || !this.canInsertIntoSlot(itemStack25, slot4)) continue;
                            ItemStack itemStack6 = slot4.getStack();
                            if (n == 0 && itemStack6.getCount() == itemStack6.getMaxCount()) continue;
                            ItemStack itemStack7 = slot4.takeStackRange(itemStack6.getCount(), itemStack25.getMaxCount() - itemStack25.getCount(), player);
                            itemStack25.increment(itemStack7.getCount());
                        }
                    }
                }
            }
        }
    }

    private CommandItemSlot getCursorCommandItemSlot() {
        return new CommandItemSlot(){

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
        if (!this.getCursorStack().isEmpty()) {
            player.dropItem(this.getCursorStack(), false);
            this.setCursorStack(ItemStack.EMPTY);
        }
    }

    protected void dropInventory(PlayerEntity player, Inventory inventory) {
        if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected()) {
            for (int i = 0; i < inventory.size(); ++i) {
                player.dropItem(inventory.removeStack(i), false);
            }
            return;
        }
        for (int i = 0; i < inventory.size(); ++i) {
            PlayerInventory playerInventory = player.getInventory();
            if (!(playerInventory.player instanceof ServerPlayerEntity)) continue;
            playerInventory.offerOrDrop(inventory.removeStack(i));
        }
    }

    public void onContentChanged(Inventory inventory) {
        this.sendContentUpdates();
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        this.getSlot(slot).setStack(stack);
    }

    public void updateSlotStacks(List<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            this.getSlot(i).setStack(stacks.get(i));
        }
    }

    public void setProperty(int id, int value) {
        this.properties.get(id).set(value);
    }

    public abstract boolean canUse(PlayerEntity var1);

    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        ItemStack itemStack;
        Slot slot;
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }
        if (stack.isStackable()) {
            while (!stack.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
                slot = this.slots.get(i);
                itemStack = slot.getStack();
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
                    --i;
                    continue;
                }
                ++i;
            }
        }
        if (!stack.isEmpty()) {
            i = fromLast ? endIndex - 1 : startIndex;
            while (fromLast ? i >= startIndex : i < endIndex) {
                slot = this.slots.get(i);
                itemStack = slot.getStack();
                if (itemStack.isEmpty() && slot.canInsert(stack)) {
                    if (stack.getCount() > slot.getMaxItemCount()) {
                        slot.setStack(stack.split(slot.getMaxItemCount()));
                    } else {
                        slot.setStack(stack.split(stack.getCount()));
                    }
                    slot.markDirty();
                    bl = true;
                    break;
                }
                if (fromLast) {
                    --i;
                    continue;
                }
                ++i;
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
        }
        if (stage == 1) {
            return true;
        }
        return stage == 2 && player.getAbilities().creativeMode;
    }

    protected void endQuickCraft() {
        this.quickCraftStage = 0;
        this.quickCraftSlots.clear();
    }

    public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
        boolean bl;
        boolean bl2 = bl = slot == null || !slot.hasStack();
        if (!bl && ItemStack.canCombine(stack, slot.getStack())) {
            return slot.getStack().getCount() + (allowOverflow ? 0 : stack.getCount()) <= stack.getMaxCount();
        }
        return bl;
    }

    public static void calculateStackSize(Set<Slot> slots, int mode, ItemStack stack, int stackSize) {
        switch (mode) {
            case 0: {
                stack.setCount(MathHelper.floor((float)stack.getCount() / (float)slots.size()));
                break;
            }
            case 1: {
                stack.setCount(1);
                break;
            }
            case 2: {
                stack.setCount(stack.getItem().getMaxCount());
            }
        }
        stack.increment(stackSize);
    }

    public boolean canInsertIntoSlot(Slot slot) {
        return true;
    }

    public static int calculateComparatorOutput(@Nullable BlockEntity entity) {
        if (entity instanceof Inventory) {
            return ScreenHandler.calculateComparatorOutput((Inventory)((Object)entity));
        }
        return 0;
    }

    public static int calculateComparatorOutput(@Nullable Inventory inventory) {
        if (inventory == null) {
            return 0;
        }
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < inventory.size(); ++j) {
            ItemStack itemStack = inventory.getStack(j);
            if (itemStack.isEmpty()) continue;
            f += (float)itemStack.getCount() / (float)Math.min(inventory.getMaxCountPerStack(), itemStack.getMaxCount());
            ++i;
        }
        return MathHelper.floor((f /= (float)inventory.size()) * 14.0f) + (i > 0 ? 1 : 0);
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
        Slot slot;
        int i;
        HashBasedTable<Inventory, Integer, Integer> table = HashBasedTable.create();
        for (i = 0; i < handler.slots.size(); ++i) {
            slot = handler.slots.get(i);
            table.put(slot.inventory, slot.getIndex(), i);
        }
        for (i = 0; i < this.slots.size(); ++i) {
            slot = this.slots.get(i);
            Integer integer = (Integer)table.get(slot.inventory, slot.getIndex());
            if (integer == null) continue;
            this.trackedStacks.set(i, handler.trackedStacks.get(integer));
            this.previousTrackedStacks.set(i, handler.previousTrackedStacks.get(integer));
        }
    }
}

