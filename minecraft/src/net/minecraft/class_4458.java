package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestStorage;

public class class_4458 extends Task<VillagerEntity> {
	private final float field_20288;
	private final int field_20289;

	public class_4458(float f, int i) {
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457));
		this.field_20288 = f;
		this.field_20289 = i;
	}

	protected boolean method_21636(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return !serverWorld.isNearOccupiedPointOfInterest(new BlockPos(villagerEntity));
	}

	protected void method_21637(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
		int i = pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(new BlockPos(villagerEntity)));
		Vec3d vec3d = null;

		for (int j = 0; j < 5; j++) {
			Vec3d vec3d2 = PathfindingUtil.findTargetStraight(
				villagerEntity, 15, 7, blockPos -> (double)(-serverWorld.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(blockPos)))
			);
			if (vec3d2 != null) {
				int k = pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(new BlockPos(vec3d2)));
				if (k < i) {
					vec3d = vec3d2;
					break;
				}

				if (k == i) {
					vec3d = vec3d2;
				}
			}
		}

		if (vec3d != null) {
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(vec3d, this.field_20288, this.field_20289));
		}
	}
}
