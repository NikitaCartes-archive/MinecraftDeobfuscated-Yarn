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
		super(ImmutableMap.of(MemoryModuleType.field_19008, MemoryModuleState.field_18456, MemoryModuleType.field_19009, MemoryModuleState.field_18456));
		this.field_19000 = i * 20;
		this.field_19001 = 0;
		this.field_19154 = j;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		Optional<Long> optional = brain.getOptionalMemory(MemoryModuleType.field_19009);
		boolean bl = (Long)optional.get() + 300L <= l;
		if (this.field_19001 <= this.field_19000 && !bl) {
			BlockPos blockPos = ((GlobalPos)brain.getOptionalMemory(MemoryModuleType.field_19008).get()).getPos();
			if (blockPos.isWithinDistance(new BlockPos(livingEntity), (double)(this.field_19154 + 1))) {
				this.field_19001++;
			}
		} else {
			brain.forget(MemoryModuleType.field_19009);
			brain.forget(MemoryModuleType.field_19008);
			brain.refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
			this.field_19001 = 0;
		}
	}
}
