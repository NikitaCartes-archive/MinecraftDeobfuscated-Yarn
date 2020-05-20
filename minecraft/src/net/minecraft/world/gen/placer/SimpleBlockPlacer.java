package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class SimpleBlockPlacer extends BlockPlacer {
	public static final Codec<SimpleBlockPlacer> field_24870 = Codec.unit((Supplier<SimpleBlockPlacer>)(() -> SimpleBlockPlacer.field_24871));
	public static final SimpleBlockPlacer field_24871 = new SimpleBlockPlacer();

	@Override
	protected BlockPlacerType<?> method_28673() {
		return BlockPlacerType.SIMPLE_BLOCK_PLACER;
	}

	@Override
	public void method_23403(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, Random random) {
		worldAccess.setBlockState(blockPos, blockState, 2);
	}
}
