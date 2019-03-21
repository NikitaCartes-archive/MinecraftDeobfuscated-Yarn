package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class NearestLivingEntitiesSensor extends Sensor<LivingEntity> {
	private static final TargetPredicate field_18965 = new TargetPredicate().setBaseMaxDistance(16.0).includeTeammates().ignoreEntityTargetRules().includeHidden();

	@Override
	public void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
		this.field_18463 = serverWorld.getTime();
		List<LivingEntity> list = serverWorld.getEntities(
			LivingEntity.class, livingEntity.getBoundingBox().expand(16.0, 16.0, 16.0), livingEntity2 -> livingEntity2 != livingEntity && livingEntity2.isValid()
		);
		list.sort(Comparator.comparingDouble(livingEntity::squaredDistanceTo));
		Brain<?> brain = livingEntity.getBrain();
		brain.putMemory(MemoryModuleType.field_18441, list);
		brain.putMemory(
			MemoryModuleType.field_18442,
			(List<LivingEntity>)list.stream()
				.filter(livingEntity2 -> field_18965.test(livingEntity, livingEntity2))
				.filter(livingEntity::canSee)
				.collect(Collectors.toList())
		);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18441, MemoryModuleType.field_18442);
	}
}
