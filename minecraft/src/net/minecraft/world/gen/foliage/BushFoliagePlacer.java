package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class BushFoliagePlacer extends BlobFoliagePlacer {
	public BushFoliagePlacer(int i, int j, int k, int l, int m) {
		super(i, j, k, l, m, FoliagePlacerType.BUSH_FOLIAGE_PLACER);
	}

	public <T> BushFoliagePlacer(Dynamic<T> dynamic) {
		this(
			dynamic.get("radius").asInt(0),
			dynamic.get("radius_random").asInt(0),
			dynamic.get("offset").asInt(0),
			dynamic.get("offset_random").asInt(0),
			dynamic.get("height").asInt(0)
		);
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
		int i
	) {
		for (int j = i; j >= i - foliageHeight; j--) {
			int k = radius + treeNode.getFoliageRadius() - 1 - j;
			this.generate(world, random, config, treeNode.getCenter(), k, leaves, j, treeNode.isGiantTrunk());
		}
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight == dz && dy == dz && random.nextInt(2) == 0;
	}
}
