package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;

public class StartSniffingTask extends Task<WardenEntity> {
	public StartSniffingTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.LAST_DISTURBANCE,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.LAST_SNIFF,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.NEAREST_ATTACKABLE,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.DISTURBANCE_LOCATION,
				MemoryModuleState.VALUE_ABSENT
			)
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		Optional<Long> optional = brain.getOptionalMemory(MemoryModuleType.LAST_DISTURBANCE);
		boolean bl = (Boolean)optional.map(long_ -> serverWorld.getTime() - long_ >= 100L).orElse(true);
		if (bl) {
			if (brain.hasMemoryModule(MemoryModuleType.LAST_SNIFF)) {
				long l = serverWorld.getTime() - (Long)brain.getOptionalMemory(MemoryModuleType.LAST_SNIFF).get();
				return l >= 120L;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		brain.forget(MemoryModuleType.WALK_TARGET);
		wardenEntity.setPose(EntityPose.SNIFFING);
		brain.remember(MemoryModuleType.IS_SNIFFING, true);
		brain.remember(MemoryModuleType.LAST_SNIFF, l);
	}
}
