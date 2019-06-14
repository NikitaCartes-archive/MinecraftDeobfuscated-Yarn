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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

public interface IWorld extends EntityView, ViewableWorld, ModifiableTestableWorld {
	long getSeed();

	default float getMoonSize() {
		return Dimension.MOON_PHASE_TO_SIZE[this.method_8597().getMoonPhase(this.method_8401().getTimeOfDay())];
	}

	default float getSkyAngle(float f) {
		return this.method_8597().getSkyAngle(this.method_8401().getTimeOfDay(), f);
	}

	@Environment(EnvType.CLIENT)
	default int getMoonPhase() {
		return this.method_8597().getMoonPhase(this.method_8401().getTimeOfDay());
	}

	TickScheduler<Block> method_8397();

	TickScheduler<Fluid> method_8405();

	World getWorld();

	LevelProperties method_8401();

	LocalDifficulty getLocalDifficulty(BlockPos blockPos);

	default Difficulty getDifficulty() {
		return this.method_8401().getDifficulty();
	}

	ChunkManager method_8398();

	@Override
	default boolean isChunkLoaded(int i, int j) {
		return this.method_8398().isChunkLoaded(i, j);
	}

	Random getRandom();

	void method_8408(BlockPos blockPos, Block block);

	@Environment(EnvType.CLIENT)
	BlockPos getSpawnPos();

	void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g);

	void addParticle(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i);

	void playLevelEvent(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j);

	default void playLevelEvent(int i, BlockPos blockPos, int j) {
		this.playLevelEvent(null, i, blockPos, j);
	}

	@Override
	default Stream<VoxelShape> method_20743(@Nullable Entity entity, Box box, Set<Entity> set) {
		return EntityView.super.method_20743(entity, box, set);
	}

	@Override
	default boolean method_8611(@Nullable Entity entity, VoxelShape voxelShape) {
		return EntityView.super.method_8611(entity, voxelShape);
	}
}
