package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class ForestFlowerBlockStateProvider extends BlockStateProvider {
	public static final Codec<ForestFlowerBlockStateProvider> CODEC = Codec.unit(
		(Supplier<ForestFlowerBlockStateProvider>)(() -> ForestFlowerBlockStateProvider.INSTANCE)
	);
	private static final BlockState[] FLOWERS = new BlockState[]{
		Blocks.field_10182.getDefaultState(),
		Blocks.field_10449.getDefaultState(),
		Blocks.field_10226.getDefaultState(),
		Blocks.field_10573.getDefaultState(),
		Blocks.field_10270.getDefaultState(),
		Blocks.field_10048.getDefaultState(),
		Blocks.field_10156.getDefaultState(),
		Blocks.field_10315.getDefaultState(),
		Blocks.field_10554.getDefaultState(),
		Blocks.field_9995.getDefaultState(),
		Blocks.field_10548.getDefaultState()
	};
	public static final ForestFlowerBlockStateProvider INSTANCE = new ForestFlowerBlockStateProvider();

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.field_21308;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		double d = MathHelper.clamp((1.0 + Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 48.0, (double)pos.getZ() / 48.0, false)) / 2.0, 0.0, 0.9999);
		return FLOWERS[(int)(d * (double)FLOWERS.length)];
	}
}
