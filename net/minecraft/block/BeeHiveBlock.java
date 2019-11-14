/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BeeHiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeeHiveBlock
extends BlockWithEntity {
    public static final Direction[] field_20418 = new Direction[]{Direction.WEST, Direction.EAST, Direction.SOUTH};
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final IntProperty HONEY_LEVEL = Properties.HONEY_LEVEL;

    public BeeHiveBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HONEY_LEVEL, 0)).with(FACING, Direction.NORTH));
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
        if (!world.isClient && blockEntity instanceof BeeHiveBlockEntity) {
            BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
            if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
                beeHiveBlockEntity.angerBees(playerEntity, blockState, BeeHiveBlockEntity.BeeState.EMERGENCY);
                world.updateHorizontalAdjacent(blockPos, this);
            }
            this.method_23893(world, blockPos);
            Criterions.BEE_NEST_DESTROYED.test((ServerPlayerEntity)playerEntity, blockState.getBlock(), itemStack, beeHiveBlockEntity.method_23903());
        }
    }

    private void method_23893(World world, BlockPos blockPos) {
        List<BeeEntity> list = world.getNonSpectatingEntities(BeeEntity.class, new Box(blockPos).expand(8.0, 6.0, 8.0));
        if (!list.isEmpty()) {
            List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, new Box(blockPos).expand(8.0, 6.0, 8.0));
            int i = list2.size();
            for (BeeEntity beeEntity : list) {
                if (beeEntity.getTarget() != null) continue;
                beeEntity.setBeeAttacker(list2.get(world.random.nextInt(i)));
            }
        }
    }

    public static void dropHoneycomb(World world, BlockPos blockPos) {
        for (int i = 0; i < 3; ++i) {
            BeeHiveBlock.dropStack(world, blockPos, new ItemStack(Items.HONEYCOMB, 1));
        }
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity2, Hand hand, BlockHitResult blockHitResult) {
        ItemStack itemStack = playerEntity2.getStackInHand(hand);
        int i = blockState.get(HONEY_LEVEL);
        boolean bl = false;
        if (i >= 5) {
            if (itemStack.getItem() == Items.SHEARS) {
                world.playSound(playerEntity2, playerEntity2.getX(), playerEntity2.getY(), playerEntity2.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                BeeHiveBlock.dropHoneycomb(world, blockPos);
                itemStack.damage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(hand));
                bl = true;
            } else if (itemStack.getItem() == Items.GLASS_BOTTLE) {
                itemStack.decrement(1);
                world.playSound(playerEntity2, playerEntity2.getX(), playerEntity2.getY(), playerEntity2.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                if (itemStack.isEmpty()) {
                    playerEntity2.setStackInHand(hand, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!playerEntity2.inventory.insertStack(new ItemStack(Items.HONEY_BOTTLE))) {
                    playerEntity2.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
                }
                bl = true;
            }
        }
        if (bl) {
            if (!CampfireBlock.method_23895(world, blockPos, 5)) {
                if (this.method_23894(world, blockPos)) {
                    this.method_23893(world, blockPos);
                }
                this.emptyHoney(world, blockState, blockPos, playerEntity2, BeeHiveBlockEntity.BeeState.EMERGENCY);
            } else {
                this.method_23754(world, blockState, blockPos);
                if (playerEntity2 instanceof ServerPlayerEntity) {
                    Criterions.SAFELY_HARVEST_HONEY.test((ServerPlayerEntity)playerEntity2, blockPos, itemStack);
                }
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(blockState, world, blockPos, playerEntity2, hand, blockHitResult);
    }

    private boolean method_23894(World world, BlockPos blockPos) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof BeeHiveBlockEntity) {
            BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
            return !beeHiveBlockEntity.hasNoBees();
        }
        return false;
    }

    public void emptyHoney(World world, BlockState blockState, BlockPos blockPos, @Nullable PlayerEntity playerEntity, BeeHiveBlockEntity.BeeState beeState) {
        this.method_23754(world, blockState, blockPos);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof BeeHiveBlockEntity) {
            BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
            beeHiveBlockEntity.angerBees(playerEntity, blockState, beeState);
        }
    }

    public void method_23754(World world, BlockState blockState, BlockPos blockPos) {
        world.setBlockState(blockPos, (BlockState)blockState.with(HONEY_LEVEL, 0), 3);
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
        if (!blockState.getFluidState().isEmpty() || world.random.nextFloat() < 0.3f) {
            return;
        }
        VoxelShape voxelShape = blockState.getCollisionShape(world, blockPos);
        double d = voxelShape.getMaximum(Direction.Axis.Y);
        if (d >= 1.0 && !blockState.matches(BlockTags.IMPERMEABLE)) {
            double e = voxelShape.getMinimum(Direction.Axis.Y);
            if (e > 0.0) {
                this.addHoneyParticle(world, blockPos, voxelShape, (double)blockPos.getY() + e - 0.05);
            } else {
                BlockPos blockPos2 = blockPos.method_10074();
                BlockState blockState2 = world.getBlockState(blockPos2);
                VoxelShape voxelShape2 = blockState2.getCollisionShape(world, blockPos2);
                double f = voxelShape2.getMaximum(Direction.Axis.Y);
                if ((f < 1.0 || !blockState2.isFullCube(world, blockPos2)) && blockState2.getFluidState().isEmpty()) {
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
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
        if (!world.isClient && playerEntity.isCreative() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && (blockEntity = world.getBlockEntity(blockPos)) instanceof BeeHiveBlockEntity) {
            CompoundTag compoundTag;
            boolean bl;
            BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
            ItemStack itemStack = new ItemStack(this);
            int i = blockState.get(HONEY_LEVEL);
            boolean bl2 = bl = !beeHiveBlockEntity.hasNoBees();
            if (!bl && i == 0) {
                return;
            }
            if (bl) {
                compoundTag = new CompoundTag();
                compoundTag.put("Bees", beeHiveBlockEntity.getBees());
                itemStack.putSubTag("BlockEntityTag", compoundTag);
            }
            compoundTag = new CompoundTag();
            compoundTag.putInt("honey_level", i);
            itemStack.putSubTag("BlockStateTag", compoundTag);
            ItemEntity itemEntity = new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
        super.onBreak(world, blockPos, blockState, playerEntity);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
        BlockEntity blockEntity;
        Entity entity = builder.getNullable(LootContextParameters.THIS_ENTITY);
        if ((entity instanceof TntEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TntMinecartEntity) && (blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY)) instanceof BeeHiveBlockEntity) {
            BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
            beeHiveBlockEntity.angerBees(null, blockState, BeeHiveBlockEntity.BeeState.EMERGENCY);
        }
        return super.getDroppedStacks(blockState, builder);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        BlockEntity blockEntity;
        if (iWorld.getBlockState(blockPos2).getBlock() instanceof FireBlock && (blockEntity = iWorld.getBlockEntity(blockPos)) instanceof BeeHiveBlockEntity) {
            BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
            beeHiveBlockEntity.angerBees(null, blockState, BeeHiveBlockEntity.BeeState.EMERGENCY);
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }
}

