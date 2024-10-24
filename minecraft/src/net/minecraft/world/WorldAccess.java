package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.ScheduledTickView;
import net.minecraft.world.tick.TickPriority;

public interface WorldAccess extends RegistryWorldView, LunarWorldView, ScheduledTickView {
	@Override
	default long getLunarTime() {
		return this.getLevelProperties().getTimeOfDay();
	}

	long getTickOrder();

	@Override
	default <T> OrderedTick<T> createOrderedTick(BlockPos pos, T type, int delay, TickPriority priority) {
		return new OrderedTick<>(type, pos, this.getLevelProperties().getTime() + (long)delay, priority, this.getTickOrder());
	}

	@Override
	default <T> OrderedTick<T> createOrderedTick(BlockPos pos, T type, int delay) {
		return new OrderedTick<>(type, pos, this.getLevelProperties().getTime() + (long)delay, this.getTickOrder());
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

	default void replaceWithStateForNeighborUpdate(
		Direction direction, BlockPos pos, BlockPos neighborPos, BlockState neighborState, int flags, int maxUpdateDepth
	) {
		NeighborUpdater.replaceWithStateForNeighborUpdate(this, direction, pos, neighborPos, neighborState, flags, maxUpdateDepth - 1);
	}

	default void playSound(@Nullable PlayerEntity except, BlockPos pos, SoundEvent sound, SoundCategory category) {
		this.playSound(except, pos, sound, category, 1.0F, 1.0F);
	}

	void playSound(@Nullable PlayerEntity source, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch);

	void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

	void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data);

	default void syncWorldEvent(int eventId, BlockPos pos, int data) {
		this.syncWorldEvent(null, eventId, pos, data);
	}

	/**
	 * Emits a game event.
	 */
	void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, GameEvent.Emitter emitter);

	default void emitGameEvent(@Nullable Entity entity, RegistryEntry<GameEvent> event, Vec3d pos) {
		this.emitGameEvent(event, pos, new GameEvent.Emitter(entity, null));
	}

	default void emitGameEvent(@Nullable Entity entity, RegistryEntry<GameEvent> event, BlockPos pos) {
		this.emitGameEvent(event, pos, new GameEvent.Emitter(entity, null));
	}

	default void emitGameEvent(RegistryEntry<GameEvent> event, BlockPos pos, GameEvent.Emitter emitter) {
		this.emitGameEvent(event, Vec3d.ofCenter(pos), emitter);
	}

	default void emitGameEvent(RegistryKey<GameEvent> event, BlockPos pos, GameEvent.Emitter emitter) {
		this.emitGameEvent(this.getRegistryManager().getOrThrow(RegistryKeys.GAME_EVENT).getOrThrow(event), pos, emitter);
	}
}
