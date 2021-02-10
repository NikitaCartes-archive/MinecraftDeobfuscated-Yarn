package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

/**
 * Represents access to a world on a logical Minecraft server.
 */
public interface ServerWorldAccess extends WorldAccess {
	ServerWorld toServerWorld();

	/**
	 * Spawns an entity and all its passengers (recursively) into the world.
	 */
	default void spawnEntityAndPassengers(Entity entity) {
		entity.streamSelfAndPassengers().forEach(this::spawnEntity);
	}
}
