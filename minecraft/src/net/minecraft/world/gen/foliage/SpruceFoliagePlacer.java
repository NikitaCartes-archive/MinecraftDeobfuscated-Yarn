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

public class SpruceFoliagePlacer extends FoliagePlacer {
	public static final Codec<SpruceFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillFoliagePlacerFields(instance)
				.and(IntProvider.createValidatingCodec(0, 24).fieldOf("trunk_height").forGetter(spruceFoliagePlacer -> spruceFoliagePlacer.trunkHeight))
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
		int k = random.nextInt(2);
		int l = 1;
		int m = 0;

		for (int n = offset; n >= -radius; n--) {
			this.generateSquare(testableWorld, biConsumer, random, treeFeatureConfig, blockPos, k, n, treeNode.isGiantTrunk());
			if (k >= l) {
				k = m;
				m = 1;
				l = Math.min(l + 1, j + treeNode.getFoliageRadius());
			} else {
				k++;
			}
		}
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return Math.max(4, trunkHeight - this.trunkHeight.get(random));
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx == radius && dz == radius && radius > 0;
	}
}
