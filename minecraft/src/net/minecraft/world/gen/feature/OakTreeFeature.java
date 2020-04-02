package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;

public class OakTreeFeature extends BranchedTreeFeature<BranchedTreeFeatureConfig> {
	public OakTreeFeature(Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		Set<BlockPos> set,
		Set<BlockPos> set2,
		BlockBox blockBox,
		BranchedTreeFeatureConfig branchedTreeFeatureConfig
	) {
		int i = branchedTreeFeatureConfig.trunkPlacer.getHeight(random, branchedTreeFeatureConfig);
		int j = branchedTreeFeatureConfig.foliagePlacer.getHeight(random, i);
		int k = i - j;
		int l = branchedTreeFeatureConfig.foliagePlacer.getRadius(random, k, branchedTreeFeatureConfig);
		Optional<BlockPos> optional = this.findPositionToGenerate(modifiableTestableWorld, i, l, blockPos, branchedTreeFeatureConfig);
		if (!optional.isPresent()) {
			return false;
		} else {
			BlockPos blockPos2 = (BlockPos)optional.get();
			this.setToDirt(modifiableTestableWorld, blockPos2.down());
			Map<BlockPos, Integer> map = branchedTreeFeatureConfig.trunkPlacer
				.generate(modifiableTestableWorld, random, i, blockPos2, l, set, blockBox, branchedTreeFeatureConfig);
			map.forEach(
				(blockPosx, integer) -> branchedTreeFeatureConfig.foliagePlacer
						.generate(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, blockPosx, j, integer, set2)
			);
			return true;
		}
	}
}
