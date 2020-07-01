package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
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
		Optional<MobEntity> optional = Optional.empty();
		List<AbstractPiglinEntity> list = Lists.<AbstractPiglinEntity>newArrayList();

		for (LivingEntity livingEntity : (List)brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of())) {
			if (livingEntity instanceof WitherSkeletonEntity || livingEntity instanceof WitherEntity) {
				optional = Optional.of((MobEntity)livingEntity);
				break;
			}
		}

		for (LivingEntity livingEntity2 : (List)brain.getOptionalMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of())) {
			if (livingEntity2 instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)livingEntity2).isAdult()) {
				list.add((AbstractPiglinEntity)livingEntity2);
			}
		}

		brain.remember(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
		brain.remember(MemoryModuleType.NEARBY_ADULT_PIGLINS, list);
	}
}
