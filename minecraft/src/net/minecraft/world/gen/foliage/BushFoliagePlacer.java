package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class BushFoliagePlacer extends BlobFoliagePlacer {
	public static final Codec<BushFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> createCodec(instance).apply(instance, BushFoliagePlacer::new));

	public BushFoliagePlacer(IntProvider intProvider, IntProvider intProvider2, int i) {
		super(intProvider, intProvider2, i);
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.BUSH_FOLIAGE_PLACER;
	}

	@Override
	protected void generate(
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		int offset
	) {
		for (int i = offset; i >= offset - foliageHeight; i--) {
			int j = radius + treeNode.getFoliageRadius() - 1 - i;
			this.generateSquare(world, replacer, random, config, treeNode.getCenter(), j, i, treeNode.isGiantTrunk());
		}
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx == radius && dz == radius && random.nextInt(2) == 0;
	}
}
