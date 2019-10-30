package net.minecraft.world.gen.placer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class SimpleBlockPlacer extends BlockPlacer {
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
	public <T> T serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("type"), ops.createString(Registry.BLOCK_PLACER_TYPE.getId(this.type).toString()))))
			.getValue();
	}
}
