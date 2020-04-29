package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class JungleFoliagePlacer extends FoliagePlacer {
	protected final int height;

	public JungleFoliagePlacer(int radius, int randomRadius, int offset, int randomOffset, int height) {
		super(radius, randomRadius, offset, randomOffset, FoliagePlacerType.JUNGLE_FOLIAGE_PLACER);
		this.height = height;
	}

	public <T> JungleFoliagePlacer(Dynamic<T> dynamic) {
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
		int j = treeNode.isGiantTrunk() ? foliageHeight : 1 + random.nextInt(2);

		for (int k = i; k >= i - j; k--) {
			int l = radius + treeNode.getFoliageRadius() + 1 - k;
			this.generate(world, random, config, treeNode.getCenter(), l, leaves, k, treeNode.isGiantTrunk());
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight + dy >= 7 ? true : baseHeight * baseHeight + dy * dy > dz * dz;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("height"), ops.createInt(this.height));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
