package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class ForgetBellRingTask extends Task<LivingEntity> {
	private final int distance;
	private final int maxHiddenTicks;
	private int hiddenTicks;

	public ForgetBellRingTask(int maxHiddenSeconds, int distance) {
		super(ImmutableMap.of(MemoryModuleType.field_19008, MemoryModuleState.field_18456, MemoryModuleType.field_19009, MemoryModuleState.field_18456));
		this.maxHiddenTicks = maxHiddenSeconds * 20;
		this.hiddenTicks = 0;
		this.distance = distance;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		Brain<?> brain = entity.getBrain();
		Optional<Long> optional = brain.getOptionalMemory(MemoryModuleType.field_19009);
		boolean bl = (Long)optional.get() + 300L <= time;
		if (this.hiddenTicks <= this.maxHiddenTicks && !bl) {
			BlockPos blockPos = ((GlobalPos)brain.getOptionalMemory(MemoryModuleType.field_19008).get()).getPos();
			if (blockPos.isWithinDistance(entity.getBlockPos(), (double)this.distance)) {
				this.hiddenTicks++;
			}
		} else {
			brain.forget(MemoryModuleType.field_19009);
			brain.forget(MemoryModuleType.field_19008);
			brain.refreshActivities(world.getTimeOfDay(), world.getTime());
			this.hiddenTicks = 0;
		}
	}
}
