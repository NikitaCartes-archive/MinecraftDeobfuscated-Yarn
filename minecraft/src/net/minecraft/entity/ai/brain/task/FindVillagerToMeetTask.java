package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class FindVillagerToMeetTask extends Task<LivingEntity> {
	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		Brain<?> brain = livingEntity.getBrain();
		Optional<GlobalPos> optional = brain.getMemory(MemoryModuleType.field_18440);
		return serverWorld.getRandom().nextInt(100) == 0
			&& optional.isPresent()
			&& Objects.equals(serverWorld.getDimension().getType(), ((GlobalPos)optional.get()).getDimension())
			&& livingEntity.squaredDistanceTo(((GlobalPos)optional.get()).getPos()) <= 16.0
			&& ((List)brain.getMemory(MemoryModuleType.field_18442).get()).stream().anyMatch(livingEntityx -> EntityType.VILLAGER.equals(livingEntityx.getType()));
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18458),
			Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18458),
			Pair.of(MemoryModuleType.field_18440, MemoryModuleState.field_18456),
			Pair.of(MemoryModuleType.field_18442, MemoryModuleState.field_18456),
			Pair.of(MemoryModuleType.field_18447, MemoryModuleState.field_18457)
		);
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		brain.getMemory(MemoryModuleType.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> EntityType.VILLAGER.equals(livingEntityxx.getType()))
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
