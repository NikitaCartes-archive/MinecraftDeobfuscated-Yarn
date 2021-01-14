package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkManager;

public interface WorldAccess extends RegistryWorldView, LunarWorldView {
	@Override
	default long getLunarTime() {
		return this.getLevelProperties().getTimeOfDay();
	}

	TickScheduler<Block> getBlockTickScheduler();

	TickScheduler<Fluid> getFluidTickScheduler();

	WorldProperties getLevelProperties();

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
		return this.getDimension().getLogicalHeight();
	}

	default void syncWorldEvent(int eventId, BlockPos pos, int data) {
		this.syncWorldEvent(null, eventId, pos, data);
	}
}
