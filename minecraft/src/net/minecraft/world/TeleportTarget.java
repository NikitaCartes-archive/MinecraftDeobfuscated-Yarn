package net.minecraft.world;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * Represents the position that an entity takes after being
 * {@linkplain net.minecraft.entity.Entity#teleportTo teleported}.
 */
public record TeleportTarget(ServerWorld world, Vec3d pos, Vec3d velocity, float yaw, float pitch, boolean missingRespawnBlock) {
	public TeleportTarget(ServerWorld world, Vec3d pos, Vec3d velocity, float yaw, float pitch) {
		this(world, pos, velocity, yaw, pitch, false);
	}

	public TeleportTarget(ServerWorld world) {
		this(world, world.getSpawnPos().toCenterPos(), Vec3d.ZERO, 0.0F, 0.0F, false);
	}

	public static TeleportTarget missingSpawnBlock(ServerWorld world) {
		return new TeleportTarget(world, world.getSpawnPos().toCenterPos(), Vec3d.ZERO, 0.0F, 0.0F, true);
	}
}
