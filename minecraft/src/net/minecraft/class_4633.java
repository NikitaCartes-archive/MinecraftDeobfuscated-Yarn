package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class class_4633 extends class_4629 {
	public class_4633() {
		super(class_4630.SIMPLE_BLOCK_PLACER);
	}

	public <T> class_4633(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		iWorld.setBlockState(blockPos, blockState, 2);
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
				dynamicOps,
				dynamicOps.createMap(
					ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_PLACER_TYPE.getId(this.field_21222).toString()))
				)
			)
			.getValue();
	}
}
