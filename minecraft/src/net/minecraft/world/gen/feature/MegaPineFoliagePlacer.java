package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class MegaPineFoliagePlacer extends FoliagePlacer {
	private final int heightRange;
	private final int crownHeight;

	public MegaPineFoliagePlacer(int radius, int randomRadius, int offset, int randomOffset, int heightRange, int crownHeight) {
		super(radius, randomRadius, offset, randomOffset, FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER);
		this.heightRange = heightRange;
		this.crownHeight = crownHeight;
	}

	public <T> MegaPineFoliagePlacer(Dynamic<T> dynamic) {
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
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves,
		int i
	) {
		BlockPos blockPos = treeNode.getCenter();
		int j = 0;

		for (int k = blockPos.getY() - foliageHeight + i; k <= blockPos.getY() + i; k++) {
			int l = blockPos.getY() - k;
			int m = radius + treeNode.getFoliageRadius() + MathHelper.floor((float)l / (float)foliageHeight * 3.5F);
			int n;
			if (l > 0 && m == j && (k & 1) == 0) {
				n = m + 1;
			} else {
				n = m;
			}

			this.generate(world, random, config, new BlockPos(blockPos.getX(), k, blockPos.getZ()), n, leaves, 0, treeNode.isGiantTrunk());
			j = m;
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return random.nextInt(this.heightRange + 1) + this.crownHeight;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight + dy >= 7 ? true : baseHeight * baseHeight + dy * dy > dz * dz;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("height_rand"), ops.createInt(this.heightRange));
		builder.put(ops.createString("crown_height"), ops.createInt(this.crownHeight));
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
