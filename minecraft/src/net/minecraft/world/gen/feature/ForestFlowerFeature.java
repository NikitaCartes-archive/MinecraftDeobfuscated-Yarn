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
		Blocks.DANDELION,
		Blocks.POPPY,
		Blocks.BLUE_ORCHID,
		Blocks.ALLIUM,
		Blocks.AZURE_BLUET,
		Blocks.RED_TULIP,
		Blocks.ORANGE_TULIP,
		Blocks.WHITE_TULIP,
		Blocks.PINK_TULIP,
		Blocks.OXEYE_DAISY,
		Blocks.CORNFLOWER,
		Blocks.LILY_OF_THE_VALLEY
	};

	public ForestFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public BlockState getFlowerToPlace(Random random, BlockPos pos) {
		double d = MathHelper.clamp((1.0 + Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 48.0, (double)pos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
		Block block = FLOWERS[(int)(d * (double)FLOWERS.length)];
		return block == Blocks.BLUE_ORCHID ? Blocks.POPPY.getDefaultState() : block.getDefaultState();
	}
}
