package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class AcaciaFoliagePlacer extends FoliagePlacer {
	public AcaciaFoliagePlacer(int radius, int randomRadius, int offset, int randomOffset) {
		super(radius, randomRadius, offset, randomOffset, FoliagePlacerType.ACACIA_FOLIAGE_PLACER);
	}

	public <T> AcaciaFoliagePlacer(Dynamic<T> data) {
		this(data.get("radius").asInt(0), data.get("radius_random").asInt(0), data.get("offset").asInt(0), data.get("offset_random").asInt(0));
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
		boolean bl = treeNode.isGiantTrunk();
		BlockPos blockPos = treeNode.getCenter().up(i);
		this.generate(world, random, config, blockPos, radius + treeNode.getFoliageRadius(), leaves, -1 - foliageHeight, bl);
		this.generate(world, random, config, blockPos, radius - 1, leaves, -foliageHeight, bl);
		this.generate(world, random, config, blockPos, radius + treeNode.getFoliageRadius() - 1, leaves, 0, bl);
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return 0;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return dx == 0 ? (baseHeight > 1 || dy > 1) && baseHeight != 0 && dy != 0 : baseHeight == dz && dy == dz && dz > 0;
	}
}
