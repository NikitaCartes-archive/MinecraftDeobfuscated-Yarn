package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_4626;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;

public class OakTreeFeature extends class_4626<NormalTreeFeatureConfig> {
	public OakTreeFeature(Function<Dynamic<?>, ? extends NormalTreeFeatureConfig> function) {
		super(function);
	}

	public boolean method_23402(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		Set<BlockPos> set,
		Set<BlockPos> set2,
		BlockBox blockBox,
		NormalTreeFeatureConfig normalTreeFeatureConfig
	) {
		int i = normalTreeFeatureConfig.baseHeight
			+ random.nextInt(normalTreeFeatureConfig.heightRandA + 1)
			+ random.nextInt(normalTreeFeatureConfig.heightRandB + 1);
		int j = normalTreeFeatureConfig.trunkHeight >= 0
			? normalTreeFeatureConfig.trunkHeight + random.nextInt(normalTreeFeatureConfig.trunkHeightRandom + 1)
			: i - (normalTreeFeatureConfig.field_21266 + random.nextInt(normalTreeFeatureConfig.field_21267 + 1));
		int k = normalTreeFeatureConfig.foliagePlacer.method_23452(random, j, i, normalTreeFeatureConfig);
		Optional<BlockPos> optional = this.method_23378(modifiableTestableWorld, i, j, k, blockPos, normalTreeFeatureConfig);
		if (!optional.isPresent()) {
			return false;
		} else {
			BlockPos blockPos2 = (BlockPos)optional.get();
			this.setToDirt(modifiableTestableWorld, blockPos2.method_10074());
			normalTreeFeatureConfig.foliagePlacer.method_23448(modifiableTestableWorld, random, normalTreeFeatureConfig, i, j, k, blockPos2, set2);
			this.method_23379(
				modifiableTestableWorld,
				random,
				i,
				blockPos2,
				normalTreeFeatureConfig.trunkTopOffsetRandom + random.nextInt(normalTreeFeatureConfig.field_21265 + 1),
				set,
				blockBox,
				normalTreeFeatureConfig
			);
			return true;
		}
	}
}
