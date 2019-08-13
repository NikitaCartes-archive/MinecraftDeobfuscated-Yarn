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
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		Brain<?> brain = livingEntity.getBrain();
		Optional<GlobalPos> optional = brain.getOptionalMemory(MemoryModuleType.field_18440);
		return serverWorld.getRandom().nextInt(100) == 0
			&& optional.isPresent()
			&& Objects.equals(serverWorld.getDimension().getType(), ((GlobalPos)optional.get()).getDimension())
			&& ((GlobalPos)optional.get()).getPos().isWithinDistance(livingEntity.getPos(), 4.0)
			&& ((List)brain.getOptionalMemory(MemoryModuleType.field_18442).get())
				.stream()
				.anyMatch(livingEntityx -> EntityType.field_6077.equals(livingEntityx.getType()));
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		brain.getOptionalMemory(MemoryModuleType.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> EntityType.field_6077.equals(livingEntityxx.getType()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= 32.0)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							brain.putMemory(MemoryModuleType.field_18447, livingEntityxx);
							brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntityxx));
							brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(new EntityPosWrapper(livingEntityxx), 0.3F, 1));
						})
			);
	}
}
