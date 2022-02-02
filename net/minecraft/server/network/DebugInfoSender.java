/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.StringHelper;
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
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DebugInfoSender {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void addGameTestMarker(ServerWorld world, BlockPos pos, String message, int color, int duration) {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeInt(color);
        packetByteBuf.writeString(message);
        packetByteBuf.writeInt(duration);
        DebugInfoSender.sendToAll(world, packetByteBuf, CustomPayloadS2CPacket.DEBUG_GAME_TEST_ADD_MARKER);
    }

    public static void clearGameTestMarkers(ServerWorld world) {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        DebugInfoSender.sendToAll(world, packetByteBuf, CustomPayloadS2CPacket.DEBUG_GAME_TEST_CLEAR);
    }

    public static void sendChunkWatchingChange(ServerWorld world, ChunkPos pos) {
    }

    public static void sendPoiAddition(ServerWorld world, BlockPos pos) {
        DebugInfoSender.sendPoi(world, pos);
    }

    public static void sendPoiRemoval(ServerWorld world, BlockPos pos) {
        DebugInfoSender.sendPoi(world, pos);
    }

    public static void sendPointOfInterest(ServerWorld world, BlockPos pos) {
        DebugInfoSender.sendPoi(world, pos);
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
        if (!(world instanceof ServerWorld)) {
            return;
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

    private static void writeBrain(LivingEntity entity, PacketByteBuf buf2) {
        Brain<Path> brain = entity.getBrain();
        long l = entity.world.getTime();
        if (entity instanceof InventoryOwner) {
            Inventory inventory = ((InventoryOwner)((Object)entity)).getInventory();
            buf2.writeString(inventory.isEmpty() ? "" : inventory.toString());
        } else {
            buf2.writeString("");
        }
        if (brain.hasMemoryModule(MemoryModuleType.PATH)) {
            buf2.writeBoolean(true);
            Path path = brain.getOptionalMemory(MemoryModuleType.PATH).get();
            path.toBuffer(buf2);
        } else {
            buf2.writeBoolean(false);
        }
        if (entity instanceof VillagerEntity) {
            VillagerEntity villagerEntity = (VillagerEntity)entity;
            boolean bl = villagerEntity.canSummonGolem(l);
            buf2.writeBoolean(bl);
        } else {
            buf2.writeBoolean(false);
        }
        buf2.writeCollection(brain.getPossibleActivities(), (buf, activity) -> buf.writeString(activity.getId()));
        Set set = brain.getRunningTasks().stream().map(Task::toString).collect(Collectors.toSet());
        buf2.writeCollection(set, PacketByteBuf::writeString);
        buf2.writeCollection(DebugInfoSender.listMemories(entity, l), (buf, memory) -> {
            String string = StringHelper.truncate(memory, 255, true);
            buf.writeString(string);
        });
        if (entity instanceof VillagerEntity) {
            Set set2 = Stream.of(MemoryModuleType.JOB_SITE, MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT).map(brain::getOptionalMemory).flatMap(Optional::stream).map(GlobalPos::getPos).collect(Collectors.toSet());
            buf2.writeCollection(set2, PacketByteBuf::writeBlockPos);
        } else {
            buf2.writeVarInt(0);
        }
        if (entity instanceof VillagerEntity) {
            Set set2 = Stream.of(MemoryModuleType.POTENTIAL_JOB_SITE).map(brain::getOptionalMemory).flatMap(Optional::stream).map(GlobalPos::getPos).collect(Collectors.toSet());
            buf2.writeCollection(set2, PacketByteBuf::writeBlockPos);
        } else {
            buf2.writeVarInt(0);
        }
        if (entity instanceof VillagerEntity) {
            Map<UUID, Object2IntMap<VillageGossipType>> map = ((VillagerEntity)entity).getGossip().getEntityReputationAssociatedGossips();
            ArrayList list = Lists.newArrayList();
            map.forEach((uuid, gossips) -> {
                String string = NameGenerator.name(uuid);
                gossips.forEach((type, value) -> list.add(string + ": " + type + ": " + value));
            });
            buf2.writeCollection(list, PacketByteBuf::writeString);
        } else {
            buf2.writeVarInt(0);
        }
    }

    private static List<String> listMemories(LivingEntity entity, long currentTime) {
        Map<MemoryModuleType<?>, Optional<Memory<?>>> map = entity.getBrain().getMemories();
        ArrayList<String> list = Lists.newArrayList();
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : map.entrySet()) {
            Object string;
            MemoryModuleType<?> memoryModuleType = entry.getKey();
            Optional<Memory<?>> optional = entry.getValue();
            if (optional.isPresent()) {
                Memory<?> memory = optional.get();
                Object object = memory.getValue();
                if (memoryModuleType == MemoryModuleType.HEARD_BELL_TIME) {
                    long l = currentTime - (Long)object;
                    string = l + " ticks ago";
                } else {
                    string = memory.isTimed() ? DebugInfoSender.format((ServerWorld)entity.world, object) + " (ttl: " + memory.getExpiry() + ")" : DebugInfoSender.format((ServerWorld)entity.world, object);
                }
            } else {
                string = "-";
            }
            list.add(Registry.MEMORY_MODULE_TYPE.getId(memoryModuleType).getPath() + ": " + (String)string);
        }
        list.sort(String::compareTo);
        return list;
    }

    private static String format(ServerWorld world, @Nullable Object object) {
        if (object == null) {
            return "-";
        }
        if (object instanceof UUID) {
            return DebugInfoSender.format(world, world.getEntity((UUID)object));
        }
        if (object instanceof LivingEntity) {
            Entity entity = (Entity)object;
            return NameGenerator.name(entity);
        }
        if (object instanceof Nameable) {
            return ((Nameable)object).getName().getString();
        }
        if (object instanceof WalkTarget) {
            return DebugInfoSender.format(world, ((WalkTarget)object).getLookTarget());
        }
        if (object instanceof EntityLookTarget) {
            return DebugInfoSender.format(world, ((EntityLookTarget)object).getEntity());
        }
        if (object instanceof GlobalPos) {
            return DebugInfoSender.format(world, ((GlobalPos)object).getPos());
        }
        if (object instanceof BlockPosLookTarget) {
            return DebugInfoSender.format(world, ((BlockPosLookTarget)object).getBlockPos());
        }
        if (object instanceof EntityDamageSource) {
            Entity entity = ((EntityDamageSource)object).getAttacker();
            return entity == null ? object.toString() : DebugInfoSender.format(world, entity);
        }
        if (object instanceof Collection) {
            ArrayList<String> list = Lists.newArrayList();
            for (Object object2 : (Iterable)object) {
                list.add(DebugInfoSender.format(world, object2));
            }
            return ((Object)list).toString();
        }
        return object.toString();
    }

    private static void sendToAll(ServerWorld world, PacketByteBuf buf, Identifier channel) {
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(channel, buf);
        for (PlayerEntity playerEntity : world.toServerWorld().getPlayers()) {
            ((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(packet);
        }
    }

    private static /* synthetic */ void method_36163(PacketByteBuf buf, Raid raid) {
        buf.writeBlockPos(raid.getCenter());
    }

    private static /* synthetic */ void method_36162(PacketByteBuf buf, PrioritizedGoal goal) {
        buf.writeInt(goal.getPriority());
        buf.writeBoolean(goal.isRunning());
        buf.writeString(goal.getGoal().getClass().getSimpleName());
    }

    private static /* synthetic */ void method_36155(ServerWorld world, PointOfInterest poi) {
        DebugInfoSender.sendPoiAddition(world, poi.getPos());
    }

    private static /* synthetic */ boolean method_36159(PointOfInterestType poiType) {
        return true;
    }
}

