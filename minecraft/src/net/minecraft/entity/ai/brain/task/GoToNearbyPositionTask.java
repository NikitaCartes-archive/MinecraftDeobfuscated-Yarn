package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

public class GoToNearbyPositionTask extends Task<MobEntityWithAi> {
	private final MemoryModuleType<GlobalPos> memoryModuleType;
	private final int completionRange;
	private final int maxDistance;
	private long nextRunTime;

	public GoToNearbyPositionTask(MemoryModuleType<GlobalPos> memoryModuleType, int completionRange, int maxDistance) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, memoryModuleType, MemoryModuleState.VALUE_PRESENT));
		this.memoryModuleType = memoryModuleType;
		this.completionRange = completionRange;
		this.maxDistance = maxDistance;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Optional<GlobalPos> optional = mobEntityWithAi.getBrain().getOptionalMemory(this.memoryModuleType);
		return optional.isPresent()
			&& Objects.equals(serverWorld.method_27983(), ((GlobalPos)optional.get()).getDimension())
			&& ((GlobalPos)optional.get()).getPos().isWithinDistance(mobEntityWithAi.getPos(), (double)this.maxDistance);
	}

	protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		if (l > this.nextRunTime) {
			Brain<?> brain = mobEntityWithAi.getBrain();
			Optional<GlobalPos> optional = brain.getOptionalMemory(this.memoryModuleType);
			optional.ifPresent(globalPos -> brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(globalPos.getPos(), 0.4F, this.completionRange)));
			this.nextRunTime = l + 80L;
		}
	}
}
