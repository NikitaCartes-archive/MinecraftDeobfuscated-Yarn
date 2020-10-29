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
		instance -> fillFoliagePlacerFields(instance)
				.and(UniformIntDistribution.createValidatedCodec(0, 16, 8).fieldOf("height").forGetter(pineFoliagePlacer -> pineFoliagePlacer.height))
				.apply(instance, PineFoliagePlacer::new)
	);
	private final UniformIntDistribution height;

	public PineFoliagePlacer(UniformIntDistribution radius, UniformIntDistribution offset, UniformIntDistribution height) {
		super(radius, offset);
		this.height = height;
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
		int offset,
		BlockBox box
	) {
		int i = 0;

		for (int j = offset; j >= offset - foliageHeight; j--) {
			this.generateSquare(world, random, config, treeNode.getCenter(), i, leaves, j, treeNode.isGiantTrunk(), box);
			if (i >= 1 && j == offset - foliageHeight + 1) {
				i--;
			} else if (i < radius + treeNode.getFoliageRadius()) {
				i++;
			}
		}
	}

	@Override
	public int getRandomRadius(Random random, int baseHeight) {
		return super.getRandomRadius(random, baseHeight) + random.nextInt(baseHeight + 1);
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height.getValue(random);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int y, int dz, boolean giantTrunk) {
		return baseHeight == dz && y == dz && dz > 0;
	}
}
