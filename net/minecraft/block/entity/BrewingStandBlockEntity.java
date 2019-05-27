/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.container.Container;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class BrewingStandBlockEntity
extends LockableContainerBlockEntity
implements SidedInventory,
Tickable {
    private static final int[] TOP_SLOTS = new int[]{3};
    private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
    private DefaultedList<ItemStack> inventory = DefaultedList.create(5, ItemStack.EMPTY);
    private int brewTime;
    private boolean[] slotsEmptyLastTick;
    private Item itemBrewing;
    private int fuel;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int i) {
            switch (i) {
                case 0: {
                    return BrewingStandBlockEntity.this.brewTime;
                }
                case 1: {
                    return BrewingStandBlockEntity.this.fuel;
                }
            }
            return 0;
        }

        @Override
        public void set(int i, int j) {
            switch (i) {
                case 0: {
                    BrewingStandBlockEntity.this.brewTime = j;
                    break;
                }
                case 1: {
                    BrewingStandBlockEntity.this.fuel = j;
                }
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public BrewingStandBlockEntity() {
        super(BlockEntityType.BREWING_STAND);
    }

    @Override
    protected Component getContainerName() {
        return new TranslatableComponent("container.brewing", new Object[0]);
    }

    @Override
    public int getInvSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        boolean[] bls;
        ItemStack itemStack = this.inventory.get(4);
        if (this.fuel <= 0 && itemStack.getItem() == Items.BLAZE_POWDER) {
            this.fuel = 20;
            itemStack.decrement(1);
            this.markDirty();
        }
        boolean bl = this.canCraft();
        boolean bl2 = this.brewTime > 0;
        ItemStack itemStack2 = this.inventory.get(3);
        if (bl2) {
            boolean bl3;
            --this.brewTime;
            boolean bl4 = bl3 = this.brewTime == 0;
            if (bl3 && bl) {
                this.craft();
                this.markDirty();
            } else if (!bl) {
                this.brewTime = 0;
                this.markDirty();
            } else if (this.itemBrewing != itemStack2.getItem()) {
                this.brewTime = 0;
                this.markDirty();
            }
        } else if (bl && this.fuel > 0) {
            --this.fuel;
            this.brewTime = 400;
            this.itemBrewing = itemStack2.getItem();
            this.markDirty();
        }
        if (!this.world.isClient && !Arrays.equals(bls = this.getSlotsEmpty(), this.slotsEmptyLastTick)) {
            this.slotsEmptyLastTick = bls;
            BlockState blockState = this.world.getBlockState(this.getPos());
            if (!(blockState.getBlock() instanceof BrewingStandBlock)) {
                return;
            }
            for (int i = 0; i < BrewingStandBlock.BOTTLE_PROPERTIES.length; ++i) {
                blockState = (BlockState)blockState.with(BrewingStandBlock.BOTTLE_PROPERTIES[i], bls[i]);
            }
            this.world.setBlockState(this.pos, blockState, 2);
        }
    }

    public boolean[] getSlotsEmpty() {
        boolean[] bls = new boolean[3];
        for (int i = 0; i < 3; ++i) {
            if (this.inventory.get(i).isEmpty()) continue;
            bls[i] = true;
        }
        return bls;
    }

    private boolean canCraft() {
        ItemStack itemStack = this.inventory.get(3);
        if (itemStack.isEmpty()) {
            return false;
        }
        if (!BrewingRecipeRegistry.isValidIngredient(itemStack)) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            ItemStack itemStack2 = this.inventory.get(i);
            if (itemStack2.isEmpty() || !BrewingRecipeRegistry.hasRecipe(itemStack2, itemStack)) continue;
            return true;
        }
        return false;
    }

    private void craft() {
        ItemStack itemStack = this.inventory.get(3);
        for (int i = 0; i < 3; ++i) {
            this.inventory.set(i, BrewingRecipeRegistry.craft(itemStack, this.inventory.get(i)));
        }
        itemStack.decrement(1);
        BlockPos blockPos = this.getPos();
        if (itemStack.getItem().hasRecipeRemainder()) {
            ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
            if (itemStack.isEmpty()) {
                itemStack = itemStack2;
            } else if (!this.world.isClient) {
                ItemScatterer.spawn(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
            }
        }
        this.inventory.set(3, itemStack);
        this.world.playLevelEvent(1035, blockPos, 0);
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
        Inventories.fromTag(compoundTag, this.inventory);
        this.brewTime = compoundTag.getShort("BrewTime");
        this.fuel = compoundTag.getByte("Fuel");
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putShort("BrewTime", (short)this.brewTime);
        Inventories.toTag(compoundTag, this.inventory);
        compoundTag.putByte("Fuel", (byte)this.fuel);
        return compoundTag;
    }

    @Override
    public ItemStack getInvStack(int i) {
        if (i >= 0 && i < this.inventory.size()) {
            return this.inventory.get(i);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack takeInvStack(int i, int j) {
        return Inventories.splitStack(this.inventory, i, j);
    }

    @Override
    public ItemStack removeInvStack(int i) {
        return Inventories.removeStack(this.inventory, i);
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        if (i >= 0 && i < this.inventory.size()) {
            this.inventory.set(i, itemStack);
        }
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return !(playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
    }

    @Override
    public boolean isValidInvStack(int i, ItemStack itemStack) {
        if (i == 3) {
            return BrewingRecipeRegistry.isValidIngredient(itemStack);
        }
        Item item = itemStack.getItem();
        if (i == 4) {
            return item == Items.BLAZE_POWDER;
        }
        return (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE) && this.getInvStack(i).isEmpty();
    }

    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        if (direction == Direction.UP) {
            return TOP_SLOTS;
        }
        if (direction == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
        return this.isValidInvStack(i, itemStack);
    }

    @Override
    public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
        if (i == 3) {
            return itemStack.getItem() == Items.GLASS_BOTTLE;
        }
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        return new BrewingStandContainer(i, playerInventory, this, this.propertyDelegate);
    }
}

