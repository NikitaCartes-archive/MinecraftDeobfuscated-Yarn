package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.QueryableTickScheduler;

public interface WorldAccess extends RegistryWorldView, LunarWorldView {
	@Override
	default long getLunarTime() {
		return this.getLevelProperties().getTimeOfDay();
	}

	long getTickOrder();

	QueryableTickScheduler<Block> getBlockTickScheduler();

	private <T> OrderedTick<T> createOrderedTick(BlockPos pos, T type, int delay, TickPriority priority) {
		return new OrderedTick<>(type, pos, this.getLevelProperties().getTime() + (long)delay, priority, this.getTickOrder());
	}

	private <T> OrderedTick<T> createOrderedTick(BlockPos pos, T type, int delay) {
		return new OrderedTick<>(type, pos, this.getLevelProperties().getTime() + (long)delay, this.getTickOrder());
	}

	default void createAndScheduleBlockTick(BlockPos pos, Block block, int delay, TickPriority priority) {
		this.getBlockTickScheduler().scheduleTick(this.createOrderedTick(pos, block, delay, priority));
	}

	default void createAndScheduleBlockTick(BlockPos pos, Block block, int delay) {
		this.getBlockTickScheduler().scheduleTick(this.createOrderedTick(pos, block, delay));
	}

	QueryableTickScheduler<Fluid> getFluidTickScheduler();

	default void createAndScheduleFluidTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority) {
		this.getFluidTickScheduler().scheduleTick(this.createOrderedTick(pos, fluid, delay, priority));
	}

	default void createAndScheduleFluidTick(BlockPos pos, Fluid fluid, int delay) {
		this.getFluidTickScheduler().scheduleTick(this.createOrderedTick(pos, fluid, delay));
	}

	WorldProperties getLevelProperties();

	LocalDifficulty getLocalDifficulty(BlockPos pos);

	@Nullable
	MinecraftServer getServer();

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

	default void syncWorldEvent(int eventId, BlockPos pos, int data) {
		this.syncWorldEvent(null, eventId, pos, data);
	}

	void emitGameEvent(@Nullable Entity sourceEntity, GameEvent event, Vec3d pos);

	default void emitGameEvent(@Nullable Entity sourceEntity, GameEvent event, BlockPos pos) {
		this.emitGameEvent(sourceEntity, event, Vec3d.ofCenter(pos));
	}
}
