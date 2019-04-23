/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlastFurnaceBlock
extends AbstractFurnaceBlock {
    protected BlastFurnaceBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new BlastFurnaceBlockEntity();
    }

    @Override
    protected void openContainer(World world, BlockPos blockPos, PlayerEntity playerEntity) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof BlastFurnaceBlockEntity) {
            playerEntity.openContainer((NameableContainerProvider)((Object)blockEntity));
            playerEntity.incrementStat(Stats.INTERACT_WITH_BLAST_FURNACE);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (!blockState.get(LIT).booleanValue()) {
            return;
        }
        double d = (double)blockPos.getX() + 0.5;
        double e = blockPos.getY();
        double f = (double)blockPos.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            world.playSound(d, e, f, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
        Direction direction = blockState.get(FACING);
        Direction.Axis axis = direction.getAxis();
        double g = 0.52;
        double h = random.nextDouble() * 0.6 - 0.3;
        double i = axis == Direction.Axis.X ? (double)direction.getOffsetX() * 0.52 : h;
        double j = random.nextDouble() * 9.0 / 16.0;
        double k = axis == Direction.Axis.Z ? (double)direction.getOffsetZ() * 0.52 : h;
        world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0);
    }
}

