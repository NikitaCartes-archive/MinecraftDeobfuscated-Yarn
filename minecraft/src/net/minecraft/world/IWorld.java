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

public interface IWorld extends EntityView, WorldView, ModifiableTestableWorld {
	long getSeed();

	default float getMoonSize() {
		return Dimension.MOON_PHASE_TO_SIZE[this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay())];
	}

	default float getSkyAngle(float delta) {
		return this.getDimension().getSkyAngle(this.getLevelProperties().getTimeOfDay(), delta);
	}

	@Environment(EnvType.CLIENT)
	default int getMoonPhase() {
		return this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay());
	}

	TickScheduler<Block> getBlockTickScheduler();

	TickScheduler<Fluid> getFluidTickScheduler();

	World getWorld();

	LevelProperties getLevelProperties();

	LocalDifficulty getLocalDifficulty(BlockPos pos);

	default Difficulty getDifficulty() {
		return this.getLevelProperties().getDifficulty();
	}

	ChunkManager getChunkManager();

	@Override
	default boolean isChunkLoaded(int chunkX, int chunkZ) {
		return this.getChunkManager().isChunkLoaded(chunkX, chunkZ);
	}

	Random getRandom();

	void updateNeighbors(BlockPos pos, Block block);

	@Environment(EnvType.CLIENT)
	BlockPos getSpawnPos();

	void playSound(@Nullable PlayerEntity player, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch);

	void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

	void playLevelEvent(@Nullable PlayerEntity player, int eventId, BlockPos blockPos, int data);

	default void playLevelEvent(int eventId, BlockPos blockPos, int data) {
		this.playLevelEvent(null, eventId, blockPos, data);
	}

	@Override
	default Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Set<Entity> excluded) {
		return EntityView.super.getEntityCollisions(entity, box, excluded);
	}

	@Override
	default boolean intersectsEntities(@Nullable Entity except, VoxelShape shape) {
		return EntityView.super.intersectsEntities(except, shape);
	}

	@Override
	default BlockPos getTopPosition(Heightmap.Type heightmap, BlockPos pos) {
		return WorldView.super.getTopPosition(heightmap, pos);
	}
}
