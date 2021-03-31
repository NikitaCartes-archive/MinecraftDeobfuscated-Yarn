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

public class JungleFoliagePlacer extends FoliagePlacer {
	public static final Codec<JungleFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillFoliagePlacerFields(instance)
				.and(Codec.intRange(0, 16).fieldOf("height").forGetter(jungleFoliagePlacer -> jungleFoliagePlacer.height))
				.apply(instance, JungleFoliagePlacer::new)
	);
	protected final int height;

	public JungleFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
		super(radius, offset);
		this.height = height;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.JUNGLE_FOLIAGE_PLACER;
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
		int k = treeNode.isGiantTrunk() ? radius : 1 + random.nextInt(2);

		for (int l = offset; l >= offset - k; l--) {
			int m = j + treeNode.getFoliageRadius() + 1 - l;
			this.generateSquare(testableWorld, biConsumer, random, treeFeatureConfig, treeNode.getCenter(), m, l, treeNode.isGiantTrunk());
		}
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx + dz >= 7 ? true : dx * dx + dz * dz > radius * radius;
	}
}
