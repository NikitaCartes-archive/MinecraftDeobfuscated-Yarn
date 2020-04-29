package net.minecraft.world;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5217;
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

public interface IWorld extends EntityView, WorldView, ModifiableTestableWorld {
	long getSeed();

	default float getMoonSize() {
		return Dimension.MOON_PHASE_TO_SIZE[this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay())];
	}

	default float getSkyAngle(float tickDelta) {
		return this.getDimension().getSkyAngle(this.getLevelProperties().getTimeOfDay(), tickDelta);
	}

	@Environment(EnvType.CLIENT)
	default int getMoonPhase() {
		return this.getDimension().getMoonPhase(this.getLevelProperties().getTimeOfDay());
	}

	TickScheduler<Block> getBlockTickScheduler();

	TickScheduler<Fluid> getFluidTickScheduler();

	World getWorld();

	class_5217 getLevelProperties();

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

	default void updateNeighbors(BlockPos pos, Block block) {
	}

	void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch);

	void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

	void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data);

	default int getDimensionHeight() {
		return this.getDimension().isNether() ? 128 : 256;
	}

	default void syncWorldEvent(int eventId, BlockPos pos, int data) {
		this.syncWorldEvent(null, eventId, pos, data);
	}

	@Override
	default Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return EntityView.super.getEntityCollisions(entity, box, predicate);
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
