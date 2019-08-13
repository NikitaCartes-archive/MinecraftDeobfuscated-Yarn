package net.minecraft.client.network;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugRendererInfoManager {
	private static final Logger LOGGER = LogManager.getLogger();

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

	public static void sendGoalSelector(World world, MobEntity mobEntity, GoalSelector goalSelector) {
	}

	public static void sendRaids(ServerWorld serverWorld, Collection<Raid> collection) {
	}

	public static void sendVillagerAiDebugData(LivingEntity livingEntity) {
	}
}
