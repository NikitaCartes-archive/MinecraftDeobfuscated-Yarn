package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
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
		this.speed = f;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457), Pair.of(MemoryModuleType.field_18438, MemoryModuleState.field_18457)
		);
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		if (serverWorld.getTime() - this.lastRunTime < 40L) {
			return false;
		} else {
			MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)livingEntity;
			PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
			Optional<BlockPos> optional = pointOfInterestStorage.getNearestPosition(
				PointOfInterestType.field_18517.getCompletionCondition(),
				blockPos -> true,
				new BlockPos(livingEntity),
				48,
				PointOfInterestStorage.OccupationStatus.field_18489
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
				mutable.setOffset(Direction.field_11033);
			}

			while (serverWorld.getBlockState(mutable).isAir() && mutable.getY() >= 0) {
				mutable.setOffset(Direction.field_11033);
			}

			Path path = mobEntityWithAi.getNavigation().findPathTo(mutable.toImmutable());
			return path != null && path.method_19315();
		};
		pointOfInterestStorage.getNearestPosition(
				PointOfInterestType.field_18517.getCompletionCondition(), predicate, new BlockPos(livingEntity), 48, PointOfInterestStorage.OccupationStatus.field_18489
			)
			.ifPresent(blockPos -> {
				livingEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(blockPos, this.speed, 1));
				DebugRendererInfoManager.sendPointOfInterest(serverWorld, blockPos);
			});
	}
}
