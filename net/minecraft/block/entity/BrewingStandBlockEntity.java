/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BrewingStandBlockEntity
extends LockableContainerBlockEntity
implements SidedInventory {
    private static final int[] TOP_SLOTS = new int[]{3};
    private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int brewTime;
    private boolean[] slotsEmptyLastTick;
    private Item itemBrewing;
    private int fuel;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
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
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    BrewingStandBlockEntity.this.brewTime = value;
                    break;
                }
                case 1: {
                    BrewingStandBlockEntity.this.fuel = value;
                }
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public BrewingStandBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.BREWING_STAND, blockPos, blockState);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.brewing");
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, BrewingStandBlockEntity brewingStandBlockEntity) {
        ItemStack itemStack = brewingStandBlockEntity.inventory.get(4);
        if (brewingStandBlockEntity.fuel <= 0 && itemStack.isOf(Items.BLAZE_POWDER)) {
            brewingStandBlockEntity.fuel = 20;
            itemStack.decrement(1);
            BrewingStandBlockEntity.markDirty(world, blockPos, blockState);
        }
        boolean bl = BrewingStandBlockEntity.canCraft(brewingStandBlockEntity.inventory);
        boolean bl2 = brewingStandBlockEntity.brewTime > 0;
        ItemStack itemStack2 = brewingStandBlockEntity.inventory.get(3);
        if (bl2) {
            boolean bl3;
            --brewingStandBlockEntity.brewTime;
            boolean bl4 = bl3 = brewingStandBlockEntity.brewTime == 0;
            if (bl3 && bl) {
                BrewingStandBlockEntity.craft(world, blockPos, brewingStandBlockEntity.inventory);
                BrewingStandBlockEntity.markDirty(world, blockPos, blockState);
            } else if (!bl || !itemStack2.isOf(brewingStandBlockEntity.itemBrewing)) {
                brewingStandBlockEntity.brewTime = 0;
                BrewingStandBlockEntity.markDirty(world, blockPos, blockState);
            }
        } else if (bl && brewingStandBlockEntity.fuel > 0) {
            --brewingStandBlockEntity.fuel;
            brewingStandBlockEntity.brewTime = 400;
            brewingStandBlockEntity.itemBrewing = itemStack2.getItem();
            BrewingStandBlockEntity.markDirty(world, blockPos, blockState);
        }
        boolean[] bls = brewingStandBlockEntity.getSlotsEmpty();
        if (!Arrays.equals(bls, brewingStandBlockEntity.slotsEmptyLastTick)) {
            brewingStandBlockEntity.slotsEmptyLastTick = bls;
            BlockState blockState2 = blockState;
            if (!(blockState2.getBlock() instanceof BrewingStandBlock)) {
                return;
            }
            for (int i = 0; i < BrewingStandBlock.BOTTLE_PROPERTIES.length; ++i) {
                blockState2 = (BlockState)blockState2.with(BrewingStandBlock.BOTTLE_PROPERTIES[i], bls[i]);
            }
            world.setBlockState(blockPos, blockState2, 2);
        }
    }

    private boolean[] getSlotsEmpty() {
        boolean[] bls = new boolean[3];
        for (int i = 0; i < 3; ++i) {
            if (this.inventory.get(i).isEmpty()) continue;
            bls[i] = true;
        }
        return bls;
    }

    private static boolean canCraft(DefaultedList<ItemStack> defaultedList) {
        ItemStack itemStack = defaultedList.get(3);
        if (itemStack.isEmpty()) {
            return false;
        }
        if (!BrewingRecipeRegistry.isValidIngredient(itemStack)) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            ItemStack itemStack2 = defaultedList.get(i);
            if (itemStack2.isEmpty() || !BrewingRecipeRegistry.hasRecipe(itemStack2, itemStack)) continue;
            return true;
        }
        return false;
    }

    private static void craft(World world, BlockPos blockPos, DefaultedList<ItemStack> defaultedList) {
        ItemStack itemStack = defaultedList.get(3);
        for (int i = 0; i < 3; ++i) {
            defaultedList.set(i, BrewingRecipeRegistry.craft(itemStack, defaultedList.get(i)));
        }
        itemStack.decrement(1);
        if (itemStack.getItem().hasRecipeRemainder()) {
            ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
            if (itemStack.isEmpty()) {
                itemStack = itemStack2;
            } else {
                ItemScatterer.spawn(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
            }
        }
        defaultedList.set(3, itemStack);
        world.syncWorldEvent(1035, blockPos, 0);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);
        this.brewTime = tag.getShort("BrewTime");
        this.fuel = tag.getByte("Fuel");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putShort("BrewTime", (short)this.brewTime);
        Inventories.toTag(tag, this.inventory);
        tag.putByte("Fuel", (byte)this.fuel);
        return tag;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot >= 0 && slot < this.inventory.size()) {
            return this.inventory.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            this.inventory.set(slot, stack);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 3) {
            return BrewingRecipeRegistry.isValidIngredient(stack);
        }
        if (slot == 4) {
            return stack.isOf(Items.BLAZE_POWDER);
        }
        return (stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.GLASS_BOTTLE)) && this.getStack(slot).isEmpty();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == 3) {
            return stack.isOf(Items.GLASS_BOTTLE);
        }
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BrewingStandScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}

