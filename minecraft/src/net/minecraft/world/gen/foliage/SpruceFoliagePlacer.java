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
	private final int field_23757;
	private final int field_23758;

	public SpruceFoliagePlacer(int i, int j, int k, int l, int m, int n) {
		super(i, j, k, l, FoliagePlacerType.SPRUCE_FOLIAGE_PLACER);
		this.field_23757 = m;
		this.field_23758 = n;
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
		TreeFeatureConfig treeFeatureConfig,
		int trunkHeight,
		FoliagePlacer.class_5208 arg,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves,
		int i
	) {
		BlockPos blockPos = arg.method_27388();
		int j = random.nextInt(2);
		int k = 1;
		int l = 0;

		for (int m = i; m >= -foliageHeight; m--) {
			this.generate(world, random, treeFeatureConfig, blockPos, j, leaves, m, arg.method_27390());
			if (j >= k) {
				j = l;
				l = 1;
				k = Math.min(k + 1, radius + arg.method_27389());
			} else {
				j++;
			}
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig treeFeatureConfig) {
		return trunkHeight - this.field_23757 - random.nextInt(this.field_23758 + 1);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight == dz && dy == dz && dz > 0;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("trunk_height"), ops.createInt(this.field_23757)).put(ops.createString("trunk_height_random"), ops.createInt(this.field_23758));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
