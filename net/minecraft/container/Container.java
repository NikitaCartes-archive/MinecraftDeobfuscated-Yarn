/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Property;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class Container {
    private final DefaultedList<ItemStack> stackList = DefaultedList.of();
    public final List<Slot> slotList = Lists.newArrayList();
    private final List<Property> properties = Lists.newArrayList();
    @Nullable
    private final ContainerType<?> type;
    public final int syncId;
    @Environment(value=EnvType.CLIENT)
    private short actionId;
    private int quickCraftStage = -1;
    private int quickCraftButton;
    private final Set<Slot> quickCraftSlots = Sets.newHashSet();
    private final List<ContainerListener> listeners = Lists.newArrayList();
    private final Set<PlayerEntity> restrictedPlayers = Sets.newHashSet();

    protected Container(@Nullable ContainerType<?> containerType, int i) {
        this.type = containerType;
        this.syncId = i;
    }

    protected static boolean canUse(BlockContext blockContext, PlayerEntity playerEntity, Block block) {
        return blockContext.run((world, blockPos) -> {
            if (world.getBlockState((BlockPos)blockPos).getBlock() != block) {
                return false;
            }
            return playerEntity.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0;
        }, true);
    }

    public ContainerType<?> getType() {
        if (this.type == null) {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        }
        return this.type;
    }

    protected static void checkContainerSize(Inventory inventory, int i) {
        int j = inventory.getInvSize();
        if (j < i) {
            throw new IllegalArgumentException("Container size " + j + " is smaller than expected " + i);
        }
    }

    protected static void checkContainerDataCount(PropertyDelegate propertyDelegate, int i) {
        int j = propertyDelegate.size();
        if (j < i) {
            throw new IllegalArgumentException("Container data count " + j + " is smaller than expected " + i);
        }
    }

    protected Slot addSlot(Slot slot) {
        slot.id = this.slotList.size();
        this.slotList.add(slot);
        this.stackList.add(ItemStack.EMPTY);
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

    public void addListener(ContainerListener containerListener) {
        if (this.listeners.contains(containerListener)) {
            return;
        }
        this.listeners.add(containerListener);
        containerListener.onContainerRegistered(this, this.getStacks());
        this.sendContentUpdates();
    }

    @Environment(value=EnvType.CLIENT)
    public void removeListener(ContainerListener containerListener) {
        this.listeners.remove(containerListener);
    }

    public DefaultedList<ItemStack> getStacks() {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        for (int i = 0; i < this.slotList.size(); ++i) {
            defaultedList.add(this.slotList.get(i).getStack());
        }
        return defaultedList;
    }

    public void sendContentUpdates() {
        int i;
        for (i = 0; i < this.slotList.size(); ++i) {
            ItemStack itemStack = this.slotList.get(i).getStack();
            ItemStack itemStack2 = this.stackList.get(i);
            if (ItemStack.areEqualIgnoreDamage(itemStack2, itemStack)) continue;
            itemStack2 = itemStack.copy();
            this.stackList.set(i, itemStack2);
            for (ContainerListener containerListener : this.listeners) {
                containerListener.onContainerSlotUpdate(this, i, itemStack2);
            }
        }
        for (i = 0; i < this.properties.size(); ++i) {
            Property property = this.properties.get(i);
            if (!property.detectChanges()) continue;
            for (ContainerListener containerListener2 : this.listeners) {
                containerListener2.onContainerPropertyUpdate(this, i, property.get());
            }
        }
    }

    public boolean onButtonClick(PlayerEntity playerEntity, int i) {
        return false;
    }

    public Slot getSlot(int i) {
        return this.slotList.get(i);
    }

    public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
        Slot slot = this.slotList.get(i);
        if (slot != null) {
            return slot.getStack();
        }
        return ItemStack.EMPTY;
    }

    public ItemStack onSlotClick(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity) {
        ItemStack itemStack = ItemStack.EMPTY;
        PlayerInventory playerInventory = playerEntity.inventory;
        if (slotActionType == SlotActionType.QUICK_CRAFT) {
            int k = this.quickCraftButton;
            this.quickCraftButton = Container.unpackButtonId(j);
            if ((k != 1 || this.quickCraftButton != 2) && k != this.quickCraftButton) {
                this.endQuickCraft();
            } else if (playerInventory.getCursorStack().isEmpty()) {
                this.endQuickCraft();
            } else if (this.quickCraftButton == 0) {
                this.quickCraftStage = Container.unpackQuickCraftStage(j);
                if (Container.shouldQuickCraftContinue(this.quickCraftStage, playerEntity)) {
                    this.quickCraftButton = 1;
                    this.quickCraftSlots.clear();
                } else {
                    this.endQuickCraft();
                }
            } else if (this.quickCraftButton == 1) {
                Slot slot = this.slotList.get(i);
                ItemStack itemStack2 = playerInventory.getCursorStack();
                if (slot != null && Container.canInsertItemIntoSlot(slot, itemStack2, true) && slot.canInsert(itemStack2) && (this.quickCraftStage == 2 || itemStack2.getCount() > this.quickCraftSlots.size()) && this.canInsertIntoSlot(slot)) {
                    this.quickCraftSlots.add(slot);
                }
            } else if (this.quickCraftButton == 2) {
                if (!this.quickCraftSlots.isEmpty()) {
                    ItemStack itemStack3 = playerInventory.getCursorStack().copy();
                    int l = playerInventory.getCursorStack().getCount();
                    for (Slot slot2 : this.quickCraftSlots) {
                        ItemStack itemStack4 = playerInventory.getCursorStack();
                        if (slot2 == null || !Container.canInsertItemIntoSlot(slot2, itemStack4, true) || !slot2.canInsert(itemStack4) || this.quickCraftStage != 2 && itemStack4.getCount() < this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot2)) continue;
                        ItemStack itemStack5 = itemStack3.copy();
                        int m = slot2.hasStack() ? slot2.getStack().getCount() : 0;
                        Container.calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack5, m);
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
                Slot slot3 = this.slotList.get(i);
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
                Slot slot3 = this.slotList.get(i);
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
                            if (Container.canStacksCombine(itemStack3, itemStack2)) {
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
                        } else if (itemStack2.getMaxCount() > 1 && Container.canStacksCombine(itemStack3, itemStack2) && !itemStack3.isEmpty() && (o = itemStack3.getCount()) + itemStack2.getCount() <= itemStack2.getMaxCount()) {
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
        } else if (slotActionType == SlotActionType.SWAP && j >= 0 && j < 9) {
            Slot slot3 = this.slotList.get(i);
            ItemStack itemStack3 = playerInventory.getInvStack(j);
            ItemStack itemStack2 = slot3.getStack();
            if (!itemStack3.isEmpty() || !itemStack2.isEmpty()) {
                if (itemStack3.isEmpty()) {
                    if (slot3.canTakeItems(playerEntity)) {
                        playerInventory.setInvStack(j, itemStack2);
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
                            playerInventory.setInvStack(j, ItemStack.EMPTY);
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
                        playerInventory.setInvStack(j, itemStack2);
                        slot3.onTakeItem(playerEntity, itemStack2);
                    }
                }
            }
        } else if (slotActionType == SlotActionType.CLONE && playerEntity.abilities.creativeMode && playerInventory.getCursorStack().isEmpty() && i >= 0) {
            Slot slot3 = this.slotList.get(i);
            if (slot3 != null && slot3.hasStack()) {
                ItemStack itemStack3 = slot3.getStack().copy();
                itemStack3.setCount(itemStack3.getMaxCount());
                playerInventory.setCursorStack(itemStack3);
            }
        } else if (slotActionType == SlotActionType.THROW && playerInventory.getCursorStack().isEmpty() && i >= 0) {
            Slot slot3 = this.slotList.get(i);
            if (slot3 != null && slot3.hasStack() && slot3.canTakeItems(playerEntity)) {
                ItemStack itemStack3 = slot3.takeStack(j == 0 ? 1 : slot3.getStack().getCount());
                slot3.onTakeItem(playerEntity, itemStack3);
                playerEntity.dropItem(itemStack3, true);
            }
        } else if (slotActionType == SlotActionType.PICKUP_ALL && i >= 0) {
            Slot slot3 = this.slotList.get(i);
            ItemStack itemStack3 = playerInventory.getCursorStack();
            if (!(itemStack3.isEmpty() || slot3 != null && slot3.hasStack() && slot3.canTakeItems(playerEntity))) {
                int l = j == 0 ? 0 : this.slotList.size() - 1;
                int o = j == 0 ? 1 : -1;
                for (int p = 0; p < 2; ++p) {
                    for (int q = l; q >= 0 && q < this.slotList.size() && itemStack3.getCount() < itemStack3.getMaxCount(); q += o) {
                        Slot slot4 = this.slotList.get(q);
                        if (!slot4.hasStack() || !Container.canInsertItemIntoSlot(slot4, itemStack3, true) || !slot4.canTakeItems(playerEntity) || !this.canInsertIntoSlot(itemStack3, slot4)) continue;
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

    public static boolean canStacksCombine(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(itemStack, itemStack2);
    }

    public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
        return true;
    }

    public void close(PlayerEntity playerEntity) {
        PlayerInventory playerInventory = playerEntity.inventory;
        if (!playerInventory.getCursorStack().isEmpty()) {
            playerEntity.dropItem(playerInventory.getCursorStack(), false);
            playerInventory.setCursorStack(ItemStack.EMPTY);
        }
    }

    protected void dropInventory(PlayerEntity playerEntity, World world, Inventory inventory) {
        if (!playerEntity.isAlive() || playerEntity instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerEntity).method_14239()) {
            for (int i = 0; i < inventory.getInvSize(); ++i) {
                playerEntity.dropItem(inventory.removeInvStack(i), false);
            }
            return;
        }
        for (int i = 0; i < inventory.getInvSize(); ++i) {
            playerEntity.inventory.offerOrDrop(world, inventory.removeInvStack(i));
        }
    }

    public void onContentChanged(Inventory inventory) {
        this.sendContentUpdates();
    }

    public void setStackInSlot(int i, ItemStack itemStack) {
        this.getSlot(i).setStack(itemStack);
    }

    @Environment(value=EnvType.CLIENT)
    public void updateSlotStacks(List<ItemStack> list) {
        for (int i = 0; i < list.size(); ++i) {
            this.getSlot(i).setStack(list.get(i));
        }
    }

    public void setProperties(int i, int j) {
        this.properties.get(i).set(j);
    }

    @Environment(value=EnvType.CLIENT)
    public short getNextActionId(PlayerInventory playerInventory) {
        this.actionId = (short)(this.actionId + 1);
        return this.actionId;
    }

    public boolean isRestricted(PlayerEntity playerEntity) {
        return !this.restrictedPlayers.contains(playerEntity);
    }

    public void setPlayerRestriction(PlayerEntity playerEntity, boolean bl) {
        if (bl) {
            this.restrictedPlayers.remove(playerEntity);
        } else {
            this.restrictedPlayers.add(playerEntity);
        }
    }

    public abstract boolean canUse(PlayerEntity var1);

    protected boolean insertItem(ItemStack itemStack, int i, int j, boolean bl) {
        ItemStack itemStack2;
        Slot slot;
        boolean bl2 = false;
        int k = i;
        if (bl) {
            k = j - 1;
        }
        if (itemStack.isStackable()) {
            while (!itemStack.isEmpty() && (bl ? k >= i : k < j)) {
                slot = this.slotList.get(k);
                itemStack2 = slot.getStack();
                if (!itemStack2.isEmpty() && Container.canStacksCombine(itemStack, itemStack2)) {
                    int l = itemStack2.getCount() + itemStack.getCount();
                    if (l <= itemStack.getMaxCount()) {
                        itemStack.setCount(0);
                        itemStack2.setCount(l);
                        slot.markDirty();
                        bl2 = true;
                    } else if (itemStack2.getCount() < itemStack.getMaxCount()) {
                        itemStack.decrement(itemStack.getMaxCount() - itemStack2.getCount());
                        itemStack2.setCount(itemStack.getMaxCount());
                        slot.markDirty();
                        bl2 = true;
                    }
                }
                if (bl) {
                    --k;
                    continue;
                }
                ++k;
            }
        }
        if (!itemStack.isEmpty()) {
            k = bl ? j - 1 : i;
            while (bl ? k >= i : k < j) {
                slot = this.slotList.get(k);
                itemStack2 = slot.getStack();
                if (itemStack2.isEmpty() && slot.canInsert(itemStack)) {
                    if (itemStack.getCount() > slot.getMaxStackAmount()) {
                        slot.setStack(itemStack.split(slot.getMaxStackAmount()));
                    } else {
                        slot.setStack(itemStack.split(itemStack.getCount()));
                    }
                    slot.markDirty();
                    bl2 = true;
                    break;
                }
                if (bl) {
                    --k;
                    continue;
                }
                ++k;
            }
        }
        return bl2;
    }

    public static int unpackQuickCraftStage(int i) {
        return i >> 2 & 3;
    }

    public static int unpackButtonId(int i) {
        return i & 3;
    }

    @Environment(value=EnvType.CLIENT)
    public static int packClickData(int i, int j) {
        return i & 3 | (j & 3) << 2;
    }

    public static boolean shouldQuickCraftContinue(int i, PlayerEntity playerEntity) {
        if (i == 0) {
            return true;
        }
        if (i == 1) {
            return true;
        }
        return i == 2 && playerEntity.abilities.creativeMode;
    }

    protected void endQuickCraft() {
        this.quickCraftButton = 0;
        this.quickCraftSlots.clear();
    }

    public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack itemStack, boolean bl) {
        boolean bl2;
        boolean bl3 = bl2 = slot == null || !slot.hasStack();
        if (!bl2 && itemStack.isItemEqualIgnoreDamage(slot.getStack()) && ItemStack.areTagsEqual(slot.getStack(), itemStack)) {
            return slot.getStack().getCount() + (bl ? 0 : itemStack.getCount()) <= itemStack.getMaxCount();
        }
        return bl2;
    }

    public static void calculateStackSize(Set<Slot> set, int i, ItemStack itemStack, int j) {
        switch (i) {
            case 0: {
                itemStack.setCount(MathHelper.floor((float)itemStack.getCount() / (float)set.size()));
                break;
            }
            case 1: {
                itemStack.setCount(1);
                break;
            }
            case 2: {
                itemStack.setCount(itemStack.getItem().getMaxCount());
            }
        }
        itemStack.increment(j);
    }

    public boolean canInsertIntoSlot(Slot slot) {
        return true;
    }

    public static int calculateComparatorOutput(@Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof Inventory) {
            return Container.calculateComparatorOutput((Inventory)((Object)blockEntity));
        }
        return 0;
    }

    public static int calculateComparatorOutput(@Nullable Inventory inventory) {
        if (inventory == null) {
            return 0;
        }
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < inventory.getInvSize(); ++j) {
            ItemStack itemStack = inventory.getInvStack(j);
            if (itemStack.isEmpty()) continue;
            f += (float)itemStack.getCount() / (float)Math.min(inventory.getInvMaxStackAmount(), itemStack.getMaxCount());
            ++i;
        }
        return MathHelper.floor((f /= (float)inventory.getInvSize()) * 14.0f) + (i > 0 ? 1 : 0);
    }
}

