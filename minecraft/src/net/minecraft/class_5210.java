package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class class_5210 extends FoliagePlacer {
	private final int field_24167;
	private final int field_24168;

	public class_5210(int i, int j, int k, int l, int m, int n) {
		super(i, j, k, l, FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER);
		this.field_24167 = m;
		this.field_24168 = n;
	}

	public <T> class_5210(Dynamic<T> dynamic) {
		this(
			dynamic.get("radius").asInt(0),
			dynamic.get("radius_random").asInt(0),
			dynamic.get("offset").asInt(0),
			dynamic.get("offset_random").asInt(0),
			dynamic.get("height_rand").asInt(0),
			dynamic.get("crown_height").asInt(0)
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
		int j = 0;

		for (int k = blockPos.getY() - foliageHeight + i; k <= blockPos.getY() + i; k++) {
			int l = blockPos.getY() - k;
			int m = radius + arg.method_27389() + MathHelper.floor((float)l / (float)foliageHeight * 3.5F);
			int n;
			if (l > 0 && m == j && (k & 1) == 0) {
				n = m + 1;
			} else {
				n = m;
			}

			this.generate(world, random, treeFeatureConfig, new BlockPos(blockPos.getX(), k, blockPos.getZ()), n, leaves, 0, arg.method_27390());
			j = m;
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig treeFeatureConfig) {
		return random.nextInt(this.field_24167 + 1) + this.field_24168;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight + dy >= 7 ? true : baseHeight * baseHeight + dy * dy > dz * dz;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("height_rand"), ops.createInt(this.field_24167));
		builder.put(ops.createString("crown_height"), ops.createInt(this.field_24168));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
