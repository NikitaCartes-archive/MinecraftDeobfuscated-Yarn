package net.minecraft.world.gen.placer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;

public class DoublePlantPlacer extends BlockPlacer {
	public DoublePlantPlacer() {
		super(BlockPlacerType.DOUBLE_PLANT_PLACER);
	}

	public <T> DoublePlantPlacer(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public void method_23403(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, Random random) {
		((TallPlantBlock)blockState.getBlock()).placeAt(worldAccess, blockPos, 2);
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("type"), ops.createString(Registry.BLOCK_PLACER_TYPE.getId(this.type).toString()))))
			.getValue();
	}
}
