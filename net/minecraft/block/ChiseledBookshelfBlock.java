/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ChiseledBookshelfBlock
extends BlockWithEntity {
    private static final int MAX_BOOK_COUNT = 6;
    public static final int BOOK_HEIGHT = 3;
    public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of(Properties.SLOT_0_OCCUPIED, Properties.SLOT_1_OCCUPIED, Properties.SLOT_2_OCCUPIED, Properties.SLOT_3_OCCUPIED, Properties.SLOT_4_OCCUPIED, Properties.SLOT_5_OCCUPIED);

    public ChiseledBookshelfBlock(AbstractBlock.Settings settings) {
        super(settings);
        BlockState blockState = (BlockState)((BlockState)this.stateManager.getDefaultState()).with(HorizontalFacingBlock.FACING, Direction.NORTH);
        for (BooleanProperty booleanProperty : SLOT_OCCUPIED_PROPERTIES) {
            blockState = (BlockState)blockState.with(booleanProperty, false);
        }
        this.setDefaultState(blockState);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ChiseledBookshelfBlockEntity)) {
            return ActionResult.PASS;
        }
        ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity = (ChiseledBookshelfBlockEntity)blockEntity;
        Optional<Vec2f> optional = ChiseledBookshelfBlock.getHitPos(hit, state.get(HorizontalFacingBlock.FACING));
        if (optional.isEmpty()) {
            return ActionResult.PASS;
        }
        int i = ChiseledBookshelfBlock.getSlotForHitPos(optional.get());
        if (((Boolean)state.get(SLOT_OCCUPIED_PROPERTIES.get(i))).booleanValue()) {
            ChiseledBookshelfBlock.tryRemoveBook(world, pos, player, chiseledBookshelfBlockEntity, i);
            return ActionResult.success(world.isClient);
        }
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isIn(ItemTags.BOOKSHELF_BOOKS)) {
            ChiseledBookshelfBlock.tryAddBook(world, pos, player, chiseledBookshelfBlockEntity, itemStack, i);
            return ActionResult.success(world.isClient);
        }
        return ActionResult.CONSUME;
    }

    private static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
        Direction direction = hit.getSide();
        if (facing != direction) {
            return Optional.empty();
        }
        BlockPos blockPos = hit.getBlockPos().offset(direction);
        Vec3d vec3d = hit.getPos().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        return switch (direction) {
            default -> throw new IncompatibleClassChangeError();
            case Direction.NORTH -> Optional.of(new Vec2f((float)(1.0 - d), (float)e));
            case Direction.SOUTH -> Optional.of(new Vec2f((float)d, (float)e));
            case Direction.WEST -> Optional.of(new Vec2f((float)f, (float)e));
            case Direction.EAST -> Optional.of(new Vec2f((float)(1.0 - f), (float)e));
            case Direction.DOWN, Direction.UP -> Optional.empty();
        };
    }

    private static int getSlotForHitPos(Vec2f hitPos) {
        int i = hitPos.y >= 0.5f ? 0 : 1;
        int j = ChiseledBookshelfBlock.getColumn(hitPos.x);
        return j + i * 3;
    }

    private static int getColumn(float x) {
        float f = 0.0625f;
        float g = 0.375f;
        if (x < 0.375f) {
            return 0;
        }
        float h = 0.6875f;
        if (x < 0.6875f) {
            return 1;
        }
        return 2;
    }

    private static void tryAddBook(World world, BlockPos pos, PlayerEntity player, ChiseledBookshelfBlockEntity blockEntity, ItemStack stack, int slot) {
        if (world.isClient) {
            return;
        }
        player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        SoundEvent soundEvent = stack.isOf(Items.ENCHANTED_BOOK) ? SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT;
        blockEntity.setStack(slot, stack.split(1));
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (player.isCreative()) {
            stack.increment(1);
        }
        world.emitGameEvent((Entity)player, GameEvent.BLOCK_CHANGE, pos);
    }

    private static void tryRemoveBook(World world, BlockPos pos, PlayerEntity player, ChiseledBookshelfBlockEntity blockEntity, int slot) {
        if (world.isClient) {
            return;
        }
        ItemStack itemStack = blockEntity.removeStack(slot, 1);
        SoundEvent soundEvent = itemStack.isOf(Items.ENCHANTED_BOOK) ? SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP;
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (!player.getInventory().insertStack(itemStack)) {
            player.dropItem(itemStack, false);
        }
        world.emitGameEvent((Entity)player, GameEvent.BLOCK_CHANGE, pos);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChiseledBookshelfBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HorizontalFacingBlock.FACING);
        SLOT_OCCUPIED_PROPERTIES.forEach(property -> builder.add((Property<?>)property));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity;
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChiseledBookshelfBlockEntity && !(chiseledBookshelfBlockEntity = (ChiseledBookshelfBlockEntity)blockEntity).isEmpty()) {
            for (int i = 0; i < 6; ++i) {
                ItemStack itemStack = chiseledBookshelfBlockEntity.getStack(i);
                if (itemStack.isEmpty()) continue;
                ItemScatterer.spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack);
            }
            chiseledBookshelfBlockEntity.clear();
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world.isClient()) {
            return 0;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChiseledBookshelfBlockEntity) {
            ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity = (ChiseledBookshelfBlockEntity)blockEntity;
            return chiseledBookshelfBlockEntity.getLastInteractedSlot() + 1;
        }
        return 0;
    }
}

