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
	private final float field_23130;

	public GoToCelebrateTask(int completionRange, float f) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_22337,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_22355,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18446,
				MemoryModuleState.field_18458
			)
		);
		this.completionRange = completionRange;
		this.field_23130 = f;
	}

	protected void method_24579(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		BlockPos blockPos = getCelebrateLocation(mobEntity);
		boolean bl = blockPos.isWithinDistance(mobEntity.getBlockPos(), (double)this.completionRange);
		if (!bl) {
			LookTargetUtil.walkTowards(mobEntity, fuzz(mobEntity, blockPos), this.field_23130, this.completionRange);
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
		return (BlockPos)entity.getBrain().getOptionalMemory(MemoryModuleType.field_22337).get();
	}
}
