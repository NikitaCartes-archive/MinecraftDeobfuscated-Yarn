package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

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
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		Brain<?> brain = livingEntity.getBrain();
		Optional<GlobalPos> optional = brain.getOptionalMemory(MemoryModuleType.MEETING_POINT);
		return serverWorld.getRandom().nextInt(100) == 0
			&& optional.isPresent()
			&& Objects.equals(serverWorld.getDimension().getType(), ((GlobalPos)optional.get()).getDimension())
			&& ((GlobalPos)optional.get()).getPos().isWithinDistance(livingEntity.getPos(), 4.0)
			&& ((List)brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get())
				.stream()
				.anyMatch(livingEntityx -> EntityType.VILLAGER.equals(livingEntityx.getType()));
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> EntityType.VILLAGER.equals(livingEntityxx.getType()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= 32.0)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							brain.putMemory(MemoryModuleType.INTERACTION_TARGET, livingEntityxx);
							brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntityxx));
							brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(livingEntityxx), 0.3F, 1));
						})
			);
	}
}
