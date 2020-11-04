/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShulkerBoxBlockEntity
extends LootableContainerBlockEntity
implements SidedInventory {
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private int viewerCount;
    private AnimationStage animationStage = AnimationStage.CLOSED;
    private float animationProgress;
    private float prevAnimationProgress;
    @Nullable
    private final DyeColor cachedColor;

    public ShulkerBoxBlockEntity(@Nullable DyeColor color, BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.SHULKER_BOX, blockPos, blockState);
        this.cachedColor = color;
    }

    public ShulkerBoxBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.SHULKER_BOX, blockPos, blockState);
        this.cachedColor = ShulkerBoxBlock.getColor(blockState.getBlock());
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
        ShulkerBoxBlockEntity.updateAnimation(world, blockPos, blockState, shulkerBoxBlockEntity);
        if (shulkerBoxBlockEntity.animationStage == AnimationStage.OPENING || shulkerBoxBlockEntity.animationStage == AnimationStage.CLOSING) {
            ShulkerBoxBlockEntity.pushEntities(world, blockPos, blockState, shulkerBoxBlockEntity.getAnimationProgress(1.0f));
        }
    }

    private static void updateAnimation(World world, BlockPos blockPos, BlockState blockState, ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
        shulkerBoxBlockEntity.prevAnimationProgress = shulkerBoxBlockEntity.animationProgress;
        switch (shulkerBoxBlockEntity.animationStage) {
            case CLOSED: {
                shulkerBoxBlockEntity.animationProgress = 0.0f;
                break;
            }
            case OPENING: {
                shulkerBoxBlockEntity.animationProgress += 0.1f;
                if (!(shulkerBoxBlockEntity.animationProgress >= 1.0f)) break;
                ShulkerBoxBlockEntity.pushEntities(world, blockPos, blockState, shulkerBoxBlockEntity.getAnimationProgress(1.0f));
                shulkerBoxBlockEntity.animationStage = AnimationStage.OPENED;
                shulkerBoxBlockEntity.animationProgress = 1.0f;
                ShulkerBoxBlockEntity.updateNeighborStates(world, blockPos, blockState);
                break;
            }
            case CLOSING: {
                shulkerBoxBlockEntity.animationProgress -= 0.1f;
                if (!(shulkerBoxBlockEntity.animationProgress <= 0.0f)) break;
                shulkerBoxBlockEntity.animationStage = AnimationStage.CLOSED;
                shulkerBoxBlockEntity.animationProgress = 0.0f;
                ShulkerBoxBlockEntity.updateNeighborStates(world, blockPos, blockState);
                break;
            }
            case OPENED: {
                shulkerBoxBlockEntity.animationProgress = 1.0f;
            }
        }
    }

    public AnimationStage getAnimationStage() {
        return this.animationStage;
    }

    public Box getBoundingBox(BlockState state) {
        return ShulkerBoxBlockEntity.getBoundingBox(state.get(ShulkerBoxBlock.FACING), this.getAnimationProgress(1.0f));
    }

    public static Box getBoundingBox(Direction direction, float f) {
        return VoxelShapes.fullCube().getBoundingBox().stretch(0.5f * f * (float)direction.getOffsetX(), 0.5f * f * (float)direction.getOffsetY(), 0.5f * f * (float)direction.getOffsetZ());
    }

    private static Box getCollisionBox(Direction direction, float f) {
        Direction direction2 = direction.getOpposite();
        return ShulkerBoxBlockEntity.getBoundingBox(direction, f).shrink(direction2.getOffsetX(), direction2.getOffsetY(), direction2.getOffsetZ());
    }

    private static void pushEntities(World world, BlockPos blockPos, BlockState blockState, float f) {
        if (!(blockState.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }
        Direction direction = blockState.get(ShulkerBoxBlock.FACING);
        Box box = ShulkerBoxBlockEntity.getCollisionBox(direction, f).offset(blockPos);
        List<Entity> list = world.getOtherEntities(null, box);
        if (list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity.getPistonBehavior() == PistonBehavior.IGNORE) continue;
            double d = 0.0;
            double e = 0.0;
            double g = 0.0;
            Box box2 = entity.getBoundingBox();
            switch (direction.getAxis()) {
                case X: {
                    d = direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.maxX - box2.minX : box2.maxX - box.minX;
                    d += 0.01;
                    break;
                }
                case Y: {
                    e = direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.maxY - box2.minY : box2.maxY - box.minY;
                    e += 0.01;
                    break;
                }
                case Z: {
                    g = direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.maxZ - box2.minZ : box2.maxZ - box.minZ;
                    g += 0.01;
                }
            }
            entity.move(MovementType.SHULKER_BOX, new Vec3d(d * (double)direction.getOffsetX(), e * (double)direction.getOffsetY(), g * (double)direction.getOffsetZ()));
        }
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.viewerCount = data;
            if (data == 0) {
                this.animationStage = AnimationStage.CLOSING;
                ShulkerBoxBlockEntity.updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
            }
            if (data == 1) {
                this.animationStage = AnimationStage.OPENING;
                ShulkerBoxBlockEntity.updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
            }
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    private static void updateNeighborStates(World world, BlockPos blockPos, BlockState blockState) {
        blockState.updateNeighbors(world, blockPos, 3);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }
            ++this.viewerCount;
            this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount == 1) {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
            this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount <= 0) {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.shulkerBox");
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.deserializeInventory(compoundTag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        return this.serializeInventory(tag);
    }

    public void deserializeInventory(CompoundTag tag) {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag) && tag.contains("Items", 9)) {
            Inventories.fromTag(tag, this.inventory);
        }
    }

    public CompoundTag serializeInventory(CompoundTag tag) {
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory, false);
        }
        return tag;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    public float getAnimationProgress(float f) {
        return MathHelper.lerp(f, this.prevAnimationProgress, this.animationProgress);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public DyeColor getColor() {
        return this.cachedColor;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ShulkerBoxScreenHandler(syncId, playerInventory, this);
    }

    public boolean suffocates() {
        return this.animationStage == AnimationStage.CLOSED;
    }

    public static enum AnimationStage {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;

    }
}

