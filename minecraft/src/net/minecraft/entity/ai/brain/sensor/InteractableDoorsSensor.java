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
	public void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
		this.field_18463 = serverWorld.getTime();
		DimensionType dimensionType = serverWorld.getDimension().getType();
		BlockPos blockPos = new BlockPos(livingEntity);
		List<GlobalPos> list = Lists.<GlobalPos>newArrayList();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					BlockPos blockPos2 = blockPos.add(i, j, k);
					if (serverWorld.getBlockState(blockPos2).matches(BlockTags.field_15494)) {
						list.add(GlobalPos.create(dimensionType, blockPos2));
					}
				}
			}
		}

		Brain<?> brain = livingEntity.getBrain();
		if (!list.isEmpty()) {
			brain.putMemory(MemoryModuleType.field_18450, list);
		} else {
			brain.forget(MemoryModuleType.field_18450);
		}
	}

	@Override
	protected Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18450);
	}
}
