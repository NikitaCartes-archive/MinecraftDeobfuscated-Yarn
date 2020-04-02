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

public class BlobFoliagePlacer extends FoliagePlacer {
	private final int height;

	public BlobFoliagePlacer(int radius, int randomRadius, int offset, int randomOffset, int height) {
		super(radius, randomRadius, offset, randomOffset, FoliagePlacerType.BLOB_FOLIAGE_PLACER);
		this.height = height;
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

		for (int j = foliageHeight + i; j >= i; j--) {
			int k = Math.max(radius - 1 - (j - foliageHeight) / 2, 0);
			this.generate(world, random, config, pos, foliageHeight, j, k, leaves);
		}
	}

	@Override
	public int getRadius(Random random, int baseHeight, BranchedTreeFeatureConfig config) {
		return this.radius + random.nextInt(this.randomRadius + 1);
	}

	@Override
	public int getHeight(Random random, int trunkHeight) {
		return this.height;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, int radius) {
		return Math.abs(dx) == radius && Math.abs(dz) == radius && (random.nextInt(2) == 0 || dy == baseHeight);
	}

	@Override
	public int getRadiusForPlacement(int trunkHeight, int baseHeight, int radius) {
		return radius == 0 ? 0 : 1;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("height"), ops.createInt(this.height));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
