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

public class DarkOakFoliagePlacer extends FoliagePlacer {
	public DarkOakFoliagePlacer(int radius, int randomRadius, int offset, int randomOffset) {
		super(radius, randomRadius, offset, randomOffset, FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER);
	}

	public <T> DarkOakFoliagePlacer(Dynamic<T> dynamic) {
		this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0), dynamic.get("offset").asInt(0), dynamic.get("offset_random").asInt(0));
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
		BlockPos blockPos = treeNode.getCenter().up(i);
		boolean bl = treeNode.isGiantTrunk();
		if (bl) {
			this.generate(world, random, config, blockPos, radius + 2, leaves, -1, bl);
			this.generate(world, random, config, blockPos, radius + 3, leaves, 0, bl);
			this.generate(world, random, config, blockPos, radius + 2, leaves, 1, bl);
			if (random.nextBoolean()) {
				this.generate(world, random, config, blockPos, radius, leaves, 2, bl);
			}
		} else {
			this.generate(world, random, config, blockPos, radius + 2, leaves, -1, bl);
			this.generate(world, random, config, blockPos, radius + 1, leaves, 0, bl);
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return 4;
	}

	@Override
	protected boolean method_27387(Random random, int i, int j, int k, int l, boolean bl) {
		return j != 0 || !bl || i != -l && i < l || k != -l && k < l ? super.method_27387(random, i, j, k, l, bl) : true;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		if (dx == -1 && !bl) {
			return baseHeight == dz && dy == dz;
		} else {
			return dx == 1 ? baseHeight + dy > dz * 2 - 2 : false;
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		return ops.merge(super.serialize(ops), ops.createMap(builder.build()));
	}
}
