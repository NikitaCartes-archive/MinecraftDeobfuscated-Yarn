package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class VillagerWalkTowardsTask extends Task<VillagerEntity> {
	private final MemoryModuleType<GlobalPos> destination;
	private final float speed;
	private final int completionRange;
	private final int field_18385;
	private final int maxRunTime;

	public VillagerWalkTowardsTask(MemoryModuleType<GlobalPos> memoryModuleType, float f, int i, int j, int k) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_19293,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18457,
				memoryModuleType,
				MemoryModuleState.field_18456
			)
		);
		this.destination = memoryModuleType;
		this.speed = f;
		this.completionRange = i;
		this.field_18385 = j;
		this.maxRunTime = k;
	}

	protected void method_19509(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<?> brain = villagerEntity.getBrain();
		brain.getOptionalMemory(this.destination).ifPresent(globalPos -> {
			if (this.method_19597(serverWorld, villagerEntity, globalPos) || this.shouldGiveUp(serverWorld, villagerEntity)) {
				villagerEntity.releaseTicketFor(this.destination);
				brain.forget(this.destination);
				brain.putMemory(MemoryModuleType.field_19293, l);
			} else if (!this.reachedDestination(serverWorld, villagerEntity, globalPos)) {
				brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(globalPos.getPos(), this.speed, this.completionRange));
			}
		});
	}

	private boolean shouldGiveUp(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Optional<Long> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_19293);
		return optional.isPresent() ? serverWorld.getTime() - (Long)optional.get() > (long)this.maxRunTime : false;
	}

	private boolean method_19597(ServerWorld serverWorld, VillagerEntity villagerEntity, GlobalPos globalPos) {
		return globalPos.getDimension() != serverWorld.method_8597().method_12460()
			|| globalPos.getPos().getManhattanDistance(new BlockPos(villagerEntity)) > this.field_18385;
	}

	private boolean reachedDestination(ServerWorld serverWorld, VillagerEntity villagerEntity, GlobalPos globalPos) {
		return globalPos.getDimension() == serverWorld.method_8597().method_12460()
			&& globalPos.getPos().getManhattanDistance(new BlockPos(villagerEntity)) <= this.completionRange;
	}
}
