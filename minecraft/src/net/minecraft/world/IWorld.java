package net.minecraft.world;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

public interface IWorld extends ViewableWorld, ModifiableTestableWorld {
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

	default <T extends Entity> List<T> getVisibleEntities(Class<? extends T> class_, BoundingBox boundingBox) {
		return this.getEntities(class_, boundingBox, EntityPredicates.EXCEPT_SPECTATOR);
	}

	<T extends Entity> List<T> getEntities(Class<? extends T> class_, BoundingBox boundingBox, @Nullable Predicate<? super T> predicate);

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
}
