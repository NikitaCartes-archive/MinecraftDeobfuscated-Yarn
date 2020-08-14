package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class DoublePlantPlacer extends BlockPlacer {
	public static final Codec<DoublePlantPlacer> field_24868 = Codec.unit((Supplier<DoublePlantPlacer>)(() -> DoublePlantPlacer.field_24869));
	public static final DoublePlantPlacer field_24869 = new DoublePlantPlacer();

	@Override
	protected BlockPlacerType<?> method_28673() {
		return BlockPlacerType.DOUBLE_PLANT_PLACER;
	}

	@Override
	public void method_23403(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, Random random) {
		((TallPlantBlock)blockState.getBlock()).placeAt(worldAccess, blockPos, 2);
	}
}
