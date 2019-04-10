package net.minecraft.sortme;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
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

	public static void sendBlockUpdate(World world, BlockPos blockPos) {
	}

	public static void sendStructureStart(IWorld iWorld, StructureStart structureStart) {
	}

	public static void sendGoalSelector(World world, MobEntity mobEntity, GoalSelector goalSelector) {
	}

	public static void sendVillagerAiDebugData(LivingEntity livingEntity) {
	}
}
