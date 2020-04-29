package net.minecraft.world.gen.foliage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class BlobFoliagePlacer extends FoliagePlacer {
	protected final int height;

	protected BlobFoliagePlacer(int i, int j, int k, int l, int m, FoliagePlacerType<?> foliagePlacerType) {
		super(i, j, k, l, foliagePlacerType);
		this.height = m;
	}

	public BlobFoliagePlacer(int radius, int randomRadius, int offset, int randomOffset, int height) {
		this(radius, randomRadius, offset, randomOffset, height, FoliagePlacerType.BLOB_FOLIAGE_PLACER);
	}

	public <T> BlobFoliagePlacer(Dynamic<T> data) {
		this(
			data.get("radius").asInt(0),
			data.get("radius_random").asInt(0),
			data.get("offset").asInt(0),
			data.get("offset_random").asInt(0),
			data.get("height").asInt(0)
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
			int k = Math.max(radius + treeNode.getFoliageRadius() - 1 - j / 2, 0);
			this.generate(world, random, config, treeNode.getCenter(), k, leaves, j, treeNode.isGiantTrunk());
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

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("height"), ops.createInt(this.height));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
