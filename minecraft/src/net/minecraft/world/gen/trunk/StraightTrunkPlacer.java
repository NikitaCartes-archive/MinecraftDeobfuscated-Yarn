package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class StraightTrunkPlacer extends TrunkPlacer {
	public StraightTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
		super(baseHeight, firstRandomHeight, secondRandomHeight, TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
	}

	public <T> StraightTrunkPlacer(Dynamic<T> data) {
		this(data.get("base_height").asInt(0), data.get("height_rand_a").asInt(0), data.get("height_rand_b").asInt(0));
	}

	@Override
	public Map<BlockPos, Integer> generate(
		ModifiableTestableWorld world,
		Random random,
		int trunkHeight,
		BlockPos pos,
		int foliageRadius,
		Set<BlockPos> logs,
		BlockBox box,
		BranchedTreeFeatureConfig config
	) {
		for (int i = 0; i < trunkHeight; i++) {
			AbstractTreeFeature.setLogBlockState(world, random, pos.up(i), logs, box, config);
		}

		return ImmutableMap.of(pos.up(trunkHeight), foliageRadius);
	}
}
