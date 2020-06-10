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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
     * @throws IllegalArgumentException if the inventory size is smaller than {@code exceptedSize}
     */
    protected static void checkSize(Inventory inventory, int expectedSize) {
        int i = inventory.size();
        if (i < expectedSize) {
            throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + expectedSize);
        }
    }

    /**
     * Checks that the size of the {@code data} is at least as large as the {@code exceptedCount}.
     * 
     * @throws IllegalArgumentException if the {@code data} has a smaller size than {@code exceptedCount}
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
     * @param slotId The ID of the slot clicked on. -999 is a special value used for the cursor stack
     * @param clickData Metadata for the click. Usually equal to the button ID, but can have different meanings depending on the action type. Check the docs for each SlotActionType value
     * @param actionType The type of slot click. Check the docs for each SlotActionType value for details
     * @param player The player
     */
    public ItemStack onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity player) {
        ItemStack itemStack = ItemStack.EMPTY;
        PlayerInventory playerInventory = player.inventory;
        if (actionType == SlotActionType.QUICK_CRAFT) {
            int i = this.quickCraftButton;
            this.quickCraftButton = ScreenHandler.unpackQuickCraftStage(clickData);
            if ((i != 1 || this.quickCraftButton != 2) && i != this.quickCraftButton) {
                this.endQuickCraft();
            } else if (playerInventory.getCursorStack().isEmpty()) {
                this.endQuickCraft();
            } else if (this.quickCraftButton == 0) {
                this.quickCraftStage = ScreenHandler.unpackQuickCraftButton(clickData);
                if (ScreenHandler.shouldQuickCraftContinue(this.quickCraftStage, player)) {
                    this.quickCraftButton = 1;
                    this.quickCraftSlots.clear();
                } else {
                    this.endQuickCraft();
                }
            } else if (this.quickCraftButton == 1) {
                Slot slot = this.slots.get(slotId);
                ItemStack itemStack2 = playerInventory.getCursorStack();
                if (slot != null && ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && slot.canInsert(itemStack2) && (this.quickCraftStage == 2 || itemStack2.getCount() > this.quickCraftSlots.size()) && this.canInsertIntoSlot(slot)) {
                    this.quickCraftSlots.add(slot);
                }
            } else if (this.quickCraftButton == 2) {
                if (!this.quickCraftSlots.isEmpty()) {
                    ItemStack itemStack3 = playerInventory.getCursorStack().copy();
                    int j = playerInventory.getCursorStack().getCount();
                    for (Slot slot2 : this.quickCraftSlots) {
                        ItemStack itemStack4 = playerInventory.getCursorStack();
                        if (slot2 == null || !ScreenHandler.canInsertItemIntoSlot(slot2, itemStack4, true) || !slot2.canInsert(itemStack4) || this.quickCraftStage != 2 && itemStack4.getCount() < this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot2)) continue;
                        ItemStack itemStack5 = itemStack3.copy();
                        int k = slot2.hasStack() ? slot2.getStack().getCount() : 0;
                        ScreenHandler.calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack5, k);
                        int l = Math.min(itemStack5.getMaxCount(), slot2.getMaxStackAmount(itemStack5));
                        if (itemStack5.getCount() > l) {
                            itemStack5.setCount(l);
                        }
                        j -= itemStack5.getCount() - k;
                        slot2.setStack(itemStack5);
                    }
                    itemStack3.setCount(j);
                    playerInventory.setCursorStack(itemStack3);
                }
                this.endQuickCraft();
            } else {
                this.endQuickCraft();
            }
        } else if (this.quickCraftButton != 0) {
            this.endQuickCraft();
        } else if (!(actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE || clickData != 0 && clickData != 1)) {
            if (slotId == -999) {
                if (!playerInventory.getCursorStack().isEmpty()) {
                    if (clickData == 0) {
                        player.dropItem(playerInventory.getCursorStack(), true);
                        playerInventory.setCursorStack(ItemStack.EMPTY);
                    }
                    if (clickData == 1) {
                        player.dropItem(playerInventory.getCursorStack().split(1), true);
                    }
                }
            } else if (actionType == SlotActionType.QUICK_MOVE) {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }
                Slot slot3 = this.slots.get(slotId);
                if (slot3 == null || !slot3.canTakeItems(player)) {
                    return ItemStack.EMPTY;
                }
                ItemStack itemStack3 = this.transferSlot(player, slotId);
                while (!itemStack3.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot3.getStack(), itemStack3)) {
                    itemStack = itemStack3.copy();
                    itemStack3 = this.transferSlot(player, slotId);
                }
            } else {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }
                Slot slot3 = this.slots.get(slotId);
                if (slot3 != null) {
                    ItemStack itemStack3 = slot3.getStack();
                    ItemStack itemStack2 = playerInventory.getCursorStack();
                    if (!itemStack3.isEmpty()) {
                        itemStack = itemStack3.copy();
                    }
                    if (itemStack3.isEmpty()) {
                        if (!itemStack2.isEmpty() && slot3.canInsert(itemStack2)) {
                            int m;
                            int n = m = clickData == 0 ? itemStack2.getCount() : 1;
                            if (m > slot3.getMaxStackAmount(itemStack2)) {
                                m = slot3.getMaxStackAmount(itemStack2);
                            }
                            slot3.setStack(itemStack2.split(m));
                        }
                    } else if (slot3.canTakeItems(player)) {
                        int m;
                        if (itemStack2.isEmpty()) {
                            if (itemStack3.isEmpty()) {
                                slot3.setStack(ItemStack.EMPTY);
                                playerInventory.setCursorStack(ItemStack.EMPTY);
                            } else {
                                int m2 = clickData == 0 ? itemStack3.getCount() : (itemStack3.getCount() + 1) / 2;
                                playerInventory.setCursorStack(slot3.takeStack(m2));
                                if (itemStack3.isEmpty()) {
                                    slot3.setStack(ItemStack.EMPTY);
                                }
                                slot3.onTakeItem(player, playerInventory.getCursorStack());
                            }
                        } else if (slot3.canInsert(itemStack2)) {
                            if (ScreenHandler.canStacksCombine(itemStack3, itemStack2)) {
                                int m3;
                                int n = m3 = clickData == 0 ? itemStack2.getCount() : 1;
                                if (m3 > slot3.getMaxStackAmount(itemStack2) - itemStack3.getCount()) {
                                    m3 = slot3.getMaxStackAmount(itemStack2) - itemStack3.getCount();
                                }
                                if (m3 > itemStack2.getMaxCount() - itemStack3.getCount()) {
                                    m3 = itemStack2.getMaxCount() - itemStack3.getCount();
                                }
                                itemStack2.decrement(m3);
                                itemStack3.increment(m3);
                            } else if (itemStack2.getCount() <= slot3.getMaxStackAmount(itemStack2)) {
                                slot3.setStack(itemStack2);
                                playerInventory.setCursorStack(itemStack3);
                            }
                        } else if (itemStack2.getMaxCount() > 1 && ScreenHandler.canStacksCombine(itemStack3, itemStack2) && !itemStack3.isEmpty() && (m = itemStack3.getCount()) + itemStack2.getCount() <= itemStack2.getMaxCount()) {
                            itemStack2.increment(m);
                            itemStack3 = slot3.takeStack(m);
                            if (itemStack3.isEmpty()) {
                                slot3.setStack(ItemStack.EMPTY);
                            }
                            slot3.onTakeItem(player, playerInventory.getCursorStack());
                        }
                    }
                    slot3.markDirty();
                }
            }
        } else if (actionType == SlotActionType.SWAP) {
            Slot slot3 = this.slots.get(slotId);
            ItemStack itemStack3 = playerInventory.getStack(clickData);
            ItemStack itemStack2 = slot3.getStack();
            if (!itemStack3.isEmpty() || !itemStack2.isEmpty()) {
                if (itemStack3.isEmpty()) {
                    if (slot3.canTakeItems(player)) {
                        playerInventory.setStack(clickData, itemStack2);
                        slot3.onTake(itemStack2.getCount());
                        slot3.setStack(ItemStack.EMPTY);
                        slot3.onTakeItem(player, itemStack2);
                    }
                } else if (itemStack2.isEmpty()) {
                    if (slot3.canInsert(itemStack3)) {
                        int m = slot3.getMaxStackAmount(itemStack3);
                        if (itemStack3.getCount() > m) {
                            slot3.setStack(itemStack3.split(m));
                        } else {
                            slot3.setStack(itemStack3);
                            playerInventory.setStack(clickData, ItemStack.EMPTY);
                        }
                    }
                } else if (slot3.canTakeItems(player) && slot3.canInsert(itemStack3)) {
                    int m = slot3.getMaxStackAmount(itemStack3);
                    if (itemStack3.getCount() > m) {
                        slot3.setStack(itemStack3.split(m));
                        slot3.onTakeItem(player, itemStack2);
                        if (!playerInventory.insertStack(itemStack2)) {
                            player.dropItem(itemStack2, true);
                        }
                    } else {
                        slot3.setStack(itemStack3);
                        playerInventory.setStack(clickData, itemStack2);
                        slot3.onTakeItem(player, itemStack2);
                    }
                }
            }
        } else if (actionType == SlotActionType.CLONE && player.abilities.creativeMode && playerInventory.getCursorStack().isEmpty() && slotId >= 0) {
            Slot slot3 = this.slots.get(slotId);
            if (slot3 != null && slot3.hasStack()) {
                ItemStack itemStack3 = slot3.getStack().copy();
                itemStack3.setCount(itemStack3.getMaxCount());
                playerInventory.setCursorStack(itemStack3);
            }
        } else if (actionType == SlotActionType.THROW && playerInventory.getCursorStack().isEmpty() && slotId >= 0) {
            Slot slot3 = this.slots.get(slotId);
            if (slot3 != null && slot3.hasStack() && slot3.canTakeItems(player)) {
                ItemStack itemStack3 = slot3.takeStack(clickData == 0 ? 1 : slot3.getStack().getCount());
                slot3.onTakeItem(player, itemStack3);
                player.dropItem(itemStack3, true);
            }
        } else if (actionType == SlotActionType.PICKUP_ALL && slotId >= 0) {
            Slot slot3 = this.slots.get(slotId);
            ItemStack itemStack3 = playerInventory.getCursorStack();
            if (!(itemStack3.isEmpty() || slot3 != null && slot3.hasStack() && slot3.canTakeItems(player))) {
                int j = clickData == 0 ? 0 : this.slots.size() - 1;
                int m = clickData == 0 ? 1 : -1;
                for (int n = 0; n < 2; ++n) {
                    for (int o = j; o >= 0 && o < this.slots.size() && itemStack3.getCount() < itemStack3.getMaxCount(); o += m) {
                        Slot slot4 = this.slots.get(o);
                        if (!slot4.hasStack() || !ScreenHandler.canInsertItemIntoSlot(slot4, itemStack3, true) || !slot4.canTakeItems(player) || !this.canInsertIntoSlot(itemStack3, slot4)) continue;
                        ItemStack itemStack6 = slot4.getStack();
                        if (n == 0 && itemStack6.getCount() == itemStack6.getMaxCount()) continue;
                        int l = Math.min(itemStack3.getMaxCount() - itemStack3.getCount(), itemStack6.getCount());
                        ItemStack itemStack7 = slot4.takeStack(l);
                        itemStack3.increment(l);
                        if (itemStack7.isEmpty()) {
                            slot4.setStack(ItemStack.EMPTY);
                        }
                        slot4.onTakeItem(player, itemStack7);
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

    public static void calculateStackSize(Set<Slot> slots, int rmode, ItemStack stack, int stackSize) {
        switch (rmode) {
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

