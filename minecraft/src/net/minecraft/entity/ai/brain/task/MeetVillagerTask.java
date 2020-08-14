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
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.MEETING_POINT,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.INTERACTION_TARGET,
				MemoryModuleState.VALUE_ABSENT
			)
		);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		Brain<?> brain = entity.getBrain();
		Optional<GlobalPos> optional = brain.getOptionalMemory(MemoryModuleType.MEETING_POINT);
		return world.getRandom().nextInt(100) == 0
			&& optional.isPresent()
			&& world.getRegistryKey() == ((GlobalPos)optional.get()).getDimension()
			&& ((GlobalPos)optional.get()).getPos().isWithinDistance(entity.getPos(), 4.0)
			&& ((List)brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get())
				.stream()
				.anyMatch(livingEntity -> EntityType.VILLAGER.equals(livingEntity.getType()));
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		Brain<?> brain = entity.getBrain();
		brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityx -> EntityType.VILLAGER.equals(livingEntityx.getType()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= 32.0)
						.findFirst()
						.ifPresent(livingEntityx -> {
							brain.remember(MemoryModuleType.INTERACTION_TARGET, livingEntityx);
							brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(livingEntityx, true));
							brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(livingEntityx, false), 0.3F, 1));
						})
			);
	}
}
