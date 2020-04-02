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
		int j = 0;

		for (int k = foliageHeight + i; k >= i; k--) {
			this.generate(world, random, config, pos, foliageHeight, k, j, leaves);
			if (j >= 1 && k == i + 1) {
				j--;
			} else if (j < radius) {
				j++;
			}
		}
	}

	@Override
	public int getRadius(Random random, int baseHeight, BranchedTreeFeatureConfig config) {
		return this.radius + random.nextInt(this.randomRadius + 1) + random.nextInt(baseHeight + 1);
	}

	@Override
	public int getHeight(Random random, int trunkHeight) {
		return this.height + random.nextInt(this.randomHeight + 1);
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
		builder.put(ops.createString("height"), ops.createInt(this.height)).put(ops.createString("height_random"), ops.createInt(this.randomHeight));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
