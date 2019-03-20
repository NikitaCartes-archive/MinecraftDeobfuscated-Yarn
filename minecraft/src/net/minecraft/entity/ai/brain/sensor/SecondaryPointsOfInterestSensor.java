package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class SecondaryPointsOfInterestSensor extends Sensor<VillagerEntity> {
	public void method_19617(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		this.field_18463 = serverWorld.getTime();
		DimensionType dimensionType = serverWorld.getDimension().getType();
		BlockPos blockPos = new BlockPos(villagerEntity);
		List<GlobalPos> list = Lists.<GlobalPos>newArrayList();
		int i = 4;

		for (int j = -4; j <= 4; j++) {
			for (int k = -2; k <= 2; k++) {
				for (int l = -4; l <= 4; l++) {
					BlockPos blockPos2 = blockPos.add(j, k, l);
					if (villagerEntity.getVillagerData().getProfession().method_19630().contains(serverWorld.getBlockState(blockPos2).getBlock())) {
						list.add(GlobalPos.create(dimensionType, blockPos2));
					}
				}
			}
		}

		Brain<?> brain = villagerEntity.getBrain();
		if (!list.isEmpty()) {
			brain.putMemory(MemoryModuleType.field_18873, list);
		} else {
			brain.forget(MemoryModuleType.field_18873);
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18873);
	}
}
