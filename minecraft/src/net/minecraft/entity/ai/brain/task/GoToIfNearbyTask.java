package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class GoToIfNearbyTask extends Task<MobEntityWithAi> {
	private final MemoryModuleType<GlobalPos> target;
	private long nextUpdateTime;
	private final int maxDistance;

	public GoToIfNearbyTask(MemoryModuleType<GlobalPos> target, int maxDistance) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, target, MemoryModuleState.VALUE_PRESENT));
		this.target = target;
		this.maxDistance = maxDistance;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Optional<GlobalPos> optional = mobEntityWithAi.getBrain().getOptionalMemory(this.target);
		return optional.isPresent()
			&& serverWorld.getRegistryKey() == ((GlobalPos)optional.get()).getDimension()
			&& ((GlobalPos)optional.get()).getPos().isWithinDistance(mobEntityWithAi.getPos(), (double)this.maxDistance);
	}

	protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		if (l > this.nextUpdateTime) {
			Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findGroundTarget(mobEntityWithAi, 8, 6));
			mobEntityWithAi.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget(vec3d, 0.4F, 1)));
			this.nextUpdateTime = l + 180L;
		}
	}
}
