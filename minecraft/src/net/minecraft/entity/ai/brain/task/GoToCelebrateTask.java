package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Random;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class GoToCelebrateTask<E extends MobEntity> extends Task<E> {
	private final int completionRange;
	private final float speed;

	public GoToCelebrateTask(int completionRange, float speed) {
		super(
			ImmutableMap.of(
				MemoryModuleType.CELEBRATE_LOCATION,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED
			)
		);
		this.completionRange = completionRange;
		this.speed = speed;
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		BlockPos blockPos = getCelebrateLocation(mobEntity);
		boolean bl = blockPos.isWithinDistance(mobEntity.getBlockPos(), (double)this.completionRange);
		if (!bl) {
			LookTargetUtil.walkTowards(mobEntity, fuzz(mobEntity, blockPos), this.speed, this.completionRange);
		}
	}

	private static BlockPos fuzz(MobEntity mob, BlockPos pos) {
		Random random = mob.world.random;
		return pos.add(fuzz(random), 0, fuzz(random));
	}

	private static int fuzz(Random random) {
		return random.nextInt(3) - 1;
	}

	private static BlockPos getCelebrateLocation(MobEntity entity) {
		return (BlockPos)entity.getBrain().getOptionalMemory(MemoryModuleType.CELEBRATE_LOCATION).get();
	}
}
