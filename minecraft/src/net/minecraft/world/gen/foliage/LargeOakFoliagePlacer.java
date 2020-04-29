package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class LargeOakFoliagePlacer extends BlobFoliagePlacer {
	public LargeOakFoliagePlacer(int i, int j, int k, int l, int m) {
		super(i, j, k, l, m, FoliagePlacerType.FANCY_FOLIAGE_PLACER);
	}

	public <T> LargeOakFoliagePlacer(Dynamic<T> dynamic) {
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
			int k = radius + (j != i && j != i - foliageHeight ? 1 : 0);
			this.generate(world, random, config, treeNode.getCenter(), k, leaves, j, treeNode.isGiantTrunk());
		}
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return MathHelper.square((float)baseHeight + 0.5F) + MathHelper.square((float)dy + 0.5F) > (float)(dz * dz);
	}
}
