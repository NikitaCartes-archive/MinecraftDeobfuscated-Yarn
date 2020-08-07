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
		Blocks.field_10048.getDefaultState(), Blocks.field_10270.getDefaultState(), Blocks.field_10315.getDefaultState(), Blocks.field_10156.getDefaultState()
	};
	private static final BlockState[] FLOWERS = new BlockState[]{
		Blocks.field_10449.getDefaultState(), Blocks.field_10573.getDefaultState(), Blocks.field_10554.getDefaultState(), Blocks.field_9995.getDefaultState()
	};

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.field_21307;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 200.0, (double)pos.getZ() / 200.0, false);
		if (d < -0.8) {
			return Util.getRandom(TULIPS, random);
		} else {
			return random.nextInt(3) > 0 ? Util.getRandom(FLOWERS, random) : Blocks.field_10182.getDefaultState();
		}
	}
}
