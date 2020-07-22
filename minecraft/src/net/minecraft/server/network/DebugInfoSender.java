package net.minecraft.server.network;

import io.netty.buffer.Unpooled;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugInfoSender {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void addGameTestMarker(ServerWorld world, BlockPos pos, String message, int color, int duration) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeBlockPos(pos);
		packetByteBuf.writeInt(color);
		packetByteBuf.writeString(message);
		packetByteBuf.writeInt(duration);
		sendToAll(world, packetByteBuf, CustomPayloadS2CPacket.DEBUG_GAME_TEST_ADD_MARKER);
	}

	public static void clearGameTestMarkers(ServerWorld world) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		sendToAll(world, packetByteBuf, CustomPayloadS2CPacket.DEBUG_GAME_TEST_CLEAR);
	}

	public static void sendChunkWatchingChange(ServerWorld world, ChunkPos pos) {
	}

	public static void sendPoiAddition(ServerWorld world, BlockPos pos) {
		method_24819(world, pos);
	}

	public static void sendPoiRemoval(ServerWorld world, BlockPos pos) {
		method_24819(world, pos);
	}

	public static void sendPointOfInterest(ServerWorld world, BlockPos pos) {
		method_24819(world, pos);
	}

	private static void method_24819(ServerWorld serverWorld, BlockPos blockPos) {
	}

	public static void sendPathfindingData(World world, MobEntity mob, @Nullable Path path, float nodeReachProximity) {
	}

	public static void sendNeighborUpdate(World world, BlockPos pos) {
	}

	public static void sendStructureStart(StructureWorldAccess structureWorldAccess, StructureStart<?> structureStart) {
	}

	public static void sendGoalSelector(World world, MobEntity mob, GoalSelector goalSelector) {
		if (world instanceof ServerWorld) {
			;
		}
	}

	public static void sendRaids(ServerWorld server, Collection<Raid> raids) {
	}

	public static void sendBrainDebugData(LivingEntity living) {
	}

	public static void sendBeeDebugData(BeeEntity bee) {
	}

	public static void sendBeehiveDebugData(BeehiveBlockEntity beehive) {
	}

	private static void sendToAll(ServerWorld world, PacketByteBuf buf, Identifier channel) {
		Packet<?> packet = new CustomPayloadS2CPacket(channel, buf);

		for (PlayerEntity playerEntity : world.toServerWorld().getPlayers()) {
			((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(packet);
		}
	}
}
