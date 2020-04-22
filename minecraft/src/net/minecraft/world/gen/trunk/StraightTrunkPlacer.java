package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class StraightTrunkPlacer extends TrunkPlacer {
	public StraightTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
		super(baseHeight, firstRandomHeight, secondRandomHeight, TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
	}

	public <T> StraightTrunkPlacer(Dynamic<T> data) {
		this(data.get("base_height").asInt(0), data.get("height_rand_a").asInt(0), data.get("height_rand_b").asInt(0));
	}

	@Override
	public List<FoliagePlacer.class_5208> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig
	) {
		method_27400(world, pos.down());

		for (int i = 0; i < trunkHeight; i++) {
			method_27402(world, random, pos.up(i), set, blockBox, treeFeatureConfig);
		}

		return ImmutableList.of(new FoliagePlacer.class_5208(pos.up(trunkHeight), 0, false));
	}
}
