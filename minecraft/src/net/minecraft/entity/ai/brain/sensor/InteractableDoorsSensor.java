package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class InteractableDoorsSensor extends Sensor<LivingEntity> {
	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		DimensionType dimensionType = world.getDimension().getType();
		BlockPos blockPos = entity.getSenseCenterPos();
		List<GlobalPos> list = Lists.<GlobalPos>newArrayList();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					BlockPos blockPos2 = blockPos.add(i, j, k);
					if (world.getBlockState(blockPos2).matches(BlockTags.WOODEN_DOORS)) {
						list.add(GlobalPos.create(dimensionType, blockPos2));
					}
				}
			}
		}

		Brain<?> brain = entity.getBrain();
		if (!list.isEmpty()) {
			brain.remember(MemoryModuleType.INTERACTABLE_DOORS, list);
		} else {
			brain.forget(MemoryModuleType.INTERACTABLE_DOORS);
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.INTERACTABLE_DOORS);
	}
}
