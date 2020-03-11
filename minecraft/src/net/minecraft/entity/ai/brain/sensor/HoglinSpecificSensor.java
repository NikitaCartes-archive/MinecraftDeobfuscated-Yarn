package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class HoglinSpecificSensor extends Sensor<HoglinEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(
			MemoryModuleType.VISIBLE_MOBS,
			MemoryModuleType.NEAREST_REPELLENT,
			MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN,
			MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS,
			MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
			MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT
		);
	}

	protected void sense(ServerWorld serverWorld, HoglinEntity hoglinEntity) {
		Brain<?> brain = hoglinEntity.getBrain();
		brain.remember(MemoryModuleType.NEAREST_REPELLENT, this.findNearestWarpedFungus(serverWorld, hoglinEntity));
		Optional<PiglinEntity> optional = Optional.empty();
		int i = 0;
		List<HoglinEntity> list = Lists.<HoglinEntity>newArrayList();

		for (LivingEntity livingEntity : (List)brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList())) {
			if (livingEntity instanceof PiglinEntity && !livingEntity.isBaby()) {
				i++;
				if (!optional.isPresent()) {
					optional = Optional.of((PiglinEntity)livingEntity);
				}
			}

			if (livingEntity instanceof HoglinEntity && !livingEntity.isBaby()) {
				list.add((HoglinEntity)livingEntity);
			}
		}

		brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, optional);
		brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, list);
		brain.remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, i);
		brain.remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, list.size());
	}

	private Optional<BlockPos> findNearestWarpedFungus(ServerWorld world, HoglinEntity hoglin) {
		return BlockPos.findClosest(hoglin.getSenseCenterPos(), 8, 4, blockPos -> world.getBlockState(blockPos).matches(BlockTags.HOGLIN_REPELLENTS));
	}
}
