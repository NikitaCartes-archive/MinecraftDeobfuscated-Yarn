package net.minecraft.entity.player;

import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public record PlayerPosition(Vec3d position, Vec3d deltaMovement, float yaw, float pitch) {
	public static final PacketCodec<PacketByteBuf, PlayerPosition> PACKET_CODEC = PacketCodec.tuple(
		Vec3d.PACKET_CODEC,
		PlayerPosition::position,
		Vec3d.PACKET_CODEC,
		PlayerPosition::deltaMovement,
		PacketCodecs.FLOAT,
		PlayerPosition::yaw,
		PacketCodecs.FLOAT,
		PlayerPosition::pitch,
		PlayerPosition::new
	);

	public static PlayerPosition fromEntity(Entity entity) {
		return new PlayerPosition(entity.getPos(), entity.getMovement(), entity.getYaw(), entity.getPitch());
	}

	public static PlayerPosition fromEntityLerpTarget(Entity entity) {
		return new PlayerPosition(
			new Vec3d(entity.getLerpTargetX(), entity.getLerpTargetY(), entity.getLerpTargetZ()), entity.getMovement(), entity.getYaw(), entity.getPitch()
		);
	}

	public static PlayerPosition fromTeleportTarget(TeleportTarget teleportTarget) {
		return new PlayerPosition(teleportTarget.position(), teleportTarget.velocity(), teleportTarget.yaw(), teleportTarget.pitch());
	}

	public static PlayerPosition apply(PlayerPosition currentPos, PlayerPosition newPos, Set<PositionFlag> flags) {
		double d = flags.contains(PositionFlag.X) ? currentPos.position.x : 0.0;
		double e = flags.contains(PositionFlag.Y) ? currentPos.position.y : 0.0;
		double f = flags.contains(PositionFlag.Z) ? currentPos.position.z : 0.0;
		float g = flags.contains(PositionFlag.Y_ROT) ? currentPos.yaw : 0.0F;
		float h = flags.contains(PositionFlag.X_ROT) ? currentPos.pitch : 0.0F;
		Vec3d vec3d = new Vec3d(d + newPos.position.x, e + newPos.position.y, f + newPos.position.z);
		float i = g + newPos.yaw;
		float j = h + newPos.pitch;
		Vec3d vec3d2 = currentPos.deltaMovement;
		if (flags.contains(PositionFlag.ROTATE_DELTA)) {
			float k = currentPos.yaw - i;
			float l = currentPos.pitch - j;
			vec3d2 = vec3d2.rotateX((float)Math.toRadians((double)l));
			vec3d2 = vec3d2.rotateY((float)Math.toRadians((double)k));
		}

		Vec3d vec3d3 = new Vec3d(
			resolve(vec3d2.x, newPos.deltaMovement.x, flags, PositionFlag.DELTA_X),
			resolve(vec3d2.y, newPos.deltaMovement.y, flags, PositionFlag.DELTA_Y),
			resolve(vec3d2.z, newPos.deltaMovement.z, flags, PositionFlag.DELTA_Z)
		);
		return new PlayerPosition(vec3d, vec3d3, i, j);
	}

	private static double resolve(double delta, double value, Set<PositionFlag> flags, PositionFlag deltaFlag) {
		return flags.contains(deltaFlag) ? delta + value : value;
	}
}
