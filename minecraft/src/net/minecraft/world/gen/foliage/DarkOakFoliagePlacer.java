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

public class DarkOakFoliagePlacer extends FoliagePlacer {
	public static final Codec<DarkOakFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillFoliagePlacerFields(instance).apply(instance, DarkOakFoliagePlacer::new)
	);

	public DarkOakFoliagePlacer(UniformIntDistribution uniformIntDistribution, UniformIntDistribution uniformIntDistribution2) {
		super(uniformIntDistribution, uniformIntDistribution2);
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER;
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
		BlockPos blockPos = treeNode.getCenter().up(offset);
		boolean bl = treeNode.isGiantTrunk();
		if (bl) {
			this.generateSquare(world, random, config, blockPos, radius + 2, leaves, -1, bl, box);
			this.generateSquare(world, random, config, blockPos, radius + 3, leaves, 0, bl, box);
			this.generateSquare(world, random, config, blockPos, radius + 2, leaves, 1, bl, box);
			if (random.nextBoolean()) {
				this.generateSquare(world, random, config, blockPos, radius, leaves, 2, bl, box);
			}
		} else {
			this.generateSquare(world, random, config, blockPos, radius + 2, leaves, -1, bl, box);
			this.generateSquare(world, random, config, blockPos, radius + 1, leaves, 0, bl, box);
		}
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return 4;
	}

	@Override
	protected boolean isPositionInvalid(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return y != 0 || !giantTrunk || dx != -radius && dx < radius || dz != -radius && dz < radius
			? super.isPositionInvalid(random, dx, y, dz, radius, giantTrunk)
			: true;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		if (y == -1 && !giantTrunk) {
			return dx == radius && dz == radius;
		} else {
			return y == 1 ? dx + dz > radius * 2 - 2 : false;
		}
	}
}
