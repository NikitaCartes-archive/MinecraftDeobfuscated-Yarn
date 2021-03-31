package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class LargeOakFoliagePlacer extends BlobFoliagePlacer {
	public static final Codec<LargeOakFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> createCodec(instance).apply(instance, LargeOakFoliagePlacer::new)
	);

	public LargeOakFoliagePlacer(IntProvider intProvider, IntProvider intProvider2, int i) {
		super(intProvider, intProvider2, i);
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.FANCY_FOLIAGE_PLACER;
	}

	@Override
	protected void generate(
		TestableWorld testableWorld,
		BiConsumer<BlockPos, BlockState> biConsumer,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		int i,
		FoliagePlacer.TreeNode treeNode,
		int radius,
		int j,
		int offset
	) {
		for (int k = offset; k >= offset - radius; k--) {
			int l = j + (k != offset && k != offset - radius ? 1 : 0);
			this.generateSquare(testableWorld, biConsumer, random, treeFeatureConfig, treeNode.getCenter(), l, k, treeNode.isGiantTrunk());
		}
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return MathHelper.square((float)dx + 0.5F) + MathHelper.square((float)dz + 0.5F) > (float)(radius * radius);
	}
}
