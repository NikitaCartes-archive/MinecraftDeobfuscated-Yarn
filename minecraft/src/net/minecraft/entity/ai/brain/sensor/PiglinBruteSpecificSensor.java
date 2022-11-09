package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.server.world.ServerWorld;

public class PiglinBruteSpecificSensor extends Sensor<LivingEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		Brain<?> brain = entity.getBrain();
		List<AbstractPiglinEntity> list = Lists.<AbstractPiglinEntity>newArrayList();
		LivingTargetCache livingTargetCache = (LivingTargetCache)brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
		Optional<MobEntity> optional = livingTargetCache.findFirst(
				livingEntityx -> livingEntityx instanceof WitherSkeletonEntity || livingEntityx instanceof WitherEntity
			)
			.map(MobEntity.class::cast);

		for (LivingEntity livingEntity : (List)brain.getOptionalRegisteredMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of())) {
			if (livingEntity instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)livingEntity).isAdult()) {
				list.add((AbstractPiglinEntity)livingEntity);
			}
		}

		brain.remember(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
		brain.remember(MemoryModuleType.NEARBY_ADULT_PIGLINS, list);
	}
}
