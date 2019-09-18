/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneOreBlock
extends Block {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedstoneOreBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
    }

    @Override
    public int getLuminance(BlockState blockState) {
        return blockState.get(LIT) != false ? super.getLuminance(blockState) : 0;
    }

    @Override
    public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
        RedstoneOreBlock.light(blockState, world, blockPos);
        super.onBlockBreakStart(blockState, world, blockPos, playerEntity);
    }

    @Override
    public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
        RedstoneOreBlock.light(world.getBlockState(blockPos), world, blockPos);
        super.onSteppedOn(world, blockPos, entity);
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        RedstoneOreBlock.light(blockState, world, blockPos);
        return super.activate(blockState, world, blockPos, playerEntity, hand, blockHitResult);
    }

    private static void light(BlockState blockState, World world, BlockPos blockPos) {
        RedstoneOreBlock.spawnParticles(world, blockPos);
        if (!blockState.get(LIT).booleanValue()) {
            world.setBlockState(blockPos, (BlockState)blockState.with(LIT, true), 3);
        }
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (blockState.get(LIT).booleanValue()) {
            serverWorld.setBlockState(blockPos, (BlockState)blockState.with(LIT, false), 3);
        }
    }

    @Override
    public void onStacksDropped(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
        super.onStacksDropped(blockState, world, blockPos, itemStack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            int i = 1 + world.random.nextInt(5);
            this.dropExperience(world, blockPos, i);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (blockState.get(LIT).booleanValue()) {
            RedstoneOreBlock.spawnParticles(world, blockPos);
        }
    }

    private static void spawnParticles(World world, BlockPos blockPos) {
        double d = 0.5625;
        Random random = world.random;
        for (Direction direction : Direction.values()) {
            BlockPos blockPos2 = blockPos.offset(direction);
            if (world.getBlockState(blockPos2).isFullOpaque(world, blockPos2)) continue;
            Direction.Axis axis = direction.getAxis();
            double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getOffsetX() : (double)random.nextFloat();
            double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getOffsetY() : (double)random.nextFloat();
            double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getOffsetZ() : (double)random.nextFloat();
            world.addParticle(DustParticleEffect.RED, (double)blockPos.getX() + e, (double)blockPos.getY() + f, (double)blockPos.getZ() + g, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}

