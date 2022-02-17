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
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
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

	public static void sendGameEvent(World world, GameEvent event, Vec3d pos) {
	}

	public static void sendGameEventListener(World world, GameEventListener eventListener) {
	}

	public static void sendBeehiveDebugData(World world, BlockPos pos, BlockState state, BeehiveBlockEntity blockEntity) {
	}

	private static void writeBrain(LivingEntity entity, PacketByteBuf buf) {
		Brain<?> brain = entity.getBrain();
		long l = entity.world.getTime();
		if (entity instanceof InventoryOwner) {
			Inventory inventory = ((InventoryOwner)entity).getInventory();
			buf.writeString(inventory.isEmpty() ? "" : inventory.toString());
		} else {
			buf.writeString("");
		}

		if (brain.hasMemoryModule(MemoryModuleType.PATH)) {
			buf.writeBoolean(true);
			Path path = (Path)brain.getOptionalMemory(MemoryModuleType.PATH).get();
			path.toBuffer(buf);
		} else {
			buf.writeBoolean(false);
		}

		if (entity instanceof VillagerEntity villagerEntity) {
			boolean bl = villagerEntity.canSummonGolem(l);
			buf.writeBoolean(bl);
		} else {
			buf.writeBoolean(false);
		}

		buf.writeCollection(brain.getPossibleActivities(), (bufx, activity) -> bufx.writeString(activity.getId()));
		Set<String> set = (Set<String>)brain.getRunningTasks().stream().map(Task::toString).collect(Collectors.toSet());
		buf.writeCollection(set, PacketByteBuf::writeString);
		buf.writeCollection(listMemories(entity, l), (bufx, memory) -> {
			String string = StringHelper.truncate(memory, 255, true);
			bufx.writeString(string);
		});
		if (entity instanceof VillagerEntity) {
			Set<BlockPos> set2 = (Set<BlockPos>)Stream.of(MemoryModuleType.JOB_SITE, MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT)
				.map(brain::getOptionalMemory)
				.flatMap(Util::stream)
				.map(GlobalPos::getPos)
				.collect(Collectors.toSet());
			buf.writeCollection(set2, PacketByteBuf::writeBlockPos);
		} else {
			buf.writeVarInt(0);
		}

		if (entity instanceof VillagerEntity) {
			Set<BlockPos> set2 = (Set<BlockPos>)Stream.of(MemoryModuleType.POTENTIAL_JOB_SITE)
				.map(brain::getOptionalMemory)
				.flatMap(Util::stream)
				.map(GlobalPos::getPos)
				.collect(Collectors.toSet());
			buf.writeCollection(set2, PacketByteBuf::writeBlockPos);
		} else {
			buf.writeVarInt(0);
		}

		if (entity instanceof VillagerEntity) {
			Map<UUID, Object2IntMap<VillageGossipType>> map = ((VillagerEntity)entity).getGossip().getEntityReputationAssociatedGossips();
			List<String> list = Lists.<String>newArrayList();
			map.forEach((uuid, gossips) -> {
				String string = NameGenerator.name(uuid);
				gossips.forEach((type, value) -> list.add(string + ": " + type + ": " + value));
			});
			buf.writeCollection(list, PacketByteBuf::writeString);
		} else {
			buf.writeVarInt(0);
		}
	}

	private static List<String> listMemories(LivingEntity entity, long currentTime) {
		Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> map = entity.getBrain().getMemories();
		List<String> list = Lists.<String>newArrayList();

		for (Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : map.entrySet()) {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)entry.getKey();
			Optional<? extends Memory<?>> optional = (Optional<? extends Memory<?>>)entry.getValue();
			String string;
			if (optional.isPresent()) {
				Memory<?> memory = (Memory<?>)optional.get();
				Object object = memory.getValue();
				if (memoryModuleType == MemoryModuleType.HEARD_BELL_TIME) {
					long l = currentTime - (Long)object;
					string = l + " ticks ago";
				} else if (memory.isTimed()) {
					string = format((ServerWorld)entity.world, object) + " (ttl: " + memory.getExpiry() + ")";
				} else {
					string = format((ServerWorld)entity.world, object);
				}
			} else {
				string = "-";
			}

			list.add(Registry.MEMORY_MODULE_TYPE.getId(memoryModuleType).getPath() + ": " + string);
		}

		list.sort(String::compareTo);
		return list;
	}

	private static String format(ServerWorld world, @Nullable Object object) {
		if (object == null) {
			return "-";
		} else if (object instanceof UUID) {
			return format(world, world.getEntity((UUID)object));
		} else if (object instanceof LivingEntity) {
			Entity entity = (Entity)object;
			return NameGenerator.name(entity);
		} else if (object instanceof Nameable) {
			return ((Nameable)object).getName().getString();
		} else if (object instanceof WalkTarget) {
			return format(world, ((WalkTarget)object).getLookTarget());
		} else if (object instanceof EntityLookTarget) {
			return format(world, ((EntityLookTarget)object).getEntity());
		} else if (object instanceof GlobalPos) {
			return format(world, ((GlobalPos)object).getPos());
		} else if (object instanceof BlockPosLookTarget) {
			return format(world, ((BlockPosLookTarget)object).getBlockPos());
		} else if (object instanceof EntityDamageSource) {
			Entity entity = ((EntityDamageSource)object).getAttacker();
			return entity == null ? object.toString() : format(world, entity);
		} else if (!(object instanceof Collection)) {
			return object.toString();
		} else {
			List<String> list = Lists.<String>newArrayList();

			for (Object object2 : (Iterable)object) {
				list.add(format(world, object2));
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
