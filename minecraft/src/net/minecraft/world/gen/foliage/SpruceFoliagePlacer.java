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

public class SpruceFoliagePlacer extends FoliagePlacer {
	public static final Codec<SpruceFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillFoliagePlacerFields(instance)
				.and(IntProvider.createValidatingCodec(0, 24).fieldOf("trunk_height").forGetter(placer -> placer.trunkHeight))
				.apply(instance, SpruceFoliagePlacer::new)
	);
	private final IntProvider trunkHeight;

	public SpruceFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight) {
		super(radius, offset);
		this.trunkHeight = trunkHeight;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
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
		int i = abstractRandom.nextInt(2);
		int j = 1;
		int k = 0;

		for (int l = offset; l >= -foliageHeight; l--) {
			this.generateSquare(world, replacer, abstractRandom, config, blockPos, i, l, treeNode.isGiantTrunk());
			if (i >= j) {
				i = k;
				k = 1;
				j = Math.min(j + 1, radius + treeNode.getFoliageRadius());
			} else {
				i++;
			}
		}
	}

	@Override
	public int getRandomHeight(AbstractRandom abstractRandom, int trunkHeight, TreeFeatureConfig config) {
		return Math.max(4, trunkHeight - this.trunkHeight.get(abstractRandom));
	}

	@Override
	protected boolean isInvalidForLeaves(AbstractRandom abstractRandom, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx == radius && dz == radius && radius > 0;
	}
}
