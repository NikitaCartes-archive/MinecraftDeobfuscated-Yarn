package net.minecraft.world.gen.foliage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

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
	public void generate(
		ModifiableTestableWorld world,
		Random random,
		BranchedTreeFeatureConfig config,
		int trunkHeight,
		BlockPos pos,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves
	) {
		int i = this.offset + random.nextInt(this.randomOffset + 1);
		int j = random.nextInt(2);
		int k = 1;
		int l = 0;

		for (int m = foliageHeight + i; m >= 0; m--) {
			this.generate(world, random, config, pos, foliageHeight, m, j, leaves);
			if (j >= k) {
				j = l;
				l = 1;
				k = Math.min(k + 1, radius);
			} else {
				j++;
			}
		}
	}

	@Override
	public int getRadius(Random random, int baseHeight, BranchedTreeFeatureConfig config) {
		return this.radius + random.nextInt(this.randomRadius + 1);
	}

	@Override
	public int getHeight(Random random, int trunkHeight) {
		return trunkHeight - this.field_23757 - random.nextInt(this.field_23758 + 1);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, int radius) {
		return Math.abs(dx) == radius && Math.abs(dz) == radius && radius > 0;
	}

	@Override
	public int getRadiusForPlacement(int trunkHeight, int baseHeight, int radius) {
		return radius <= 1 ? 0 : 2;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("trunk_height"), ops.createInt(this.field_23757)).put(ops.createString("trunk_height_random"), ops.createInt(this.field_23758));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
