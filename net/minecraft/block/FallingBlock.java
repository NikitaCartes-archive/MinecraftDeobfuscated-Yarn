/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.class_4538;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FallingBlock
extends Block {
    public FallingBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        iWorld.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(iWorld));
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (!FallingBlock.canFallThrough(serverWorld.getBlockState(blockPos.down())) || blockPos.getY() < 0) {
            return;
        }
        FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(serverWorld, (double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, serverWorld.getBlockState(blockPos));
        this.configureFallingBlockEntity(fallingBlockEntity);
        serverWorld.spawnEntity(fallingBlockEntity);
    }

    protected void configureFallingBlockEntity(FallingBlockEntity fallingBlockEntity) {
    }

    @Override
    public int getTickRate(class_4538 arg) {
        return 2;
    }

    public static boolean canFallThrough(BlockState blockState) {
        Block block = blockState.getBlock();
        Material material = blockState.getMaterial();
        return blockState.isAir() || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
    }

    public void onLanding(World world, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
    }

    public void onDestroyedOnLanding(World world, BlockPos blockPos) {
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        BlockPos blockPos2;
        if (random.nextInt(16) == 0 && FallingBlock.canFallThrough(world.getBlockState(blockPos2 = blockPos.down()))) {
            double d = (float)blockPos.getX() + random.nextFloat();
            double e = (double)blockPos.getY() - 0.05;
            double f = (float)blockPos.getZ() + random.nextFloat();
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, blockState), d, e, f, 0.0, 0.0, 0.0);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public int getColor(BlockState blockState) {
        return -16777216;
    }
}

