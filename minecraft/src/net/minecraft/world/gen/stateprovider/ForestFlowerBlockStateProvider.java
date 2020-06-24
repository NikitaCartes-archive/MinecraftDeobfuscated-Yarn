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
		Blocks.DANDELION.getDefaultState(),
		Blocks.POPPY.getDefaultState(),
		Blocks.ALLIUM.getDefaultState(),
		Blocks.AZURE_BLUET.getDefaultState(),
		Blocks.RED_TULIP.getDefaultState(),
		Blocks.ORANGE_TULIP.getDefaultState(),
		Blocks.WHITE_TULIP.getDefaultState(),
		Blocks.PINK_TULIP.getDefaultState(),
		Blocks.OXEYE_DAISY.getDefaultState(),
		Blocks.CORNFLOWER.getDefaultState(),
		Blocks.LILY_OF_THE_VALLEY.getDefaultState()
	};
	public static final ForestFlowerBlockStateProvider INSTANCE = new ForestFlowerBlockStateProvider();

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.FOREST_FLOWER_PROVIDER;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		double d = MathHelper.clamp((1.0 + Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 48.0, (double)pos.getZ() / 48.0, false)) / 2.0, 0.0, 0.9999);
		return FLOWERS[(int)(d * (double)FLOWERS.length)];
	}
}
