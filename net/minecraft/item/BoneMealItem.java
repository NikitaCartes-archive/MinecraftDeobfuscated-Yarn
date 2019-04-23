/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
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
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        World world = itemUsageContext.getWorld();
        BlockPos blockPos = itemUsageContext.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(itemUsageContext.getFacing());
        if (BoneMealItem.useOnFertilizable(itemUsageContext.getItemStack(), world, blockPos)) {
            if (!world.isClient) {
                world.playLevelEvent(2005, blockPos, 0);
            }
            return ActionResult.SUCCESS;
        }
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = Block.isSolidFullSquare(blockState, world, blockPos, itemUsageContext.getFacing());
        if (bl && BoneMealItem.useOnGround(itemUsageContext.getItemStack(), world, blockPos2, itemUsageContext.getFacing())) {
            if (!world.isClient) {
                world.playLevelEvent(2005, blockPos2, 0);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static boolean useOnFertilizable(ItemStack itemStack, World world, BlockPos blockPos) {
        Fertilizable fertilizable;
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() instanceof Fertilizable && (fertilizable = (Fertilizable)((Object)blockState.getBlock())).isFertilizable(world, blockPos, blockState, world.isClient)) {
            if (!world.isClient) {
                if (fertilizable.canGrow(world, world.random, blockPos, blockState)) {
                    fertilizable.grow(world, world.random, blockPos, blockState);
                }
                itemStack.subtractAmount(1);
            }
            return true;
        }
        return false;
    }

    public static boolean useOnGround(ItemStack itemStack, World world, BlockPos blockPos, @Nullable Direction direction) {
        if (world.getBlockState(blockPos).getBlock() == Blocks.WATER && world.getFluidState(blockPos).getLevel() == 8) {
            if (!world.isClient) {
                block0: for (int i = 0; i < 128; ++i) {
                    int j;
                    BlockPos blockPos2 = blockPos;
                    Biome biome = world.getBiome(blockPos2);
                    BlockState blockState = Blocks.SEAGRASS.getDefaultState();
                    for (j = 0; j < i / 16; ++j) {
                        blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                        biome = world.getBiome(blockPos2);
                        if (Block.isShapeFullCube(world.getBlockState(blockPos2).getCollisionShape(world, blockPos2))) continue block0;
                    }
                    if (biome == Biomes.WARM_OCEAN || biome == Biomes.DEEP_WARM_OCEAN) {
                        if (i == 0 && direction != null && direction.getAxis().isHorizontal()) {
                            blockState = (BlockState)BlockTags.WALL_CORALS.getRandom(world.random).getDefaultState().with(DeadCoralWallFanBlock.FACING, direction);
                        } else if (random.nextInt(4) == 0) {
                            blockState = BlockTags.UNDERWATER_BONEMEALS.getRandom(random).getDefaultState();
                        }
                    }
                    if (blockState.getBlock().matches(BlockTags.WALL_CORALS)) {
                        for (j = 0; !blockState.canPlaceAt(world, blockPos2) && j < 4; ++j) {
                            blockState = (BlockState)blockState.with(DeadCoralWallFanBlock.FACING, Direction.Type.HORIZONTAL.random(random));
                        }
                    }
                    if (!blockState.canPlaceAt(world, blockPos2)) continue;
                    BlockState blockState2 = world.getBlockState(blockPos2);
                    if (blockState2.getBlock() == Blocks.WATER && world.getFluidState(blockPos2).getLevel() == 8) {
                        world.setBlockState(blockPos2, blockState, 3);
                        continue;
                    }
                    if (blockState2.getBlock() != Blocks.SEAGRASS || random.nextInt(10) != 0) continue;
                    ((Fertilizable)((Object)Blocks.SEAGRASS)).grow(world, random, blockPos2, blockState2);
                }
                itemStack.subtractAmount(1);
            }
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static void playEffects(IWorld iWorld, BlockPos blockPos, int i) {
        BlockState blockState;
        if (i == 0) {
            i = 15;
        }
        if ((blockState = iWorld.getBlockState(blockPos)).isAir()) {
            return;
        }
        for (int j = 0; j < i; ++j) {
            double d = random.nextGaussian() * 0.02;
            double e = random.nextGaussian() * 0.02;
            double f = random.nextGaussian() * 0.02;
            iWorld.addParticle(ParticleTypes.HAPPY_VILLAGER, (float)blockPos.getX() + random.nextFloat(), (double)blockPos.getY() + (double)random.nextFloat() * blockState.getOutlineShape(iWorld, blockPos).getMaximum(Direction.Axis.Y), (float)blockPos.getZ() + random.nextFloat(), d, e, f);
        }
    }
}

