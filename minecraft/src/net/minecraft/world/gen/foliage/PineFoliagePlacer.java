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

public class PineFoliagePlacer extends FoliagePlacer {
	public static final Codec<PineFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillFoliagePlacerFields(instance)
				.and(IntProvider.createValidatingCodec(0, 24).fieldOf("height").forGetter(placer -> placer.height))
				.apply(instance, PineFoliagePlacer::new)
	);
	private final IntProvider height;

	public PineFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
		super(radius, offset);
		this.height = height;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.PINE_FOLIAGE_PLACER;
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
		int i = 0;

		for (int j = offset; j >= offset - foliageHeight; j--) {
			this.generateSquare(world, replacer, abstractRandom, config, treeNode.getCenter(), i, j, treeNode.isGiantTrunk());
			if (i >= 1 && j == offset - foliageHeight + 1) {
				i--;
			} else if (i < radius + treeNode.getFoliageRadius()) {
				i++;
			}
		}
	}

	@Override
	public int getRandomRadius(AbstractRandom abstractRandom, int baseHeight) {
		return super.getRandomRadius(abstractRandom, baseHeight) + abstractRandom.nextInt(Math.max(baseHeight + 1, 1));
	}

	@Override
	public int getRandomHeight(AbstractRandom abstractRandom, int trunkHeight, TreeFeatureConfig config) {
		return this.height.get(abstractRandom);
	}

	@Override
	protected boolean isInvalidForLeaves(AbstractRandom abstractRandom, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx == radius && dz == radius && radius > 0;
	}
}
