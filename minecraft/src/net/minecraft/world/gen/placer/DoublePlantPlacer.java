package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class DoublePlantPlacer extends BlockPlacer {
	public static final Codec<DoublePlantPlacer> CODEC = Codec.unit((Supplier<DoublePlantPlacer>)(() -> DoublePlantPlacer.INSTANCE));
	public static final DoublePlantPlacer INSTANCE = new DoublePlantPlacer();

	@Override
	protected BlockPlacerType<?> getType() {
		return BlockPlacerType.DOUBLE_PLANT_PLACER;
	}

	@Override
	public void generate(WorldAccess world, BlockPos pos, BlockState state, Random random) {
		((TallPlantBlock)state.getBlock()).placeAt(world, state, pos, 2);
	}
}
