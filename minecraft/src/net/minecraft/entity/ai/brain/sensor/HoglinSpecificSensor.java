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
			MemoryModuleType.field_18442,
			MemoryModuleType.field_22474,
			MemoryModuleType.field_22345,
			MemoryModuleType.field_22344,
			MemoryModuleType.field_22347,
			MemoryModuleType.field_22348
		);
	}

	protected void method_24639(ServerWorld serverWorld, HoglinEntity hoglinEntity) {
		Brain<?> brain = hoglinEntity.getBrain();
		brain.remember(MemoryModuleType.field_22474, this.findNearestWarpedFungus(serverWorld, hoglinEntity));
		Optional<PiglinEntity> optional = Optional.empty();
		int i = 0;
		List<HoglinEntity> list = Lists.<HoglinEntity>newArrayList();

		for (LivingEntity livingEntity : (List)brain.getOptionalMemory(MemoryModuleType.field_18442).orElse(Lists.newArrayList())) {
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

		brain.remember(MemoryModuleType.field_22345, optional);
		brain.remember(MemoryModuleType.field_22344, list);
		brain.remember(MemoryModuleType.field_22347, i);
		brain.remember(MemoryModuleType.field_22348, list.size());
	}

	private Optional<BlockPos> findNearestWarpedFungus(ServerWorld world, HoglinEntity hoglin) {
		return BlockPos.findClosest(hoglin.getBlockPos(), 8, 4, blockPos -> world.getBlockState(blockPos).isIn(BlockTags.field_22466));
	}
}
