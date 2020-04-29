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

public class SpruceFoliagePlacer extends FoliagePlacer {
	private final int trunkHeight;
	private final int randomTrunkHeight;

	public SpruceFoliagePlacer(int radius, int randomRadius, int offset, int randomOffset, int trunkHeight, int randomChunkHeight) {
		super(radius, randomRadius, offset, randomOffset, FoliagePlacerType.SPRUCE_FOLIAGE_PLACER);
		this.trunkHeight = trunkHeight;
		this.randomTrunkHeight = randomChunkHeight;
	}

	public <T> SpruceFoliagePlacer(Dynamic<T> dynamic) {
		this(
			dynamic.get("radius").asInt(0),
			dynamic.get("radius_random").asInt(0),
			dynamic.get("offset").asInt(0),
			dynamic.get("offset_random").asInt(0),
			dynamic.get("trunk_height").asInt(0),
			dynamic.get("trunk_height_random").asInt(0)
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
		BlockPos blockPos = treeNode.getCenter();
		int j = random.nextInt(2);
		int k = 1;
		int l = 0;

		for (int m = i; m >= -foliageHeight; m--) {
			this.generate(world, random, config, blockPos, j, leaves, m, treeNode.isGiantTrunk());
			if (j >= k) {
				j = l;
				l = 1;
				k = Math.min(k + 1, radius + treeNode.getFoliageRadius());
			} else {
				j++;
			}
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return trunkHeight - this.trunkHeight - random.nextInt(this.randomTrunkHeight + 1);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight == dz && dy == dz && dz > 0;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("trunk_height"), ops.createInt(this.trunkHeight))
			.put(ops.createString("trunk_height_random"), ops.createInt(this.randomTrunkHeight));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
