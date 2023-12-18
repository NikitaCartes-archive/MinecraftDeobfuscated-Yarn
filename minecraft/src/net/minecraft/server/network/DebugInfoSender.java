package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.custom.DebugGameTestAddMarkerCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestClearCustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.slf4j.Logger;

public class DebugInfoSender {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void addGameTestMarker(ServerWorld world, BlockPos pos, String message, int color, int duration) {
		sendToAll(world, new DebugGameTestAddMarkerCustomPayload(pos, color, message, duration));
	}

	public static void clearGameTestMarkers(ServerWorld world) {
		sendToAll(world, new DebugGameTestClearCustomPayload());
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

	public static void sendStructureStart(StructureWorldAccess world, StructureStart structureStart) {
	}

	public static void sendGoalSelector(World world, MobEntity mob, GoalSelector goalSelector) {
	}

	public static void sendRaids(ServerWorld server, Collection<Raid> raids) {
	}

	public static void sendBrainDebugData(LivingEntity living) {
	}

	public static void sendBeeDebugData(BeeEntity bee) {
	}

	public static void sendBreezeDebugData(BreezeEntity breeze) {
	}

	public static void sendGameEvent(World world, RegistryEntry<GameEvent> event, Vec3d pos) {
	}

	public static void sendGameEventListener(World world, GameEventListener eventListener) {
	}

	public static void sendBeehiveDebugData(World world, BlockPos pos, BlockState state, BeehiveBlockEntity blockEntity) {
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
					string = format((ServerWorld)entity.getWorld(), object) + " (ttl: " + memory.getExpiry() + ")";
				} else {
					string = format((ServerWorld)entity.getWorld(), object);
				}
			} else {
				string = "-";
			}

			list.add(Registries.MEMORY_MODULE_TYPE.getId(memoryModuleType).getPath() + ": " + string);
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
		} else if (object instanceof DamageSource) {
			Entity entity = ((DamageSource)object).getAttacker();
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

	private static void sendToAll(ServerWorld world, CustomPayload payload) {
		Packet<?> packet = new CustomPayloadS2CPacket(payload);

		for (ServerPlayerEntity serverPlayerEntity : world.getPlayers()) {
			serverPlayerEntity.networkHandler.sendPacket(packet);
		}
	}
}
