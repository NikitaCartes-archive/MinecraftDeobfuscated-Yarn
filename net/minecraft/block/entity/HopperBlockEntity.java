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
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(compoundTag)) {
            Inventories.fromTag(compoundTag, this.inventory);
        }
        this.transferCooldown = compoundTag.getInt("TransferCooldown");
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (!this.serializeLootTable(compoundTag)) {
            Inventories.toTag(compoundTag, this.inventory);
        }
        compoundTag.putInt("TransferCooldown", this.transferCooldown);
        return compoundTag;
    }

    @Override
    public int getInvSize() {
        return this.inventory.size();
    }

    @Override
    public ItemStack takeInvStack(int i, int j) {
        this.checkLootInteraction(null);
        return Inventories.splitStack(this.getInvStackList(), i, j);
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(i, itemStack);
        if (itemStack.getCount() > this.getInvMaxStackAmount()) {
            itemStack.setCount(this.getInvMaxStackAmount());
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

    private boolean insertAndExtract(Supplier<Boolean> supplier) {
        if (this.world == null || this.world.isClient) {
            return false;
        }
        if (!this.needsCooldown() && this.getCachedState().get(HopperBlock.ENABLED).booleanValue()) {
            boolean bl = false;
            if (!this.isInvEmpty()) {
                bl = this.insert();
            }
            if (!this.isFull()) {
                bl |= supplier.get().booleanValue();
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

    private static IntStream getAvailableSlots(Inventory inventory, Direction direction) {
        if (inventory instanceof SidedInventory) {
            return IntStream.of(((SidedInventory)inventory).getInvAvailableSlots(direction));
        }
        return IntStream.range(0, inventory.getInvSize());
    }

    private boolean isInventoryFull(Inventory inventory, Direction direction) {
        return HopperBlockEntity.getAvailableSlots(inventory, direction).allMatch(i -> {
            ItemStack itemStack = inventory.getInvStack(i);
            return itemStack.getCount() >= itemStack.getMaxCount();
        });
    }

    private static boolean isInventoryEmpty(Inventory inventory, Direction direction) {
        return HopperBlockEntity.getAvailableSlots(inventory, direction).allMatch(i -> inventory.getInvStack(i).isEmpty());
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

    private static boolean extract(Hopper hopper, Inventory inventory, int i, Direction direction) {
        ItemStack itemStack = inventory.getInvStack(i);
        if (!itemStack.isEmpty() && HopperBlockEntity.canExtract(inventory, itemStack, i, direction)) {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = HopperBlockEntity.transfer(inventory, hopper, inventory.takeInvStack(i, 1), null);
            if (itemStack3.isEmpty()) {
                inventory.markDirty();
                return true;
            }
            inventory.setInvStack(i, itemStack2);
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

    public static ItemStack transfer(@Nullable Inventory inventory, Inventory inventory2, ItemStack itemStack, @Nullable Direction direction) {
        if (inventory2 instanceof SidedInventory && direction != null) {
            SidedInventory sidedInventory = (SidedInventory)inventory2;
            int[] is = sidedInventory.getInvAvailableSlots(direction);
            for (int i = 0; i < is.length && !itemStack.isEmpty(); ++i) {
                itemStack = HopperBlockEntity.transfer(inventory, inventory2, itemStack, is[i], direction);
            }
        } else {
            int j = inventory2.getInvSize();
            for (int k = 0; k < j && !itemStack.isEmpty(); ++k) {
                itemStack = HopperBlockEntity.transfer(inventory, inventory2, itemStack, k, direction);
            }
        }
        return itemStack;
    }

    private static boolean canInsert(Inventory inventory, ItemStack itemStack, int i, @Nullable Direction direction) {
        if (!inventory.isValidInvStack(i, itemStack)) {
            return false;
        }
        return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsertInvStack(i, itemStack, direction);
    }

    private static boolean canExtract(Inventory inventory, ItemStack itemStack, int i, Direction direction) {
        return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canExtractInvStack(i, itemStack, direction);
    }

    private static ItemStack transfer(@Nullable Inventory inventory, Inventory inventory2, ItemStack itemStack, int i, @Nullable Direction direction) {
        ItemStack itemStack2 = inventory2.getInvStack(i);
        if (HopperBlockEntity.canInsert(inventory2, itemStack, i, direction)) {
            int k;
            boolean bl = false;
            boolean bl2 = inventory2.isInvEmpty();
            if (itemStack2.isEmpty()) {
                inventory2.setInvStack(i, itemStack);
                itemStack = ItemStack.EMPTY;
                bl = true;
            } else if (HopperBlockEntity.canMergeItems(itemStack2, itemStack)) {
                int j = itemStack.getMaxCount() - itemStack2.getCount();
                k = Math.min(itemStack.getCount(), j);
                itemStack.decrement(k);
                itemStack2.increment(k);
                boolean bl3 = bl = k > 0;
            }
            if (bl) {
                HopperBlockEntity hopperBlockEntity;
                if (bl2 && inventory2 instanceof HopperBlockEntity && !(hopperBlockEntity = (HopperBlockEntity)inventory2).isDisabled()) {
                    k = 0;
                    if (inventory instanceof HopperBlockEntity) {
                        HopperBlockEntity hopperBlockEntity2 = (HopperBlockEntity)inventory;
                        if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
                            k = 1;
                        }
                    }
                    hopperBlockEntity.setCooldown(8 - k);
                }
                inventory2.markDirty();
            }
        }
        return itemStack;
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
    public static Inventory getInventoryAt(World world, double d, double e, double f) {
        List<Entity> list;
        BlockEntity blockEntity;
        Inventory inventory = null;
        BlockPos blockPos = new BlockPos(d, e, f);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof InventoryProvider) {
            inventory = ((InventoryProvider)((Object)block)).getInventory(blockState, world, blockPos);
        } else if (block.hasBlockEntity() && (blockEntity = world.getBlockEntity(blockPos)) instanceof Inventory && (inventory = (Inventory)((Object)blockEntity)) instanceof ChestBlockEntity && block instanceof ChestBlock) {
            inventory = ChestBlock.getInventory((ChestBlock)block, blockState, world, blockPos, true);
        }
        if (inventory == null && !(list = world.getEntities((Entity)null, new Box(d - 0.5, e - 0.5, f - 0.5, d + 0.5, e + 0.5, f + 0.5), EntityPredicates.VALID_INVENTORIES)).isEmpty()) {
            inventory = (Inventory)((Object)list.get(world.random.nextInt(list.size())));
        }
        return inventory;
    }

    private static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack.getItem() != itemStack2.getItem()) {
            return false;
        }
        if (itemStack.getDamage() != itemStack2.getDamage()) {
            return false;
        }
        if (itemStack.getCount() > itemStack.getMaxCount()) {
            return false;
        }
        return ItemStack.areTagsEqual(itemStack, itemStack2);
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

    private void setCooldown(int i) {
        this.transferCooldown = i;
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
    protected void setInvStackList(DefaultedList<ItemStack> defaultedList) {
        this.inventory = defaultedList;
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

