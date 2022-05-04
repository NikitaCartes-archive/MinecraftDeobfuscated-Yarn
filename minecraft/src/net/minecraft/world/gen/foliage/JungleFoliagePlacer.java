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

public class JungleFoliagePlacer extends FoliagePlacer {
	public static final Codec<JungleFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillFoliagePlacerFields(instance)
				.and(Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height))
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
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		AbstractRandom random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		int offset
	) {
		int i = treeNode.isGiantTrunk() ? foliageHeight : 1 + random.nextInt(2);

		for (int j = offset; j >= offset - i; j--) {
			int k = radius + treeNode.getFoliageRadius() + 1 - j;
			this.generateSquare(world, replacer, random, config, treeNode.getCenter(), k, j, treeNode.isGiantTrunk());
		}
	}

	@Override
	public int getRandomHeight(AbstractRandom random, int trunkHeight, TreeFeatureConfig config) {
		return this.height;
	}

	@Override
	protected boolean isInvalidForLeaves(AbstractRandom random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx + dz >= 7 ? true : dx * dx + dz * dz > radius * radius;
	}
}
