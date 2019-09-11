package net.minecraft.world;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4538;
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
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

public interface IWorld extends EntityView, class_4538, ModifiableTestableWorld {
	long getSeed();

	default float getMoonSize() {
		return Dimension.MOON_PHASE_TO_SIZE[this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay())];
	}

	default float getSkyAngle(float f) {
		return this.getDimension().getSkyAngle(this.getLevelProperties().getTimeOfDay(), f);
	}

	@Environment(EnvType.CLIENT)
	default int getMoonPhase() {
		return this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay());
	}

	TickScheduler<Block> getBlockTickScheduler();

	TickScheduler<Fluid> getFluidTickScheduler();

	World getWorld();

	LevelProperties getLevelProperties();

	LocalDifficulty getLocalDifficulty(BlockPos blockPos);

	default Difficulty getDifficulty() {
		return this.getLevelProperties().getDifficulty();
	}

	ChunkManager getChunkManager();

	@Override
	default boolean isChunkLoaded(int i, int j) {
		return this.getChunkManager().isChunkLoaded(i, j);
	}

	Random getRandom();

	void updateNeighbors(BlockPos blockPos, Block block);

	@Environment(EnvType.CLIENT)
	BlockPos getSpawnPos();

	void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g);

	void addParticle(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i);

	void playLevelEvent(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j);

	default void playLevelEvent(int i, BlockPos blockPos, int j) {
		this.playLevelEvent(null, i, blockPos, j);
	}

	@Override
	default Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Set<Entity> set) {
		return EntityView.super.getEntityCollisions(entity, box, set);
	}

	@Override
	default boolean intersectsEntities(@Nullable Entity entity, VoxelShape voxelShape) {
		return EntityView.super.intersectsEntities(entity, voxelShape);
	}

	@Override
	default BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
		return class_4538.super.getTopPosition(type, blockPos);
	}
}
