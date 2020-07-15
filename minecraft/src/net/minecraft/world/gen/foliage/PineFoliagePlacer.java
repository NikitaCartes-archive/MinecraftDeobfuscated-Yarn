package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class PineFoliagePlacer extends FoliagePlacer {
	public static final Codec<PineFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> method_30411(instance)
				.and(UniformIntDistribution.createValidatedCodec(0, 16, 8).fieldOf("height").forGetter(pineFoliagePlacer -> pineFoliagePlacer.height))
				.apply(instance, PineFoliagePlacer::new)
	);
	private final UniformIntDistribution height;

	public PineFoliagePlacer(
		UniformIntDistribution uniformIntDistribution, UniformIntDistribution uniformIntDistribution2, UniformIntDistribution uniformIntDistribution3
	) {
		super(uniformIntDistribution, uniformIntDistribution2);
		this.height = uniformIntDistribution3;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.PINE_FOLIAGE_PLACER;
	}

	@Override
	protected void generate(
		ModifiableTestableWorld world,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves,
		int i,
		BlockBox blockBox
	) {
		int j = 0;

		for (int k = i; k >= i - foliageHeight; k--) {
			this.generate(world, random, config, treeNode.getCenter(), j, leaves, k, treeNode.isGiantTrunk(), blockBox);
			if (j >= 1 && k == i - foliageHeight + 1) {
				j--;
			} else if (j < radius + treeNode.getFoliageRadius()) {
				j++;
			}
		}
	}

	@Override
	public int getRadius(Random random, int baseHeight) {
		return super.getRadius(random, baseHeight) + random.nextInt(baseHeight + 1);
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height.getValue(random);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight == dz && dy == dz && dz > 0;
	}
}
