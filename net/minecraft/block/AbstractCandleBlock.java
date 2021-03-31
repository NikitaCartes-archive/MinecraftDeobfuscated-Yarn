/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCandleBlock
extends Block {
    public static final int field_30987 = 3;
    public static final BooleanProperty LIT = Properties.LIT;

    protected AbstractCandleBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected abstract Iterable<Vec3d> getParticleOffsets(BlockState var1);

    public static boolean isLitCandle(BlockState state) {
        return state.contains(LIT) && state.isIn(BlockTags.CANDLES) && state.get(LIT) != false;
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient && projectile.isOnFire() && this.isNotLit(state)) {
            AbstractCandleBlock.setLit(world, state, hit.getBlockPos(), true);
        }
    }

    protected boolean isNotLit(BlockState state) {
        return state.get(LIT) == false;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT).booleanValue()) {
            return;
        }
        this.getParticleOffsets(state).forEach(offset -> AbstractCandleBlock.spawnCandleParticles(world, offset.add(pos.getX(), pos.getY(), pos.getZ()), random));
    }

    private static void spawnCandleParticles(World world, Vec3d vec3d, Random random) {
        float f = random.nextFloat();
        if (f < 0.3f) {
            world.addParticle(ParticleTypes.SMOKE, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
            if (f < 0.17f) {
                world.playSound(vec3d.x + 0.5, vec3d.y + 0.5, vec3d.z + 0.5, SoundEvents.BLOCK_CANDLE_AMBIENT, SoundCategory.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
            }
        }
        world.addParticle(ParticleTypes.SMALL_FLAME, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
    }

    public static void extinguish(@Nullable PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos) {
        AbstractCandleBlock.setLit(world, state, pos, false);
        if (state.getBlock() instanceof AbstractCandleBlock) {
            ((AbstractCandleBlock)state.getBlock()).getParticleOffsets(state).forEach(vec3d -> world.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + vec3d.getX(), (double)pos.getY() + vec3d.getY(), (double)pos.getZ() + vec3d.getZ(), 0.0, 0.1f, 0.0));
        }
        world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.emitGameEvent((Entity)player, GameEvent.BLOCK_CHANGE, pos);
    }

    private static void setLit(WorldAccess world, BlockState state, BlockPos pos, boolean lit) {
        world.setBlockState(pos, (BlockState)state.with(LIT, lit), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
    }
}

