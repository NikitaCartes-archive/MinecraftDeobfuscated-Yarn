package net.minecraft.world.gen.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class LargeOakFoliagePlacer extends BlobFoliagePlacer {
	public static final MapCodec<LargeOakFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
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
		TestableWorld world,
		FoliagePlacer.BlockPlacer placer,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		int offset
	) {
		for (int i = offset; i >= offset - foliageHeight; i--) {
			int j = radius + (i != offset && i != offset - foliageHeight ? 1 : 0);
			this.generateSquare(world, placer, random, config, treeNode.getCenter(), j, i, treeNode.isGiantTrunk());
		}
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return MathHelper.square((float)dx + 0.5F) + MathHelper.square((float)dz + 0.5F) > (float)(radius * radius);
	}
}
