package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class PlainsFlowerBlockStateProvider extends BlockStateProvider {
	public static final Codec<PlainsFlowerBlockStateProvider> CODEC = Codec.unit(
		(Supplier<PlainsFlowerBlockStateProvider>)(() -> PlainsFlowerBlockStateProvider.INSTANCE)
	);
	public static final PlainsFlowerBlockStateProvider INSTANCE = new PlainsFlowerBlockStateProvider();
	private static final BlockState[] TULIPS = new BlockState[]{
		Blocks.ORANGE_TULIP.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState()
	};
	private static final BlockState[] FLOWERS = new BlockState[]{
		Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState()
	};

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.PLAIN_FLOWER_PROVIDER;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 200.0, (double)pos.getZ() / 200.0, false);
		if (d < -0.8) {
			return Util.getRandom(TULIPS, random);
		} else {
			return random.nextInt(3) > 0 ? Util.getRandom(FLOWERS, random) : Blocks.DANDELION.getDefaultState();
		}
	}
}
