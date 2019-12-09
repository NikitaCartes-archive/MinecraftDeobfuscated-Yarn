/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.HopperContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HopperBlockEntity
extends LootableContainerBlockEntity
implements Hopper,
Tickable {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private long lastTickTime;

    public HopperBlockEntity() {
        super(BlockEntityType.HOPPER);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag)) {
            Inventories.fromTag(tag, this.inventory);
        }
        this.transferCooldown = tag.getInt("TransferCooldown");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory);
        }
        tag.putInt("TransferCooldown", this.transferCooldown);
        return tag;
    }

    @Override
    public int getInvSize() {
        return this.inventory.size();
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        this.checkLootInteraction(null);
        return Inventories.splitStack(this.getInvStackList(), slot, amount);
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(slot, stack);
        if (stack.getCount() > this.getInvMaxStackAmount()) {
            stack.setCount(this.getInvMaxStackAmount());
        }
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.hopper", new Object[0]);
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient) {
            return;
        }
        --this.transferCooldown;
        this.lastTickTime = this.world.getTime();
        if (!this.needsCooldown()) {
            this.setCooldown(0);
            this.insertAndExtract(() -> HopperBlockEntity.extract(this));
        }
    }

    private boolean insertAndExtract(Supplier<Boolean> extractMethod) {
        if (this.world == null || this.world.isClient) {
            return false;
        }
        if (!this.needsCooldown() && this.getCachedState().get(HopperBlock.ENABLED).booleanValue()) {
            boolean bl = false;
            if (!this.isInvEmpty()) {
                bl = this.insert();
            }
            if (!this.isFull()) {
                bl |= extractMethod.get().booleanValue();
            }
            if (bl) {
                this.setCooldown(8);
                this.markDirty();
                return true;
            }
        }
        return false;
    }

    private boolean isFull() {
        for (ItemStack itemStack : this.inventory) {
            if (!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxCount()) continue;
            return false;
        }
        return true;
    }

    private boolean insert() {
        Inventory inventory = this.getOutputInventory();
        if (inventory == null) {
            return false;
        }
        Direction direction = this.getCachedState().get(HopperBlock.FACING).getOpposite();
        if (this.isInventoryFull(inventory, direction)) {
            return false;
        }
        for (int i = 0; i < this.getInvSize(); ++i) {
            if (this.getInvStack(i).isEmpty()) continue;
            ItemStack itemStack = this.getInvStack(i).copy();
            ItemStack itemStack2 = HopperBlockEntity.transfer(this, inventory, this.takeInvStack(i, 1), direction);
            if (itemStack2.isEmpty()) {
                inventory.markDirty();
                return true;
            }
            this.setInvStack(i, itemStack);
        }
        return false;
    }

    private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory) {
            return IntStream.of(((SidedInventory)inventory).getInvAvailableSlots(side));
        }
        return IntStream.range(0, inventory.getInvSize());
    }

    private boolean isInventoryFull(Inventory inv, Direction direction) {
        return HopperBlockEntity.getAvailableSlots(inv, direction).allMatch(i -> {
            ItemStack itemStack = inv.getInvStack(i);
            return itemStack.getCount() >= itemStack.getMaxCount();
        });
    }

    private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        return HopperBlockEntity.getAvailableSlots(inv, facing).allMatch(i -> inv.getInvStack(i).isEmpty());
    }

    public static boolean extract(Hopper hopper) {
        Inventory inventory = HopperBlockEntity.getInputInventory(hopper);
        if (inventory != null) {
            Direction direction = Direction.DOWN;
            if (HopperBlockEntity.isInventoryEmpty(inventory, direction)) {
                return false;
            }
            return HopperBlockEntity.getAvailableSlots(inventory, direction).anyMatch(i -> HopperBlockEntity.extract(hopper, inventory, i, direction));
        }
        for (ItemEntity itemEntity : HopperBlockEntity.getInputItemEntities(hopper)) {
            if (!HopperBlockEntity.extract(hopper, itemEntity)) continue;
            return true;
        }
        return false;
    }

    private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        ItemStack itemStack = inventory.getInvStack(slot);
        if (!itemStack.isEmpty() && HopperBlockEntity.canExtract(inventory, itemStack, slot, side)) {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = HopperBlockEntity.transfer(inventory, hopper, inventory.takeInvStack(slot, 1), null);
            if (itemStack3.isEmpty()) {
                inventory.markDirty();
                return true;
            }
            inventory.setInvStack(slot, itemStack2);
        }
        return false;
    }

    public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
        boolean bl = false;
        ItemStack itemStack = itemEntity.getStack().copy();
        ItemStack itemStack2 = HopperBlockEntity.transfer(null, inventory, itemStack, null);
        if (itemStack2.isEmpty()) {
            bl = true;
            itemEntity.remove();
        } else {
            itemEntity.setStack(itemStack2);
        }
        return bl;
    }

    public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
        if (to instanceof SidedInventory && side != null) {
            SidedInventory sidedInventory = (SidedInventory)to;
            int[] is = sidedInventory.getInvAvailableSlots(side);
            for (int i = 0; i < is.length && !stack.isEmpty(); ++i) {
                stack = HopperBlockEntity.transfer(from, to, stack, is[i], side);
            }
        } else {
            int j = to.getInvSize();
            for (int k = 0; k < j && !stack.isEmpty(); ++k) {
                stack = HopperBlockEntity.transfer(from, to, stack, k, side);
            }
        }
        return stack;
    }

    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        if (!inventory.isValidInvStack(slot, stack)) {
            return false;
        }
        return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsertInvStack(slot, stack, side);
    }

    private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
        return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtractInvStack(slot, stack, facing);
    }

    private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction direction) {
        ItemStack itemStack = to.getInvStack(slot);
        if (HopperBlockEntity.canInsert(to, stack, slot, direction)) {
            int j;
            boolean bl = false;
            boolean bl2 = to.isInvEmpty();
            if (itemStack.isEmpty()) {
                to.setInvStack(slot, stack);
                stack = ItemStack.EMPTY;
                bl = true;
            } else if (HopperBlockEntity.canMergeItems(itemStack, stack)) {
                int i = stack.getMaxCount() - itemStack.getCount();
                j = Math.min(stack.getCount(), i);
                stack.decrement(j);
                itemStack.increment(j);
                boolean bl3 = bl = j > 0;
            }
            if (bl) {
                HopperBlockEntity hopperBlockEntity;
                if (bl2 && to instanceof HopperBlockEntity && !(hopperBlockEntity = (HopperBlockEntity)to).isDisabled()) {
                    j = 0;
                    if (from instanceof HopperBlockEntity) {
                        HopperBlockEntity hopperBlockEntity2 = (HopperBlockEntity)from;
                        if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
                            j = 1;
                        }
                    }
                    hopperBlockEntity.setCooldown(8 - j);
                }
                to.markDirty();
            }
        }
        return stack;
    }

    @Nullable
    private Inventory getOutputInventory() {
        Direction direction = this.getCachedState().get(HopperBlock.FACING);
        return HopperBlockEntity.getInventoryAt(this.getWorld(), this.pos.offset(direction));
    }

    @Nullable
    public static Inventory getInputInventory(Hopper hopper) {
        return HopperBlockEntity.getInventoryAt(hopper.getWorld(), hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
    }

    public static List<ItemEntity> getInputItemEntities(Hopper hopper) {
        return hopper.getInputAreaShape().getBoundingBoxes().stream().flatMap(box -> hopper.getWorld().getEntities(ItemEntity.class, box.offset(hopper.getHopperX() - 0.5, hopper.getHopperY() - 0.5, hopper.getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY).stream()).collect(Collectors.toList());
    }

    @Nullable
    public static Inventory getInventoryAt(World world, BlockPos blockPos) {
        return HopperBlockEntity.getInventoryAt(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
    }

    @Nullable
    public static Inventory getInventoryAt(World world, double x, double y, double z) {
        List<Entity> list;
        BlockEntity blockEntity;
        Inventory inventory = null;
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof InventoryProvider) {
            inventory = ((InventoryProvider)((Object)block)).getInventory(blockState, world, blockPos);
        } else if (block.hasBlockEntity() && (blockEntity = world.getBlockEntity(blockPos)) instanceof Inventory && (inventory = (Inventory)((Object)blockEntity)) instanceof ChestBlockEntity && block instanceof ChestBlock) {
            inventory = ChestBlock.getInventory((ChestBlock)block, blockState, world, blockPos, true);
        }
        if (inventory == null && !(list = world.getEntities((Entity)null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.VALID_INVENTORIES)).isEmpty()) {
            inventory = (Inventory)((Object)list.get(world.random.nextInt(list.size())));
        }
        return inventory;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        if (first.getItem() != second.getItem()) {
            return false;
        }
        if (first.getDamage() != second.getDamage()) {
            return false;
        }
        if (first.getCount() > first.getMaxCount()) {
            return false;
        }
        return ItemStack.areTagsEqual(first, second);
    }

    @Override
    public double getHopperX() {
        return (double)this.pos.getX() + 0.5;
    }

    @Override
    public double getHopperY() {
        return (double)this.pos.getY() + 0.5;
    }

    @Override
    public double getHopperZ() {
        return (double)this.pos.getZ() + 0.5;
    }

    private void setCooldown(int cooldown) {
        this.transferCooldown = cooldown;
    }

    private boolean needsCooldown() {
        return this.transferCooldown > 0;
    }

    private boolean isDisabled() {
        return this.transferCooldown > 8;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    public void onEntityCollided(Entity entity) {
        if (entity instanceof ItemEntity) {
            BlockPos blockPos = this.getPos();
            if (VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ())), this.getInputAreaShape(), BooleanBiFunction.AND)) {
                this.insertAndExtract(() -> HopperBlockEntity.extract(this, (ItemEntity)entity));
            }
        }
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        return new HopperContainer(i, playerInventory, this);
    }
}

