package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class WalkTowardsLookTargetTask<E extends LivingEntity> extends Task<E> {
	private final Function<LivingEntity, Optional<LookTarget>> lookTargetFunction;
	private final int range;
	private final float speed;

	public WalkTowardsLookTargetTask(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, int range, float speed) {
		super(Map.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.lookTargetFunction = lookTargetFunction;
		this.range = range;
		this.speed = speed;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		Optional<LookTarget> optional = (Optional<LookTarget>)this.lookTargetFunction.apply(entity);
		return optional.isPresent() && !entity.getPos().isInRange(((LookTarget)optional.get()).getPos(), (double)this.range);
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		LookTargetUtil.walkTowards(entity, (LookTarget)((Optional)this.lookTargetFunction.apply(entity)).get(), this.speed, this.range / 2);
	}
}
