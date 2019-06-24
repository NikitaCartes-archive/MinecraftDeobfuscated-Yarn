package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class WalkHomeTask extends Task<LivingEntity> {
	private final float speed;
	private long lastRunTime;

	public WalkHomeTask(float f) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT));
		this.speed = f;
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		if (serverWorld.getTime() - this.lastRunTime < 40L) {
			return false;
		} else {
			MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)livingEntity;
			PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
			Optional<BlockPos> optional = pointOfInterestStorage.getNearestPosition(
				PointOfInterestType.HOME.getCompletionCondition(), blockPos -> true, new BlockPos(livingEntity), 48, PointOfInterestStorage.OccupationStatus.ANY
			);
			return optional.isPresent() && !(((BlockPos)optional.get()).getSquaredDistance(new Vec3i(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z)) <= 4.0);
		}
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		this.lastRunTime = serverWorld.getTime();
		MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)livingEntity;
		PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
		Predicate<BlockPos> predicate = blockPos -> {
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
			if (serverWorld.getBlockState(blockPos.down()).isAir()) {
				mutable.setOffset(Direction.DOWN);
			}

			while (serverWorld.getBlockState(mutable).isAir() && mutable.getY() >= 0) {
				mutable.setOffset(Direction.DOWN);
			}

			Path path = mobEntityWithAi.getNavigation().findPathTo(mutable);
			return path != null && path.method_19313(mutable);
		};
		pointOfInterestStorage.getNearestPosition(
				PointOfInterestType.HOME.getCompletionCondition(), predicate, new BlockPos(livingEntity), 48, PointOfInterestStorage.OccupationStatus.ANY
			)
			.ifPresent(blockPos -> {
				livingEntity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPos, this.speed, 1));
				DebugRendererInfoManager.sendPointOfInterest(serverWorld, blockPos);
			});
	}
}
