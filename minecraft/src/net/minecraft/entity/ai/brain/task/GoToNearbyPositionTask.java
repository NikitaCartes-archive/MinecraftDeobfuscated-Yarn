package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;

public class GoToNearbyPositionTask extends Task<PathAwareEntity> {
	private final MemoryModuleType<GlobalPos> memoryModuleType;
	private final int completionRange;
	private final int maxDistance;
	private final float walkSpeed;
	private long nextRunTime;

	public GoToNearbyPositionTask(MemoryModuleType<GlobalPos> memoryModuleType, float walkSpeed, int completionRange, int maxDistance) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, memoryModuleType, MemoryModuleState.VALUE_PRESENT));
		this.memoryModuleType = memoryModuleType;
		this.walkSpeed = walkSpeed;
		this.completionRange = completionRange;
		this.maxDistance = maxDistance;
	}

	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		Optional<GlobalPos> optional = pathAwareEntity.getBrain().getOptionalMemory(this.memoryModuleType);
		return optional.isPresent()
			&& serverWorld.getRegistryKey() == ((GlobalPos)optional.get()).getDimension()
			&& ((GlobalPos)optional.get()).getPos().isWithinDistance(pathAwareEntity.getPos(), (double)this.maxDistance);
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		if (l > this.nextRunTime) {
			Brain<?> brain = pathAwareEntity.getBrain();
			Optional<GlobalPos> optional = brain.getOptionalMemory(this.memoryModuleType);
			optional.ifPresent(globalPos -> brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(globalPos.getPos(), this.walkSpeed, this.completionRange)));
			this.nextRunTime = l + 80L;
		}
	}
}
