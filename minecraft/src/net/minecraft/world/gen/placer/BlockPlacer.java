package net.minecraft.world.gen.placer;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public abstract class BlockPlacer implements DynamicSerializable {
	protected final BlockPlacerType<?> type;

	protected BlockPlacer(BlockPlacerType<?> type) {
		this.type = type;
	}

	public abstract void method_23403(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random);

	public static BlockPlacer method_26594(Random random) {
		return (BlockPlacer)(random.nextBoolean() ? new SimpleBlockPlacer() : new ColumnPlacer(random.nextInt(10), random.nextInt(5)));
	}
}
