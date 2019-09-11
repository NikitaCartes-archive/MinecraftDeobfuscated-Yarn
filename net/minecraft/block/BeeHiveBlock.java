/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BeeHiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeeHiveBlock
extends BlockWithEntity {
    public static final Direction[] field_20418 = new Direction[]{Direction.WEST, Direction.EAST, Direction.SOUTH};
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final IntProperty HONEY_LEVEL = Properties.HONEY_LEVEL;

    public BeeHiveBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(HONEY_LEVEL, 0)).with(FACING, Direction.NORTH));
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        return blockState.get(HONEY_LEVEL);
    }

    @Override
    public void afterBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        super.afterBreak(world, playerEntity, blockPos, blockState, blockEntity, itemStack);
        if (!world.isClient) {
            List<BeeEntity> list;
            if (blockEntity instanceof BeeHiveBlockEntity && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
                ((BeeHiveBlockEntity)blockEntity).angerBees(playerEntity, BeeHiveBlockEntity.BeeState.BEE_RELEASED);
                world.updateHorizontalAdjacent(blockPos, this);
            }
            if (!(list = world.getNonSpectatingEntities(BeeEntity.class, new Box(blockPos).expand(8.0, 6.0, 8.0))).isEmpty()) {
                List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, new Box(blockPos).expand(8.0, 6.0, 8.0));
                int i = list2.size();
                for (BeeEntity beeEntity : list) {
                    if (beeEntity.getTarget() != null) continue;
                    beeEntity.setBeeAttacker(list2.get(world.random.nextInt(i)));
                }
            }
        }
    }

    public static void dropHoneycomb(World world, BlockPos blockPos) {
        for (int i = 0; i < 3; ++i) {
            BeeHiveBlock.dropStack(world, blockPos, new ItemStack(Items.HONEYCOMB, 1));
        }
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity2, Hand hand, BlockHitResult blockHitResult) {
        ItemStack itemStack = playerEntity2.getStackInHand(hand);
        int i = blockState.get(HONEY_LEVEL);
        boolean bl = false;
        if (i >= 5) {
            if (itemStack.getItem() == Items.SHEARS) {
                world.playSound(playerEntity2, playerEntity2.x, playerEntity2.y, playerEntity2.z, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                BeeHiveBlock.dropHoneycomb(world, blockPos);
                itemStack.damage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(hand));
                bl = true;
            } else if (itemStack.getItem() == Items.GLASS_BOTTLE) {
                itemStack.decrement(1);
                world.playSound(playerEntity2, playerEntity2.x, playerEntity2.y, playerEntity2.z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                if (itemStack.isEmpty()) {
                    playerEntity2.setStackInHand(hand, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!playerEntity2.inventory.insertStack(new ItemStack(Items.HONEY_BOTTLE))) {
                    playerEntity2.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
                }
                bl = true;
            }
        }
        if (bl) {
            this.emptyHoney(world, blockState, blockPos, playerEntity2);
            return true;
        }
        return super.activate(blockState, world, blockPos, playerEntity2, hand, blockHitResult);
    }

    public void emptyHoney(World world, BlockState blockState, BlockPos blockPos, @Nullable PlayerEntity playerEntity) {
        world.setBlockState(blockPos, (BlockState)blockState.with(HONEY_LEVEL, 0), 3);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof BeeHiveBlockEntity) {
            BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
            beeHiveBlockEntity.angerBees(playerEntity, BeeHiveBlockEntity.BeeState.BEE_RELEASED);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (blockState.get(HONEY_LEVEL) >= 5) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.method_21843(world, blockPos, blockState);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    private void method_21843(World world, BlockPos blockPos, BlockState blockState) {
        if (!blockState.getFluidState().isEmpty()) {
            return;
        }
        VoxelShape voxelShape = blockState.getCollisionShape(world, blockPos);
        double d = voxelShape.getMaximum(Direction.Axis.Y);
        if (d >= 1.0 && !blockState.matches(BlockTags.IMPERMEABLE)) {
            double e = voxelShape.getMinimum(Direction.Axis.Y);
            if (e > 0.0) {
                this.addHoneyParticle(world, blockPos, voxelShape, (double)blockPos.getY() + e - 0.05);
            } else {
                BlockPos blockPos2 = blockPos.down();
                BlockState blockState2 = world.getBlockState(blockPos2);
                VoxelShape voxelShape2 = blockState2.getCollisionShape(world, blockPos2);
                double f = voxelShape2.getMaximum(Direction.Axis.Y);
                if ((f < 1.0 || !blockState2.method_21743(world, blockPos2)) && blockState2.getFluidState().isEmpty()) {
                    this.addHoneyParticle(world, blockPos, voxelShape, (double)blockPos.getY() - 0.05);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    private void addHoneyParticle(World world, BlockPos blockPos, VoxelShape voxelShape, double d) {
        this.addHoneyParticle(world, (double)blockPos.getX() + voxelShape.getMinimum(Direction.Axis.X), (double)blockPos.getX() + voxelShape.getMaximum(Direction.Axis.X), (double)blockPos.getZ() + voxelShape.getMinimum(Direction.Axis.Z), (double)blockPos.getZ() + voxelShape.getMaximum(Direction.Axis.Z), d);
    }

    @Environment(value=EnvType.CLIENT)
    private void addHoneyParticle(World world, double d, double e, double f, double g, double h) {
        world.addParticle(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(world.random.nextDouble(), d, e), h, MathHelper.lerp(world.random.nextDouble(), f, g), 0.0, 0.0, 0.0);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return (BlockState)this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(HONEY_LEVEL, FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new BeeHiveBlockEntity();
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        BlockEntity blockEntity;
        if (!world.isClient && playerEntity.isCreative() && (blockEntity = world.getBlockEntity(blockPos)) instanceof BeeHiveBlockEntity) {
            CompoundTag compoundTag;
            ItemStack itemStack = new ItemStack(this);
            BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
            if (!beeHiveBlockEntity.hasNoBees()) {
                compoundTag = new CompoundTag();
                compoundTag.put("Bees", beeHiveBlockEntity.getBees());
                itemStack.putSubTag("BlockEntityTag", compoundTag);
            }
            compoundTag = new CompoundTag();
            compoundTag.putInt("honey_level", blockState.get(HONEY_LEVEL));
            itemStack.putSubTag("BlockStateTag", compoundTag);
            ItemEntity itemEntity = new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
        super.onBreak(world, blockPos, blockState, playerEntity);
    }
}

