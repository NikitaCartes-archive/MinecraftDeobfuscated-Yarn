package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class WanderIndoorsTask extends Task<PathAwareEntity> {
	private final float speed;

	public WanderIndoorsTask(float speed) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.speed = speed;
	}

	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		return !serverWorld.isSkyVisible(pathAwareEntity.getBlockPos());
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		BlockPos blockPos = pathAwareEntity.getBlockPos();
		List<BlockPos> list = (List<BlockPos>)BlockPos.stream(blockPos.add(-1, -1, -1), blockPos.add(1, 1, 1))
			.map(BlockPos::toImmutable)
			.collect(Collectors.toList());
		Collections.shuffle(list);
		Optional<BlockPos> optional = list.stream()
			.filter(blockPosx -> !serverWorld.isSkyVisible(blockPosx))
			.filter(blockPosx -> serverWorld.isTopSolid(blockPosx, pathAwareEntity))
			.filter(blockPosx -> serverWorld.isSpaceEmpty(pathAwareEntity))
			.findFirst();
		optional.ifPresent(blockPosx -> pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPosx, this.speed, 0)));
	}
}
