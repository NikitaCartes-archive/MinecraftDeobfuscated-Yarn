/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class CartographyTableScreenHandler
extends ScreenHandler {
    private final ScreenHandlerContext context;
    private long lastTakeResultTime;
    public final Inventory inventory = new SimpleInventory(2){

        @Override
        public void markDirty() {
            CartographyTableScreenHandler.this.onContentChanged(this);
            super.markDirty();
        }
    };
    private final CraftingResultInventory resultInventory = new CraftingResultInventory(){

        @Override
        public void markDirty() {
            CartographyTableScreenHandler.this.onContentChanged(this);
            super.markDirty();
        }
    };

    public CartographyTableScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public CartographyTableScreenHandler(int syncId, PlayerInventory inventory, final ScreenHandlerContext context) {
        super(ScreenHandlerType.CARTOGRAPHY_TABLE, syncId);
        int i;
        this.context = context;
        this.addSlot(new Slot(this.inventory, 0, 15, 15){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.FILLED_MAP;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 15, 52){

            @Override
            public boolean canInsert(ItemStack stack) {
                Item item = stack.getItem();
                return item == Items.PAPER || item == Items.MAP || item == Items.GLASS_PANE;
            }
        });
        this.addSlot(new Slot(this.resultInventory, 2, 145, 39){

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
                ((Slot)CartographyTableScreenHandler.this.slots.get(0)).takeStack(1);
                ((Slot)CartographyTableScreenHandler.this.slots.get(1)).takeStack(1);
                stack.getItem().onCraft(stack, player.world, player);
                context.run((world, blockPos) -> {
                    long l = world.getTime();
                    if (CartographyTableScreenHandler.this.lastTakeResultTime != l) {
                        world.playSound(null, (BlockPos)blockPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        CartographyTableScreenHandler.this.lastTakeResultTime = l;
                    }
                });
                return super.onTakeItem(player, stack);
            }
        });
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return CartographyTableScreenHandler.canUse(this.context, player, Blocks.CARTOGRAPHY_TABLE);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.inventory.getStack(0);
        ItemStack itemStack2 = this.inventory.getStack(1);
        ItemStack itemStack3 = this.resultInventory.getStack(2);
        if (!itemStack3.isEmpty() && (itemStack.isEmpty() || itemStack2.isEmpty())) {
            this.resultInventory.removeStack(2);
        } else if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
            this.updateResult(itemStack, itemStack2, itemStack3);
        }
    }

    private void updateResult(ItemStack map, ItemStack item, ItemStack oldResult) {
        this.context.run((world, blockPos) -> {
            ItemStack itemStack4;
            Item item = item.getItem();
            MapState mapState = FilledMapItem.getMapState(map, world);
            if (mapState == null) {
                return;
            }
            if (item == Items.PAPER && !mapState.locked && mapState.scale < 4) {
                itemStack4 = map.copy();
                itemStack4.setCount(1);
                itemStack4.getOrCreateTag().putInt("map_scale_direction", 1);
                this.sendContentUpdates();
            } else if (item == Items.GLASS_PANE && !mapState.locked) {
                itemStack4 = map.copy();
                itemStack4.setCount(1);
                itemStack4.getOrCreateTag().putBoolean("map_to_lock", true);
                this.sendContentUpdates();
            } else if (item == Items.MAP) {
                itemStack4 = map.copy();
                itemStack4.setCount(2);
                this.sendContentUpdates();
            } else {
                this.resultInventory.removeStack(2);
                this.sendContentUpdates();
                return;
            }
            if (!ItemStack.areEqual(itemStack4, oldResult)) {
                this.resultInventory.setStack(2, itemStack4);
                this.sendContentUpdates();
            }
        });
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.resultInventory && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2;
            ItemStack itemStack3 = itemStack2 = slot.getStack();
            Item item = itemStack3.getItem();
            itemStack = itemStack3.copy();
            if (index == 2) {
                item.onCraft(itemStack3, player.world, player);
                if (!this.insertItem(itemStack3, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemStack3, itemStack);
            } else if (index == 1 || index == 0 ? !this.insertItem(itemStack3, 3, 39, false) : (item == Items.FILLED_MAP ? !this.insertItem(itemStack3, 0, 1, false) : (item == Items.PAPER || item == Items.MAP || item == Items.GLASS_PANE ? !this.insertItem(itemStack3, 1, 2, false) : (index >= 3 && index < 30 ? !this.insertItem(itemStack3, 30, 39, false) : index >= 30 && index < 39 && !this.insertItem(itemStack3, 3, 30, false))))) {
                return ItemStack.EMPTY;
            }
            if (itemStack3.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            slot.markDirty();
            if (itemStack3.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack3);
            this.sendContentUpdates();
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.resultInventory.removeStack(2);
        this.context.run((world, blockPos) -> this.dropInventory(player, playerEntity.world, this.inventory));
    }
}

