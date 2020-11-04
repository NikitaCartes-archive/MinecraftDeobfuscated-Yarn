package net.minecraft.world;

import net.minecraft.util.math.Vec3d;

public class TeleportTarget {
	public final Vec3d position;
	public final Vec3d velocity;
	public final float yaw;
	public final float pitch;

	public TeleportTarget(Vec3d position, Vec3d velocity, float yaw, float pitch) {
		this.position = position;
		this.velocity = velocity;
		this.yaw = yaw;
		this.pitch = pitch;
	}
}
