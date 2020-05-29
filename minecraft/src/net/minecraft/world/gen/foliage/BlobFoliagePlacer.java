package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products.P5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class BlobFoliagePlacer extends FoliagePlacer {
	public static final Codec<BlobFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> method_28838(instance).apply(instance, BlobFoliagePlacer::new));
	protected final int height;

	protected static <P extends BlobFoliagePlacer> P5<Mu<P>, Integer, Integer, Integer, Integer, Integer> method_28838(Instance<P> instance) {
		return method_28846(instance).and(Codec.INT.fieldOf("height").forGetter(blobFoliagePlacer -> blobFoliagePlacer.height));
	}

	protected BlobFoliagePlacer(int i, int j, int k, int l, int m, FoliagePlacerType<?> foliagePlacerType) {
		super(i, j, k, l);
		this.height = m;
	}

	public BlobFoliagePlacer(int i, int j, int k, int l, int m) {
		this(i, j, k, l, m, FoliagePlacerType.BLOB_FOLIAGE_PLACER);
	}

	@Override
	protected FoliagePlacerType<?> method_28843() {
		return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
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
