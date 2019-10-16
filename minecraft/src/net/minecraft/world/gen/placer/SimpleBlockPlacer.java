package net.minecraft.world.gen.placer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.class_4629;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class SimpleBlockPlacer extends class_4629 {
	public SimpleBlockPlacer() {
		super(BlockPlacerType.SIMPLE_BLOCK_PLACER);
	}

	public <T> SimpleBlockPlacer(Dynamic<T> dynamic) {
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
