/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.jetbrains.annotations.Nullable;

public class BoneMealItem
extends Item {
    public BoneMealItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(context.getSide());
        if (BoneMealItem.useOnFertilizable(context.getStack(), world, blockPos)) {
            if (!world.isClient) {
                world.playLevelEvent(2005, blockPos, 0);
            }
            return ActionResult.SUCCESS;
        }
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isSideSolidFullSquare(world, blockPos, context.getSide());
        if (bl && BoneMealItem.useOnGround(context.getStack(), world, blockPos2, context.getSide())) {
            if (!world.isClient) {
                world.playLevelEvent(2005, blockPos2, 0);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static boolean useOnFertilizable(ItemStack stack, World world, BlockPos pos) {
        Fertilizable fertilizable;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof Fertilizable && (fertilizable = (Fertilizable)((Object)blockState.getBlock())).isFertilizable(world, pos, blockState, world.isClient)) {
            if (world instanceof ServerWorld) {
                if (fertilizable.canGrow(world, world.random, pos, blockState)) {
                    fertilizable.grow((ServerWorld)world, world.random, pos, blockState);
                }
                stack.decrement(1);
            }
            return true;
        }
        return false;
    }

    public static boolean useOnGround(ItemStack stack, World world, BlockPos blockPos, @Nullable Direction facing) {
        if (world.getBlockState(blockPos).getBlock() != Blocks.WATER || world.getFluidState(blockPos).getLevel() != 8) {
            return false;
        }
        if (!(world instanceof ServerWorld)) {
            return true;
        }
        block0: for (int i = 0; i < 128; ++i) {
            int j;
            BlockPos blockPos2 = blockPos;
            Biome biome = world.getBiome(blockPos2);
            BlockState blockState = Blocks.SEAGRASS.getDefaultState();
            for (j = 0; j < i / 16; ++j) {
                blockPos2 = blockPos2.add(RANDOM.nextInt(3) - 1, (RANDOM.nextInt(3) - 1) * RANDOM.nextInt(3) / 2, RANDOM.nextInt(3) - 1);
                biome = world.getBiome(blockPos2);
                if (world.getBlockState(blockPos2).isFullCube(world, blockPos2)) continue block0;
            }
            if (biome == Biomes.WARM_OCEAN || biome == Biomes.DEEP_WARM_OCEAN) {
                if (i == 0 && facing != null && facing.getAxis().isHorizontal()) {
                    blockState = (BlockState)BlockTags.WALL_CORALS.getRandom(world.random).getDefaultState().with(DeadCoralWallFanBlock.FACING, facing);
                } else if (RANDOM.nextInt(4) == 0) {
                    blockState = BlockTags.UNDERWATER_BONEMEALS.getRandom(RANDOM).getDefaultState();
                }
            }
            if (blockState.getBlock().matches(BlockTags.WALL_CORALS)) {
                for (j = 0; !blockState.canPlaceAt(world, blockPos2) && j < 4; ++j) {
                    blockState = (BlockState)blockState.with(DeadCoralWallFanBlock.FACING, Direction.Type.HORIZONTAL.random(RANDOM));
                }
            }
            if (!blockState.canPlaceAt(world, blockPos2)) continue;
            BlockState blockState2 = world.getBlockState(blockPos2);
            if (blockState2.getBlock() == Blocks.WATER && world.getFluidState(blockPos2).getLevel() == 8) {
                world.setBlockState(blockPos2, blockState, 3);
                continue;
            }
            if (blockState2.getBlock() != Blocks.SEAGRASS || RANDOM.nextInt(10) != 0) continue;
            ((Fertilizable)((Object)Blocks.SEAGRASS)).grow((ServerWorld)world, RANDOM, blockPos2, blockState2);
        }
        stack.decrement(1);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public static void createParticles(IWorld world, BlockPos pos, int count) {
        BlockState blockState;
        if (count == 0) {
            count = 15;
        }
        if ((blockState = world.getBlockState(pos)).isAir()) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            double d = RANDOM.nextGaussian() * 0.02;
            double e = RANDOM.nextGaussian() * 0.02;
            double f = RANDOM.nextGaussian() * 0.02;
            world.addParticle(ParticleTypes.HAPPY_VILLAGER, (float)pos.getX() + RANDOM.nextFloat(), (double)pos.getY() + (double)RANDOM.nextFloat() * blockState.getOutlineShape(world, pos).getMaximum(Direction.Axis.Y), (float)pos.getZ() + RANDOM.nextFloat(), d, e, f);
        }
    }
}

