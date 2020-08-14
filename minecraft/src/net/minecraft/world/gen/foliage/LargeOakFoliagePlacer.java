package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class LargeOakFoliagePlacer extends BlobFoliagePlacer {
	public static final Codec<LargeOakFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> method_28838(instance).apply(instance, LargeOakFoliagePlacer::new)
	);

	public LargeOakFoliagePlacer(UniformIntDistribution uniformIntDistribution, UniformIntDistribution uniformIntDistribution2, int i) {
		super(uniformIntDistribution, uniformIntDistribution2, i);
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.FANCY_FOLIAGE_PLACER;
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
		for (int j = i; j >= i - foliageHeight; j--) {
			int k = radius + (j != i && j != i - foliageHeight ? 1 : 0);
			this.generate(world, random, config, treeNode.getCenter(), k, leaves, j, treeNode.isGiantTrunk(), blockBox);
		}
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return MathHelper.square((float)baseHeight + 0.5F) + MathHelper.square((float)dy + 0.5F) > (float)(dz * dz);
	}
}
