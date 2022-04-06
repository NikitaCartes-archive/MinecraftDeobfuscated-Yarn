package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class RandomSpreadFoliagePlacer extends FoliagePlacer {
	public static final Codec<RandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillFoliagePlacerFields(instance)
				.<IntProvider, Integer>and(
					instance.group(
						IntProvider.createValidatingCodec(1, 512).fieldOf("foliage_height").forGetter(placer -> placer.foliageHeight),
						Codec.intRange(0, 256).fieldOf("leaf_placement_attempts").forGetter(placer -> placer.leafPlacementAttempts)
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
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		AbstractRandom abstractRandom,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		int offset
	) {
		BlockPos blockPos = treeNode.getCenter();
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (int i = 0; i < this.leafPlacementAttempts; i++) {
			mutable.set(
				blockPos,
				abstractRandom.nextInt(radius) - abstractRandom.nextInt(radius),
				abstractRandom.nextInt(foliageHeight) - abstractRandom.nextInt(foliageHeight),
				abstractRandom.nextInt(radius) - abstractRandom.nextInt(radius)
			);
			placeFoliageBlock(world, replacer, abstractRandom, config, mutable);
		}
	}

	@Override
	public int getRandomHeight(AbstractRandom abstractRandom, int trunkHeight, TreeFeatureConfig config) {
		return this.foliageHeight.get(abstractRandom);
	}

	@Override
	protected boolean isInvalidForLeaves(AbstractRandom abstractRandom, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return false;
	}
}
