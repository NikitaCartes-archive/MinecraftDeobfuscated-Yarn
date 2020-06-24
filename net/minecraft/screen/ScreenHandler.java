/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class ScreenHandler {
    /**
     * A list of item stacks that is used for tracking changes in {@link #sendContentUpdates()}.
     */
    private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
    public final List<Slot> slots = Lists.newArrayList();
    private final List<Property> properties = Lists.newArrayList();
    @Nullable
    private final ScreenHandlerType<?> type;
    public final int syncId;
    @Environment(value=EnvType.CLIENT)
    private short actionId;
    private int quickCraftStage = -1;
    private int quickCraftButton;
    private final Set<Slot> quickCraftSlots = Sets.newHashSet();
    private final List<ScreenHandlerListener> listeners = Lists.newArrayList();
    private final Set<PlayerEntity> restrictedPlayers = Sets.newHashSet();

    protected ScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        this.type = type;
        this.syncId = syncId;
    }

    protected static boolean canUse(ScreenHandlerContext context, PlayerEntity player, Block block) {
        return context.run((world, blockPos) -> {
            if (!world.getBlockState((BlockPos)blockPos).isOf(block)) {
                return false;
            }
            return player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0;
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
        return slot;
    }

    protected Property addProperty(Property property) {
        this.properties.add(property);
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
        listener.onHandlerRegistered(this, this.getStacks());
        this.sendContentUpdates();
    }

    @Environment(value=EnvType.CLIENT)
    public void removeListener(ScreenHandlerListener listener) {
        this.listeners.remove(listener);
    }

    public DefaultedList<ItemStack> getStacks() {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        for (int i = 0; i < this.slots.size(); ++i) {
            defaultedList.add(this.slots.get(i).getStack());
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
            ItemStack itemStack2 = this.trackedStacks.get(i);
            if (ItemStack.areEqual(itemStack2, itemStack)) continue;
            ItemStack itemStack3 = itemStack.copy();
            this.trackedStacks.set(i, itemStack3);
            for (ScreenHandlerListener screenHandlerListener : this.listeners) {
                screenHandlerListener.onSlotUpdate(this, i, itemStack3);
            }
        }
        for (i = 0; i < this.properties.size(); ++i) {
            Property property = this.properties.get(i);
            if (!property.hasChanged()) continue;
            for (ScreenHandlerListener screenHandlerListener2 : this.listeners) {
                screenHandlerListener2.onPropertyUpdate(this, i, property.get());
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
        Slot slot = this.slots.get(index);
        if (slot != null) {
            return slot.getStack();
        }
        return ItemStack.EMPTY;
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
        } catch (Exception exception) {
            CrashReport crashReport = CrashReport.create(exception, "Container click");
            CrashReportSection crashReportSection = crashReport.addElement("Click info");
            crashReportSection.add("Menu Type", () -> this.type != null ? Registry.SCREEN_HANDLER.getId(this.type).toString() : "<no type>");
            crashReportSection.add("Menu Class", () -> this.getClass().getCanonicalName());
            crashReportSection.add("Slot Count", this.slots.size());
            crashReportSection.add("Slot", i);
            crashReportSection.add("Button", j);
            crashReportSection.add("Type", (Object)actionType);
            throw new CrashException(crashReport);
        }
    }

    private ItemStack method_30010(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity) {
        ItemStack itemStack = ItemStack.EMPTY;
        PlayerInventory playerInventory = playerEntity.inventory;
        if (slotActionType == SlotActionType.QUICK_CRAFT) {
            int k = this.quickCraftButton;
            this.quickCraftButton = ScreenHandler.unpackQuickCraftStage(j);
            if ((k != 1 || this.quickCraftButton != 2) && k != this.quickCraftButton) {
                this.endQuickCraft();
            } else if (playerInventory.getCursorStack().isEmpty()) {
                this.endQuickCraft();
            } else if (this.quickCraftButton == 0) {
                this.quickCraftStage = ScreenHandler.unpackQuickCraftButton(j);
                if (ScreenHandler.shouldQuickCraftContinue(this.quickCraftStage, playerEntity)) {
                    this.quickCraftButton = 1;
                    this.quickCraftSlots.clear();
                } else {
                    this.endQuickCraft();
                }
            } else if (this.quickCraftButton == 1) {
                Slot slot = this.slots.get(i);
                ItemStack itemStack2 = playerInventory.getCursorStack();
                if (slot != null && ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && slot.canInsert(itemStack2) && (this.quickCraftStage == 2 || itemStack2.getCount() > this.quickCraftSlots.size()) && this.canInsertIntoSlot(slot)) {
                    this.quickCraftSlots.add(slot);
                }
            } else if (this.quickCraftButton == 2) {
                if (!this.quickCraftSlots.isEmpty()) {
                    ItemStack itemStack3 = playerInventory.getCursorStack().copy();
                    int l = playerInventory.getCursorStack().getCount();
                    for (Slot slot2 : this.quickCraftSlots) {
                        ItemStack itemStack4 = playerInventory.getCursorStack();
                        if (slot2 == null || !ScreenHandler.canInsertItemIntoSlot(slot2, itemStack4, true) || !slot2.canInsert(itemStack4) || this.quickCraftStage != 2 && itemStack4.getCount() < this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot2)) continue;
                        ItemStack itemStack5 = itemStack3.copy();
                        int m = slot2.hasStack() ? slot2.getStack().getCount() : 0;
                        ScreenHandler.calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack5, m);
                        int n = Math.min(itemStack5.getMaxCount(), slot2.getMaxStackAmount(itemStack5));
                        if (itemStack5.getCount() > n) {
                            itemStack5.setCount(n);
                        }
                        l -= itemStack5.getCount() - m;
                        slot2.setStack(itemStack5);
                    }
                    itemStack3.setCount(l);
                    playerInventory.setCursorStack(itemStack3);
                }
                this.endQuickCraft();
            } else {
                this.endQuickCraft();
            }
        } else if (this.quickCraftButton != 0) {
            this.endQuickCraft();
        } else if (!(slotActionType != SlotActionType.PICKUP && slotActionType != SlotActionType.QUICK_MOVE || j != 0 && j != 1)) {
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
                Slot slot3 = this.slots.get(i);
                if (slot3 == null || !slot3.canTakeItems(playerEntity)) {
                    return ItemStack.EMPTY;
                }
                ItemStack itemStack3 = this.transferSlot(playerEntity, i);
                while (!itemStack3.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot3.getStack(), itemStack3)) {
                    itemStack = itemStack3.copy();
                    itemStack3 = this.transferSlot(playerEntity, i);
                }
            } else {
                if (i < 0) {
                    return ItemStack.EMPTY;
                }
                Slot slot3 = this.slots.get(i);
                if (slot3 != null) {
                    ItemStack itemStack3 = slot3.getStack();
                    ItemStack itemStack2 = playerInventory.getCursorStack();
                    if (!itemStack3.isEmpty()) {
                        itemStack = itemStack3.copy();
                    }
                    if (itemStack3.isEmpty()) {
                        if (!itemStack2.isEmpty() && slot3.canInsert(itemStack2)) {
                            int o;
                            int n = o = j == 0 ? itemStack2.getCount() : 1;
                            if (o > slot3.getMaxStackAmount(itemStack2)) {
                                o = slot3.getMaxStackAmount(itemStack2);
                            }
                            slot3.setStack(itemStack2.split(o));
                        }
                    } else if (slot3.canTakeItems(playerEntity)) {
                        int o;
                        if (itemStack2.isEmpty()) {
                            if (itemStack3.isEmpty()) {
                                slot3.setStack(ItemStack.EMPTY);
                                playerInventory.setCursorStack(ItemStack.EMPTY);
                            } else {
                                int o2 = j == 0 ? itemStack3.getCount() : (itemStack3.getCount() + 1) / 2;
                                playerInventory.setCursorStack(slot3.takeStack(o2));
                                if (itemStack3.isEmpty()) {
                                    slot3.setStack(ItemStack.EMPTY);
                                }
                                slot3.onTakeItem(playerEntity, playerInventory.getCursorStack());
                            }
                        } else if (slot3.canInsert(itemStack2)) {
                            if (ScreenHandler.canStacksCombine(itemStack3, itemStack2)) {
                                int o3;
                                int n = o3 = j == 0 ? itemStack2.getCount() : 1;
                                if (o3 > slot3.getMaxStackAmount(itemStack2) - itemStack3.getCount()) {
                                    o3 = slot3.getMaxStackAmount(itemStack2) - itemStack3.getCount();
                                }
                                if (o3 > itemStack2.getMaxCount() - itemStack3.getCount()) {
                                    o3 = itemStack2.getMaxCount() - itemStack3.getCount();
                                }
                                itemStack2.decrement(o3);
                                itemStack3.increment(o3);
                            } else if (itemStack2.getCount() <= slot3.getMaxStackAmount(itemStack2)) {
                                slot3.setStack(itemStack2);
                                playerInventory.setCursorStack(itemStack3);
                            }
                        } else if (itemStack2.getMaxCount() > 1 && ScreenHandler.canStacksCombine(itemStack3, itemStack2) && !itemStack3.isEmpty() && (o = itemStack3.getCount()) + itemStack2.getCount() <= itemStack2.getMaxCount()) {
                            itemStack2.increment(o);
                            itemStack3 = slot3.takeStack(o);
                            if (itemStack3.isEmpty()) {
                                slot3.setStack(ItemStack.EMPTY);
                            }
                            slot3.onTakeItem(playerEntity, playerInventory.getCursorStack());
                        }
                    }
                    slot3.markDirty();
                }
            }
        } else if (slotActionType == SlotActionType.SWAP) {
            Slot slot3 = this.slots.get(i);
            ItemStack itemStack3 = playerInventory.getStack(j);
            ItemStack itemStack2 = slot3.getStack();
            if (!itemStack3.isEmpty() || !itemStack2.isEmpty()) {
                if (itemStack3.isEmpty()) {
                    if (slot3.canTakeItems(playerEntity)) {
                        playerInventory.setStack(j, itemStack2);
                        slot3.onTake(itemStack2.getCount());
                        slot3.setStack(ItemStack.EMPTY);
                        slot3.onTakeItem(playerEntity, itemStack2);
                    }
                } else if (itemStack2.isEmpty()) {
                    if (slot3.canInsert(itemStack3)) {
                        int o = slot3.getMaxStackAmount(itemStack3);
                        if (itemStack3.getCount() > o) {
                            slot3.setStack(itemStack3.split(o));
                        } else {
                            slot3.setStack(itemStack3);
                            playerInventory.setStack(j, ItemStack.EMPTY);
                        }
                    }
                } else if (slot3.canTakeItems(playerEntity) && slot3.canInsert(itemStack3)) {
                    int o = slot3.getMaxStackAmount(itemStack3);
                    if (itemStack3.getCount() > o) {
                        slot3.setStack(itemStack3.split(o));
                        slot3.onTakeItem(playerEntity, itemStack2);
                        if (!playerInventory.insertStack(itemStack2)) {
                            playerEntity.dropItem(itemStack2, true);
                        }
                    } else {
                        slot3.setStack(itemStack3);
                        playerInventory.setStack(j, itemStack2);
                        slot3.onTakeItem(playerEntity, itemStack2);
                    }
                }
            }
        } else if (slotActionType == SlotActionType.CLONE && playerEntity.abilities.creativeMode && playerInventory.getCursorStack().isEmpty() && i >= 0) {
            Slot slot3 = this.slots.get(i);
            if (slot3 != null && slot3.hasStack()) {
                ItemStack itemStack3 = slot3.getStack().copy();
                itemStack3.setCount(itemStack3.getMaxCount());
                playerInventory.setCursorStack(itemStack3);
            }
        } else if (slotActionType == SlotActionType.THROW && playerInventory.getCursorStack().isEmpty() && i >= 0) {
            Slot slot3 = this.slots.get(i);
            if (slot3 != null && slot3.hasStack() && slot3.canTakeItems(playerEntity)) {
                ItemStack itemStack3 = slot3.takeStack(j == 0 ? 1 : slot3.getStack().getCount());
                slot3.onTakeItem(playerEntity, itemStack3);
                playerEntity.dropItem(itemStack3, true);
            }
        } else if (slotActionType == SlotActionType.PICKUP_ALL && i >= 0) {
            Slot slot3 = this.slots.get(i);
            ItemStack itemStack3 = playerInventory.getCursorStack();
            if (!(itemStack3.isEmpty() || slot3 != null && slot3.hasStack() && slot3.canTakeItems(playerEntity))) {
                int l = j == 0 ? 0 : this.slots.size() - 1;
                int o = j == 0 ? 1 : -1;
                for (int p = 0; p < 2; ++p) {
                    for (int q = l; q >= 0 && q < this.slots.size() && itemStack3.getCount() < itemStack3.getMaxCount(); q += o) {
                        Slot slot4 = this.slots.get(q);
                        if (!slot4.hasStack() || !ScreenHandler.canInsertItemIntoSlot(slot4, itemStack3, true) || !slot4.canTakeItems(playerEntity) || !this.canInsertIntoSlot(itemStack3, slot4)) continue;
                        ItemStack itemStack6 = slot4.getStack();
                        if (p == 0 && itemStack6.getCount() == itemStack6.getMaxCount()) continue;
                        int n = Math.min(itemStack3.getMaxCount() - itemStack3.getCount(), itemStack6.getCount());
                        ItemStack itemStack7 = slot4.takeStack(n);
                        itemStack3.increment(n);
                        if (itemStack7.isEmpty()) {
                            slot4.setStack(ItemStack.EMPTY);
                        }
                        slot4.onTakeItem(playerEntity, itemStack7);
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
            for (int i = 0; i < inventory.size(); ++i) {
                player.dropItem(inventory.removeStack(i), false);
            }
            return;
        }
        for (int i = 0; i < inventory.size(); ++i) {
            player.inventory.offerOrDrop(world, inventory.removeStack(i));
        }
    }

    public void onContentChanged(Inventory inventory) {
        this.sendContentUpdates();
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        this.getSlot(slot).setStack(stack);
    }

    @Environment(value=EnvType.CLIENT)
    public void updateSlotStacks(List<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            this.getSlot(i).setStack(stacks.get(i));
        }
    }

    public void setProperty(int id, int value) {
        this.properties.get(id).set(value);
    }

    @Environment(value=EnvType.CLIENT)
    public short getNextActionId(PlayerInventory playerInventory) {
        this.actionId = (short)(this.actionId + 1);
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
                if (!itemStack.isEmpty() && ScreenHandler.canStacksCombine(stack, itemStack)) {
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
                    if (stack.getCount() > slot.getMaxStackAmount()) {
                        slot.setStack(stack.split(slot.getMaxStackAmount()));
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

    @Environment(value=EnvType.CLIENT)
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
        return stage == 2 && player.abilities.creativeMode;
    }

    protected void endQuickCraft() {
        this.quickCraftButton = 0;
        this.quickCraftSlots.clear();
    }

    public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
        boolean bl;
        boolean bl2 = bl = slot == null || !slot.hasStack();
        if (!bl && stack.isItemEqualIgnoreDamage(slot.getStack()) && ItemStack.areTagsEqual(slot.getStack(), stack)) {
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
}

