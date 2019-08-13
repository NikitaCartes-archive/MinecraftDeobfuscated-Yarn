package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class GoToSecondaryPositionTask extends Task<VillagerEntity> {
	private final MemoryModuleType<List<GlobalPos>> secondaryPositions;
	private final MemoryModuleType<GlobalPos> primaryPosition;
	private final float speed;
	private final int completionRange;
	private final int primaryPositionActivationDistance;
	private long nextRunTime;
	@Nullable
	private GlobalPos chosenPosition;

	public GoToSecondaryPositionTask(MemoryModuleType<List<GlobalPos>> memoryModuleType, float f, int i, int j, MemoryModuleType<GlobalPos> memoryModuleType2) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18458,
				memoryModuleType,
				MemoryModuleState.field_18456,
				memoryModuleType2,
				MemoryModuleState.field_18456
			)
		);
		this.secondaryPositions = memoryModuleType;
		this.speed = f;
		this.completionRange = i;
		this.primaryPositionActivationDistance = j;
		this.primaryPosition = memoryModuleType2;
	}

	protected boolean method_19609(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Optional<List<GlobalPos>> optional = villagerEntity.getBrain().getOptionalMemory(this.secondaryPositions);
		Optional<GlobalPos> optional2 = villagerEntity.getBrain().getOptionalMemory(this.primaryPosition);
		if (optional.isPresent() && optional2.isPresent()) {
			List<GlobalPos> list = (List<GlobalPos>)optional.get();
			if (!list.isEmpty()) {
				this.chosenPosition = (GlobalPos)list.get(serverWorld.getRandom().nextInt(list.size()));
				return this.chosenPosition != null
					&& Objects.equals(serverWorld.getDimension().getType(), this.chosenPosition.getDimension())
					&& ((GlobalPos)optional2.get()).getPos().isWithinDistance(villagerEntity.getPos(), (double)this.primaryPositionActivationDistance);
			}
		}

		return false;
	}

	protected void method_19610(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (l > this.nextRunTime && this.chosenPosition != null) {
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(this.chosenPosition.getPos(), this.speed, this.completionRange));
			this.nextRunTime = l + 100L;
		}
	}
}
