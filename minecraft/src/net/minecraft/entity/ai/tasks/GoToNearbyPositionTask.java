package net.minecraft.entity.ai.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class GoToNearbyPositionTask extends Task<MobEntityWithAi> {
	private final MemoryModuleType<GlobalPos> memoryModuleType;
	private final int field_18863;
	private final int maxDistance;
	private long nextRunTime;

	public GoToNearbyPositionTask(MemoryModuleType<GlobalPos> memoryModuleType, int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18458, memoryModuleType, MemoryModuleState.field_18456));
		this.memoryModuleType = memoryModuleType;
		this.field_18863 = i;
		this.maxDistance = j;
	}

	protected boolean method_19607(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Optional<GlobalPos> optional = mobEntityWithAi.getBrain().getOptionalMemory(this.memoryModuleType);
		return optional.isPresent()
			&& Objects.equals(serverWorld.getDimension().getType(), ((GlobalPos)optional.get()).getDimension())
			&& ((GlobalPos)optional.get()).getPos().isWithinDistance(mobEntityWithAi.getPos(), (double)this.maxDistance);
	}

	protected void method_19608(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		if (l > this.nextRunTime) {
			Brain<?> brain = mobEntityWithAi.getBrain();
			Optional<GlobalPos> optional = brain.getOptionalMemory(this.memoryModuleType);
			optional.ifPresent(globalPos -> brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(globalPos.getPos(), 0.4F, this.field_18863)));
			this.nextRunTime = l + 80L;
		}
	}
}
