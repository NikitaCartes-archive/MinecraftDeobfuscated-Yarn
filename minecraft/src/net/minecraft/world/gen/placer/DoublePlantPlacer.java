package net.minecraft.world.gen.placer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.class_4629;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class DoublePlantPlacer extends class_4629 {
	public DoublePlantPlacer() {
		super(BlockPlacerType.DOUBLE_PLANT_PLACER);
	}

	public <T> DoublePlantPlacer(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		((TallPlantBlock)blockState.getBlock()).placeAt(iWorld, blockPos, 2);
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
