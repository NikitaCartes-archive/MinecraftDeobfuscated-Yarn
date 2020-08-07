package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

public class MeetVillagerTask extends Task<LivingEntity> {
	public MeetVillagerTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_18446,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_18440,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_18442,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_18447,
				MemoryModuleState.field_18457
			)
		);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		Brain<?> brain = entity.getBrain();
		Optional<GlobalPos> optional = brain.getOptionalMemory(MemoryModuleType.field_18440);
		return world.getRandom().nextInt(100) == 0
			&& optional.isPresent()
			&& world.getRegistryKey() == ((GlobalPos)optional.get()).getDimension()
			&& ((GlobalPos)optional.get()).getPos().isWithinDistance(entity.getPos(), 4.0)
			&& ((List)brain.getOptionalMemory(MemoryModuleType.field_18442).get())
				.stream()
				.anyMatch(livingEntity -> EntityType.field_6077.equals(livingEntity.getType()));
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		Brain<?> brain = entity.getBrain();
		brain.getOptionalMemory(MemoryModuleType.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityx -> EntityType.field_6077.equals(livingEntityx.getType()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= 32.0)
						.findFirst()
						.ifPresent(livingEntityx -> {
							brain.remember(MemoryModuleType.field_18447, livingEntityx);
							brain.remember(MemoryModuleType.field_18446, new EntityLookTarget(livingEntityx, true));
							brain.remember(MemoryModuleType.field_18445, new WalkTarget(new EntityLookTarget(livingEntityx, false), 0.3F, 1));
						})
			);
	}
}
