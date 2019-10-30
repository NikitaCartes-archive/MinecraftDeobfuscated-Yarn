package net.minecraft.client.network;

import io.netty.buffer.Unpooled;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugRendererInfoManager {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void addGameTestMarker(ServerWorld world, BlockPos pos, String message, int color, int duration) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeBlockPos(pos);
		packetByteBuf.writeInt(color);
		packetByteBuf.writeString(message);
		packetByteBuf.writeInt(duration);
		method_22319(world, packetByteBuf, CustomPayloadS2CPacket.DEBUG_GAME_TEST_ADD_MARKER);
	}

	public static void clearGameTestMarkers(ServerWorld world) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		method_22319(world, packetByteBuf, CustomPayloadS2CPacket.DEBUG_GAME_TEST_CLEAR);
	}

	public static void method_19775(ServerWorld serverWorld, ChunkPos chunkPos) {
	}

	public static void method_19776(ServerWorld serverWorld, BlockPos blockPos) {
	}

	public static void method_19777(ServerWorld serverWorld, BlockPos blockPos) {
	}

	public static void sendPointOfInterest(ServerWorld serverWorld, BlockPos blockPos) {
	}

	public static void sendPathfindingData(World world, MobEntity mobEntity, @Nullable Path path, float f) {
	}

	public static void sendNeighborUpdate(World world, BlockPos blockPos) {
	}

	public static void sendStructureStart(IWorld iWorld, StructureStart structureStart) {
	}

	public static void sendGoalSelector(World world, MobEntity mob, GoalSelector goalSelector) {
	}

	public static void sendRaids(ServerWorld server, Collection<Raid> raids) {
	}

	public static void sendVillagerAiDebugData(LivingEntity livingEntity) {
	}

	private static void method_22319(ServerWorld serverWorld, PacketByteBuf packetByteBuf, Identifier identifier) {
		Packet<?> packet = new CustomPayloadS2CPacket(identifier, packetByteBuf);

		for (PlayerEntity playerEntity : serverWorld.getWorld().getPlayers()) {
			((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(packet);
		}
	}
}
