package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

public class GoToSecondaryPositionTask extends Task<VillagerEntity> {
	private final MemoryModuleType<List<GlobalPos>> secondaryPositions;
	private final MemoryModuleType<GlobalPos> primaryPosition;
	private final float speed;
	private final int completionRange;
	private final int primaryPositionActivationDistance;
	private long nextRunTime;
	@Nullable
	private GlobalPos chosenPosition;

	public GoToSecondaryPositionTask(
		MemoryModuleType<List<GlobalPos>> secondaryPositions,
		float speed,
		int completionRange,
		int primaryPositionActivationDistance,
		MemoryModuleType<GlobalPos> primaryPosition
	) {
		super(
			ImmutableMap.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.REGISTERED,
				secondaryPositions,
				MemoryModuleState.VALUE_PRESENT,
				primaryPosition,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.secondaryPositions = secondaryPositions;
		this.speed = speed;
		this.completionRange = completionRange;
		this.primaryPositionActivationDistance = primaryPositionActivationDistance;
		this.primaryPosition = primaryPosition;
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Optional<List<GlobalPos>> optional = villagerEntity.getBrain().getOptionalMemory(this.secondaryPositions);
		Optional<GlobalPos> optional2 = villagerEntity.getBrain().getOptionalMemory(this.primaryPosition);
		if (optional.isPresent() && optional2.isPresent()) {
			List<GlobalPos> list = (List<GlobalPos>)optional.get();
			if (!list.isEmpty()) {
				this.chosenPosition = (GlobalPos)list.get(serverWorld.getRandom().nextInt(list.size()));
				return this.chosenPosition != null
					&& serverWorld.getRegistryKey() == this.chosenPosition.getDimension()
					&& ((GlobalPos)optional2.get()).getPos().isWithinDistance(villagerEntity.getPos(), (double)this.primaryPositionActivationDistance);
			}
		}

		return false;
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (l > this.nextRunTime && this.chosenPosition != null) {
			villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(this.chosenPosition.getPos(), this.speed, this.completionRange));
			this.nextRunTime = l + 100L;
		}
	}
}
