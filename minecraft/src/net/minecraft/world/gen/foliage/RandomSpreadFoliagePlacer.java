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

public class RandomSpreadFoliagePlacer extends FoliagePlacer {
	public static final Codec<RandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillFoliagePlacerFields(instance)
				.<IntProvider, Integer>and(
					instance.group(
						IntProvider.createValidatingCodec(1, 512).fieldOf("foliage_height").forGetter(randomSpreadFoliagePlacer -> randomSpreadFoliagePlacer.foliageHeight),
						Codec.intRange(0, 256).fieldOf("leaf_placement_attempts").forGetter(randomSpreadFoliagePlacer -> randomSpreadFoliagePlacer.leafPlacementAttempts)
					)
				)
				.apply(instance, RandomSpreadFoliagePlacer::new)
	);
	private final IntProvider foliageHeight;
	private final int leafPlacementAttempts;

	public RandomSpreadFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider foliageHeight, int leafPlacementAttempts) {
		super(radius, offset);
		this.foliageHeight = foliageHeight;
		this.leafPlacementAttempts = leafPlacementAttempts;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.RANDOM_SPREAD_FOLIAGE_PLACER;
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
		BlockPos blockPos = treeNode.getCenter();
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (int k = 0; k < this.leafPlacementAttempts; k++) {
			mutable.set(blockPos, random.nextInt(j) - random.nextInt(j), random.nextInt(radius) - random.nextInt(radius), random.nextInt(j) - random.nextInt(j));
			placeFoliageBlock(testableWorld, biConsumer, random, treeFeatureConfig, mutable);
		}
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.foliageHeight.get(random);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return false;
	}
}
