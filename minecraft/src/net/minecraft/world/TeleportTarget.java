package net.minecraft.world;

import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Represents the position that an entity takes after being
 * {@linkplain net.minecraft.entity.Entity#teleportTo teleported}.
 */
public record TeleportTarget(
	ServerWorld world,
	Vec3d position,
	Vec3d velocity,
	float yaw,
	float pitch,
	boolean missingRespawnBlock,
	Set<PositionFlag> relatives,
	TeleportTarget.PostDimensionTransition postDimensionTransition
) {
	public static final TeleportTarget.PostDimensionTransition NO_OP = entity -> {
	};
	public static final TeleportTarget.PostDimensionTransition SEND_TRAVEL_THROUGH_PORTAL_PACKET = TeleportTarget::sendTravelThroughPortalPacket;
	public static final TeleportTarget.PostDimensionTransition ADD_PORTAL_CHUNK_TICKET = TeleportTarget::addPortalChunkTicket;

	public TeleportTarget(ServerWorld world, Vec3d pos, Vec3d velocity, float yaw, float pitch, TeleportTarget.PostDimensionTransition postDimensionTransition) {
		this(world, pos, velocity, yaw, pitch, Set.of(), postDimensionTransition);
	}

	public TeleportTarget(
		ServerWorld world, Vec3d pos, Vec3d velocity, float yaw, float pitch, Set<PositionFlag> flags, TeleportTarget.PostDimensionTransition postDimensionTransition
	) {
		this(world, pos, velocity, yaw, pitch, false, flags, postDimensionTransition);
	}

	public TeleportTarget(ServerWorld world, Entity entity, TeleportTarget.PostDimensionTransition postDimensionTransition) {
		this(world, getWorldSpawnPos(world, entity), Vec3d.ZERO, 0.0F, 0.0F, false, Set.of(), postDimensionTransition);
	}

	private static void sendTravelThroughPortalPacket(Entity entity) {
		if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
			serverPlayerEntity.networkHandler.sendPacket(new WorldEventS2CPacket(WorldEvents.TRAVEL_THROUGH_PORTAL, BlockPos.ORIGIN, 0, false));
		}
	}

	private static void addPortalChunkTicket(Entity entity) {
		entity.addPortalChunkTicketAt(BlockPos.ofFloored(entity.getPos()));
	}

	public static TeleportTarget missingSpawnBlock(ServerWorld world, Entity entity, TeleportTarget.PostDimensionTransition postDimensionTransition) {
		return new TeleportTarget(world, getWorldSpawnPos(world, entity), Vec3d.ZERO, 0.0F, 0.0F, true, Set.of(), postDimensionTransition);
	}

	private static Vec3d getWorldSpawnPos(ServerWorld world, Entity entity) {
		return entity.getWorldSpawnPos(world, world.getSpawnPos()).toBottomCenterPos();
	}

	public TeleportTarget withRotation(float yaw, float pitch) {
		return new TeleportTarget(
			this.world(), this.position(), this.velocity(), yaw, pitch, this.missingRespawnBlock(), this.relatives(), this.postDimensionTransition()
		);
	}

	@FunctionalInterface
	public interface PostDimensionTransition {
		void onTransition(Entity entity);

		default TeleportTarget.PostDimensionTransition then(TeleportTarget.PostDimensionTransition next) {
			return entity -> {
				this.onTransition(entity);
				next.onTransition(entity);
			};
		}
	}
}
