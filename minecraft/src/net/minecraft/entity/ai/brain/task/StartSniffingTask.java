package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class StartSniffingTask extends Task<WardenEntity> {
	private static final IntProvider COOLDOWN = UniformIntProvider.create(100, 200);

	public StartSniffingTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.SNIFF_COOLDOWN,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.NEAREST_ATTACKABLE,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.DISTURBANCE_LOCATION,
				MemoryModuleState.VALUE_ABSENT
			)
		);
	}

	protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		brain.remember(MemoryModuleType.IS_SNIFFING, Unit.INSTANCE);
		brain.remember(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, (long)COOLDOWN.get(serverWorld.getRandom()));
		brain.forget(MemoryModuleType.WALK_TARGET);
		wardenEntity.setPose(EntityPose.SNIFFING);
	}
}
