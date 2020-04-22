package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class class_5209 extends FoliagePlacer {
	protected final int field_24166;

	public class_5209(int i, int j, int k, int l, int m) {
		super(i, j, k, l, FoliagePlacerType.JUNGLE_FOLIAGE_PLACER);
		this.field_24166 = m;
	}

	public <T> class_5209(Dynamic<T> dynamic) {
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
		TreeFeatureConfig treeFeatureConfig,
		int trunkHeight,
		FoliagePlacer.class_5208 arg,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves,
		int i
	) {
		int j = arg.method_27390() ? foliageHeight : 1 + random.nextInt(2);

		for (int k = i; k >= i - j; k--) {
			int l = radius + arg.method_27389() + 1 - k;
			this.generate(world, random, treeFeatureConfig, arg.method_27388(), l, leaves, k, arg.method_27390());
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig treeFeatureConfig) {
		return this.field_24166;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight + dy >= 7 ? true : baseHeight * baseHeight + dy * dy > dz * dz;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("height"), ops.createInt(this.field_24166));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
