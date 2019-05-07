package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class GoToIfNearbyTask extends Task<MobEntityWithAi> {
	private final MemoryModuleType<GlobalPos> target;
	private long nextUpdateTime;
	private final int maxDistance;

	public GoToIfNearbyTask(MemoryModuleType<GlobalPos> memoryModuleType, int i) {
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18458, memoryModuleType, MemoryModuleState.field_18456));
		this.target = memoryModuleType;
		this.maxDistance = i;
	}

	protected boolean method_18993(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Optional<GlobalPos> optional = mobEntityWithAi.getBrain().getOptionalMemory(this.target);
		return optional.isPresent()
			&& Objects.equals(serverWorld.getDimension().getType(), ((GlobalPos)optional.get()).getDimension())
			&& ((GlobalPos)optional.get()).getPos().isWithinDistance(mobEntityWithAi.getPos(), (double)this.maxDistance);
	}

	protected void method_18994(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		if (l > this.nextUpdateTime) {
			Optional<Vec3d> optional = Optional.ofNullable(PathfindingUtil.findTargetStraight(mobEntityWithAi, 8, 6));
			mobEntityWithAi.getBrain().setMemory(MemoryModuleType.field_18445, optional.map(vec3d -> new WalkTarget(vec3d, 0.4F, 1)));
			this.nextUpdateTime = l + 180L;
		}
	}
}
