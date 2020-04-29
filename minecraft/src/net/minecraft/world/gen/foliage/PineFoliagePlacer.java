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

public class PineFoliagePlacer extends FoliagePlacer {
	private final int height;
	private final int randomHeight;

	public PineFoliagePlacer(int i, int j, int k, int l, int m, int n) {
		super(i, j, k, l, FoliagePlacerType.PINE_FOLIAGE_PLACER);
		this.height = m;
		this.randomHeight = n;
	}

	public <T> PineFoliagePlacer(Dynamic<T> data) {
		this(
			data.get("radius").asInt(0),
			data.get("radius_random").asInt(0),
			data.get("offset").asInt(0),
			data.get("offset_random").asInt(0),
			data.get("height").asInt(0),
			data.get("height_random").asInt(0)
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
		int j = 0;

		for (int k = i; k >= i - foliageHeight; k--) {
			this.generate(world, random, config, treeNode.getCenter(), j, leaves, k, treeNode.isGiantTrunk());
			if (j >= 1 && k == i - foliageHeight + 1) {
				j--;
			} else if (j < radius + treeNode.getFoliageRadius()) {
				j++;
			}
		}
	}

	@Override
	public int getRadius(Random random, int baseHeight) {
		return super.getRadius(random, baseHeight) + random.nextInt(baseHeight + 1);
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height + random.nextInt(this.randomHeight + 1);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight == dz && dy == dz && dz > 0;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("height"), ops.createInt(this.height)).put(ops.createString("height_random"), ops.createInt(this.randomHeight));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
