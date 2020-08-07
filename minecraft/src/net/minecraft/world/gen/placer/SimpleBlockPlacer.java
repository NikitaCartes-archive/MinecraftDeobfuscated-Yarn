package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class SimpleBlockPlacer extends BlockPlacer {
	public static final Codec<SimpleBlockPlacer> CODEC = Codec.unit((Supplier<SimpleBlockPlacer>)(() -> SimpleBlockPlacer.INSTANCE));
	public static final SimpleBlockPlacer INSTANCE = new SimpleBlockPlacer();

	@Override
	protected BlockPlacerType<?> method_28673() {
		return BlockPlacerType.field_21223;
	}

	@Override
	public void method_23403(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, Random random) {
		worldAccess.setBlockState(blockPos, blockState, 2);
	}
}
