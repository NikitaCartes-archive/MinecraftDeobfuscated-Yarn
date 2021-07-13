package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class MeadowFlowerProvider extends BlockStateProvider {
	public static final Codec<MeadowFlowerProvider> CODEC = Codec.unit((Supplier<MeadowFlowerProvider>)(() -> MeadowFlowerProvider.INSTANCE));
	private static final BlockState[] FLOWERS = new BlockState[]{
		Blocks.TALL_GRASS.getDefaultState(),
		Blocks.ALLIUM.getDefaultState(),
		Blocks.POPPY.getDefaultState(),
		Blocks.AZURE_BLUET.getDefaultState(),
		Blocks.DANDELION.getDefaultState(),
		Blocks.CORNFLOWER.getDefaultState(),
		Blocks.OXEYE_DAISY.getDefaultState(),
		Blocks.BLUE_ORCHID.getDefaultState(),
		Blocks.GRASS.getDefaultState()
	};
	public static final int field_34245 = 1;
	public static final int field_34246 = 3;
	public static final double field_34247 = 5.0E-4;
	public static final double field_34248 = 0.1;
	public static final MeadowFlowerProvider INSTANCE = new MeadowFlowerProvider();

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.MEADOW_FLOWER_PROVIDER;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)pos.getX() * 5.0E-4, (double)pos.getZ() * 5.0E-4, false);
		int i = (int)MathHelper.lerpFromProgress(d, -1.0, 1.0, 1.0, 4.0);
		List<BlockState> list = Lists.<BlockState>newArrayList();

		for (int j = 0; j < i; j++) {
			int k = this.method_37773(pos.add(j * 234349, 0, 0), FLOWERS.length, 5.0E-4);
			list.add(FLOWERS[k]);
		}

		return (BlockState)list.get(this.method_37773(pos, list.size(), 0.1));
	}

	private int method_37773(BlockPos blockPos, int i, double d) {
		double e = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() * d, (double)blockPos.getZ() * d, false);
		double f = MathHelper.clamp((1.0 + e) / 2.0, 0.0, 0.9999);
		return (int)(f * (double)i);
	}
}
