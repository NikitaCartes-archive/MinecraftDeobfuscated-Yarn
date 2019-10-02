/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.EntityView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;

public interface IWorld
extends EntityView,
WorldView,
ModifiableTestableWorld {
    public long getSeed();

    default public float getMoonSize() {
        return Dimension.MOON_PHASE_TO_SIZE[this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay())];
    }

    default public float getSkyAngle(float f) {
        return this.getDimension().getSkyAngle(this.getLevelProperties().getTimeOfDay(), f);
    }

    @Environment(value=EnvType.CLIENT)
    default public int getMoonPhase() {
        return this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay());
    }

    public TickScheduler<Block> getBlockTickScheduler();

    public TickScheduler<Fluid> getFluidTickScheduler();

    public World getWorld();

    public LevelProperties getLevelProperties();

    public LocalDifficulty getLocalDifficulty(BlockPos var1);

    default public Difficulty getDifficulty() {
        return this.getLevelProperties().getDifficulty();
    }

    public ChunkManager getChunkManager();

    @Override
    default public boolean isChunkLoaded(int i, int j) {
        return this.getChunkManager().isChunkLoaded(i, j);
    }

    public Random getRandom();

    public void updateNeighbors(BlockPos var1, Block var2);

    @Environment(value=EnvType.CLIENT)
    public BlockPos getSpawnPos();

    public void playSound(@Nullable PlayerEntity var1, BlockPos var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

    public void addParticle(ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12);

    public void playLevelEvent(@Nullable PlayerEntity var1, int var2, BlockPos var3, int var4);

    default public void playLevelEvent(int i, BlockPos blockPos, int j) {
        this.playLevelEvent(null, i, blockPos, j);
    }

    @Override
    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Set<Entity> set) {
        return EntityView.super.getEntityCollisions(entity, box, set);
    }

    @Override
    default public boolean intersectsEntities(@Nullable Entity entity, VoxelShape voxelShape) {
        return EntityView.super.intersectsEntities(entity, voxelShape);
    }

    @Override
    default public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
        return WorldView.super.getTopPosition(type, blockPos);
    }
}

