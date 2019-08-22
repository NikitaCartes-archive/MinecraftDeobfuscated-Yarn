package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class ForgetBellRingTask extends Task<LivingEntity> {
	private final int field_19154;
	private final int field_19000;
	private int field_19001;

	public ForgetBellRingTask(int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleState.VALUE_PRESENT));
		this.field_19000 = i * 20;
		this.field_19001 = 0;
		this.field_19154 = j;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		Optional<Long> optional = brain.getOptionalMemory(MemoryModuleType.HEARD_BELL_TIME);
		boolean bl = (Long)optional.get() + 300L <= l;
		if (this.field_19001 <= this.field_19000 && !bl) {
			BlockPos blockPos = ((GlobalPos)brain.getOptionalMemory(MemoryModuleType.HIDING_PLACE).get()).getPos();
			if (blockPos.isWithinDistance(new BlockPos(livingEntity), (double)(this.field_19154 + 1))) {
				this.field_19001++;
			}
		} else {
			brain.forget(MemoryModuleType.HEARD_BELL_TIME);
			brain.forget(MemoryModuleType.HIDING_PLACE);
			brain.refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
			this.field_19001 = 0;
		}
	}
}
