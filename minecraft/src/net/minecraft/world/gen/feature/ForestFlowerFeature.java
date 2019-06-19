package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class ForestFlowerFeature extends FlowerFeature {
	private static final Block[] FLOWERS = new Block[]{
		Blocks.field_10182,
		Blocks.field_10449,
		Blocks.field_10086,
		Blocks.field_10226,
		Blocks.field_10573,
		Blocks.field_10270,
		Blocks.field_10048,
		Blocks.field_10156,
		Blocks.field_10315,
		Blocks.field_10554,
		Blocks.field_9995,
		Blocks.field_10548
	};

	public ForestFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public BlockState getFlowerToPlace(Random random, BlockPos blockPos) {
		double d = MathHelper.clamp((1.0 + Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 48.0, (double)blockPos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
		Block block = FLOWERS[(int)(d * (double)FLOWERS.length)];
		return block == Blocks.field_10086 ? Blocks.field_10449.getDefaultState() : block.getDefaultState();
	}
}
