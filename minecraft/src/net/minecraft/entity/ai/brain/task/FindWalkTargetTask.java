package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class FindWalkTargetTask extends Task<MobEntityWithAi> {
	private final float walkSpeed;
	private final int field_19352;
	private final int field_19353;

	public FindWalkTargetTask(float f) {
		this(f, 10, 7);
	}

	public FindWalkTargetTask(float f, int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457));
		this.walkSpeed = f;
		this.field_19352 = i;
		this.field_19353 = j;
	}

	protected void method_18996(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		BlockPos blockPos = new BlockPos(mobEntityWithAi);
		if (serverWorld.isNearOccupiedPointOfInterest(blockPos)) {
			this.method_20429(mobEntityWithAi);
		} else {
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
			ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld, chunkSectionPos, 2);
			if (chunkSectionPos2 != chunkSectionPos) {
				this.method_20430(mobEntityWithAi, chunkSectionPos2);
			} else {
				this.method_20429(mobEntityWithAi);
			}
		}
	}

	private void method_20430(MobEntityWithAi mobEntityWithAi, ChunkSectionPos chunkSectionPos) {
		BlockPos blockPos = chunkSectionPos.getCenterPos();
		Optional<Vec3d> optional = Optional.ofNullable(
			PathfindingUtil.method_6373(
				mobEntityWithAi, this.field_19352, this.field_19353, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ())
			)
		);
		mobEntityWithAi.getBrain().setMemory(MemoryModuleType.field_18445, optional.map(vec3d -> new WalkTarget(vec3d, this.walkSpeed, 0)));
	}

	private void method_20429(MobEntityWithAi mobEntityWithAi) {
		Optional<Vec3d> optional = Optional.ofNullable(PathfindingUtil.findTargetStraight(mobEntityWithAi, this.field_19352, this.field_19353));
		mobEntityWithAi.getBrain().setMemory(MemoryModuleType.field_18445, optional.map(vec3d -> new WalkTarget(vec3d, this.walkSpeed, 0)));
	}
}
