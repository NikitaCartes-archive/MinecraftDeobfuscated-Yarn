package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class class_4145 extends class_4148<LivingEntity> {
	@Override
	public void method_19101(ServerWorld serverWorld, LivingEntity livingEntity) {
		this.field_18463 = serverWorld.getTime();
		DimensionType dimensionType = serverWorld.method_8597().method_12460();
		BlockPos blockPos = new BlockPos(livingEntity);
		List<class_4208> list = Lists.<class_4208>newArrayList();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					BlockPos blockPos2 = blockPos.add(i, j, k);
					if (serverWorld.method_8320(blockPos2).method_11602(BlockTags.field_15494)) {
						list.add(class_4208.method_19443(dimensionType, blockPos2));
					}
				}
			}
		}

		class_4095<?> lv = livingEntity.method_18868();
		if (!list.isEmpty()) {
			lv.method_18878(class_4140.field_18450, list);
		} else {
			lv.method_18875(class_4140.field_18450);
		}
	}

	@Override
	protected Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18450);
	}
}
