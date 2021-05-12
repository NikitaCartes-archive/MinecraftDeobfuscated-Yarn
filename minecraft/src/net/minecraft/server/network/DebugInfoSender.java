package net.minecraft.server.network;

import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.render.debug.NameGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillageGossipType;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
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
		sendPoi(world, pos);
	}

	public static void sendPoiRemoval(ServerWorld world, BlockPos pos) {
		sendPoi(world, pos);
	}

	public static void sendPointOfInterest(ServerWorld world, BlockPos pos) {
		sendPoi(world, pos);
	}

	private static void sendPoi(ServerWorld world, BlockPos pos) {
	}

	public static void sendPathfindingData(World world, MobEntity mob, @Nullable Path path, float nodeReachProximity) {
	}

	public static void sendNeighborUpdate(World world, BlockPos pos) {
	}

	public static void sendStructureStart(StructureWorldAccess world, StructureStart<?> structureStart) {
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

	public static void sendGameEvent(World world, GameEvent event, BlockPos pos) {
	}

	public static void sendGameEventListener(World world, GameEventListener eventListener) {
	}

	public static void sendBeehiveDebugData(World world, BlockPos pos, BlockState state, BeehiveBlockEntity blockEntity) {
	}

	private static void method_36158(LivingEntity livingEntity, PacketByteBuf packetByteBuf) {
		Brain<?> brain = livingEntity.getBrain();
		long l = livingEntity.world.getTime();
		if (livingEntity instanceof InventoryOwner) {
			Inventory inventory = ((InventoryOwner)livingEntity).getInventory();
			packetByteBuf.writeString(inventory.isEmpty() ? "" : inventory.toString());
		} else {
			packetByteBuf.writeString("");
		}

		if (brain.hasMemoryModule(MemoryModuleType.PATH)) {
			packetByteBuf.writeBoolean(true);
			Path path = (Path)brain.getOptionalMemory(MemoryModuleType.PATH).get();
			path.toBuffer(packetByteBuf);
		} else {
			packetByteBuf.writeBoolean(false);
		}

		if (livingEntity instanceof VillagerEntity villagerEntity) {
			boolean bl = villagerEntity.canSummonGolem(l);
			packetByteBuf.writeBoolean(bl);
		} else {
			packetByteBuf.writeBoolean(false);
		}

		packetByteBuf.writeCollection(brain.getPossibleActivities(), (packetByteBufx, activity) -> packetByteBufx.writeString(activity.getId()));
		Set<String> set = (Set<String>)brain.getRunningTasks().stream().map(Task::toString).collect(Collectors.toSet());
		packetByteBuf.writeCollection(set, PacketByteBuf::writeString);
		packetByteBuf.writeCollection(method_36157(livingEntity, l), (packetByteBufx, string) -> {
			String string2 = ChatUtil.truncate(string, 255, true);
			packetByteBufx.writeString(string2);
		});
		if (livingEntity instanceof VillagerEntity) {
			Set<BlockPos> set2 = (Set<BlockPos>)Stream.of(MemoryModuleType.JOB_SITE, MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT)
				.map(brain::getOptionalMemory)
				.flatMap(Util::stream)
				.map(GlobalPos::getPos)
				.collect(Collectors.toSet());
			packetByteBuf.writeCollection(set2, PacketByteBuf::writeBlockPos);
		} else {
			packetByteBuf.writeVarInt(0);
		}

		if (livingEntity instanceof VillagerEntity) {
			Set<BlockPos> set2 = (Set<BlockPos>)Stream.of(MemoryModuleType.POTENTIAL_JOB_SITE)
				.map(brain::getOptionalMemory)
				.flatMap(Util::stream)
				.map(GlobalPos::getPos)
				.collect(Collectors.toSet());
			packetByteBuf.writeCollection(set2, PacketByteBuf::writeBlockPos);
		} else {
			packetByteBuf.writeVarInt(0);
		}

		if (livingEntity instanceof VillagerEntity) {
			Map<UUID, Object2IntMap<VillageGossipType>> map = ((VillagerEntity)livingEntity).getGossip().method_35120();
			List<String> list = Lists.<String>newArrayList();
			map.forEach((uUID, object2IntMap) -> {
				String string = NameGenerator.name(uUID);
				object2IntMap.forEach((villageGossipType, integer) -> list.add(string + ": " + villageGossipType + ": " + integer));
			});
			packetByteBuf.writeCollection(list, PacketByteBuf::writeString);
		} else {
			packetByteBuf.writeVarInt(0);
		}
	}

	private static List<String> method_36157(LivingEntity livingEntity, long l) {
		Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> map = livingEntity.getBrain().method_35058();
		List<String> list = Lists.<String>newArrayList();

		for (Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : map.entrySet()) {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)entry.getKey();
			Optional<? extends Memory<?>> optional = (Optional<? extends Memory<?>>)entry.getValue();
			String string;
			if (optional.isPresent()) {
				Memory<?> memory = (Memory<?>)optional.get();
				Object object = memory.getValue();
				if (memoryModuleType == MemoryModuleType.HEARD_BELL_TIME) {
					long m = l - (Long)object;
					string = m + " ticks ago";
				} else if (memory.isTimed()) {
					string = method_36156((ServerWorld)livingEntity.world, object) + " (ttl: " + memory.getExpiry() + ")";
				} else {
					string = method_36156((ServerWorld)livingEntity.world, object);
				}
			} else {
				string = "-";
			}

			list.add(Registry.MEMORY_MODULE_TYPE.getId(memoryModuleType).getPath() + ": " + string);
		}

		list.sort(String::compareTo);
		return list;
	}

	private static String method_36156(ServerWorld serverWorld, @Nullable Object object) {
		if (object == null) {
			return "-";
		} else if (object instanceof UUID) {
			return method_36156(serverWorld, serverWorld.getEntity((UUID)object));
		} else if (object instanceof LivingEntity) {
			Entity entity = (Entity)object;
			return NameGenerator.name(entity);
		} else if (object instanceof Nameable) {
			return ((Nameable)object).getName().getString();
		} else if (object instanceof WalkTarget) {
			return method_36156(serverWorld, ((WalkTarget)object).getLookTarget());
		} else if (object instanceof EntityLookTarget) {
			return method_36156(serverWorld, ((EntityLookTarget)object).getEntity());
		} else if (object instanceof GlobalPos) {
			return method_36156(serverWorld, ((GlobalPos)object).getPos());
		} else if (object instanceof BlockPosLookTarget) {
			return method_36156(serverWorld, ((BlockPosLookTarget)object).getBlockPos());
		} else if (object instanceof EntityDamageSource) {
			Entity entity = ((EntityDamageSource)object).getAttacker();
			return entity == null ? object.toString() : method_36156(serverWorld, entity);
		} else if (!(object instanceof Collection)) {
			return object.toString();
		} else {
			List<String> list = Lists.<String>newArrayList();

			for (Object object2 : (Iterable)object) {
				list.add(method_36156(serverWorld, object2));
			}

			return list.toString();
		}
	}

	private static void sendToAll(ServerWorld world, PacketByteBuf buf, Identifier channel) {
		Packet<?> packet = new CustomPayloadS2CPacket(channel, buf);

		for (PlayerEntity playerEntity : world.toServerWorld().getPlayers()) {
			((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(packet);
		}
	}
}
