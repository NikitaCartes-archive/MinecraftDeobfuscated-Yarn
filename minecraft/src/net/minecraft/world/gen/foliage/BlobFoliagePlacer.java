package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class BlobFoliagePlacer extends FoliagePlacer {
	public static final Codec<BlobFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> method_28838(instance).apply(instance, BlobFoliagePlacer::new));
	protected final int height;

	protected static <P extends BlobFoliagePlacer> P3<Mu<P>, UniformIntDistribution, UniformIntDistribution, Integer> method_28838(Instance<P> instance) {
		return method_30411(instance).and(Codec.intRange(0, 16).fieldOf("height").forGetter(blobFoliagePlacer -> blobFoliagePlacer.height));
	}

	public BlobFoliagePlacer(UniformIntDistribution uniformIntDistribution, UniformIntDistribution uniformIntDistribution2, int i) {
		super(uniformIntDistribution, uniformIntDistribution2);
		this.height = i;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.field_21299;
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
			int k = Math.max(radius + treeNode.getFoliageRadius() - 1 - j / 2, 0);
			this.generate(world, random, config, treeNode.getCenter(), k, leaves, j, treeNode.isGiantTrunk(), blockBox);
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight == dz && dy == dz && (random.nextInt(2) == 0 || dx == 0);
	}
}
