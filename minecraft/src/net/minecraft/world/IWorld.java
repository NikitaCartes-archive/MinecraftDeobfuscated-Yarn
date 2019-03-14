package net.minecraft.world;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

public interface IWorld extends EntityView, ViewableWorld, ModifiableTestableWorld {
	long getSeed();

	default float method_8391() {
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

	BlockPos getSpawnPos();

	void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g);

	void addParticle(ParticleParameters particleParameters, double d, double e, double f, double g, double h, double i);

	@Override
	default Stream<VoxelShape> getCollidingEntityBoundingBoxesForEntity(@Nullable Entity entity, VoxelShape voxelShape, Set<Entity> set) {
		return EntityView.super.getCollidingEntityBoundingBoxesForEntity(entity, voxelShape, set);
	}

	@Override
	default boolean method_8611(@Nullable Entity entity, VoxelShape voxelShape) {
		return EntityView.super.method_8611(entity, voxelShape);
	}
}
