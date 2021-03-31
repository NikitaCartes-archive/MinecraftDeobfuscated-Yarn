package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class MegaJungleTrunkPlacer extends GiantTrunkPlacer {
	public static final Codec<MegaJungleTrunkPlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillTrunkPlacerFields(instance).apply(instance, MegaJungleTrunkPlacer::new)
	);

	public MegaJungleTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TrunkPlacerType.MEGA_JUNGLE_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, int i, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig
	) {
		List<FoliagePlacer.TreeNode> list = Lists.<FoliagePlacer.TreeNode>newArrayList();
		list.addAll(super.generate(testableWorld, biConsumer, random, i, blockPos, treeFeatureConfig));

		for (int j = i - 2 - random.nextInt(4); j > i / 2; j -= 2 + random.nextInt(4)) {
			float f = random.nextFloat() * (float) (Math.PI * 2);
			int k = 0;
			int l = 0;

			for (int m = 0; m < 5; m++) {
				k = (int)(1.5F + MathHelper.cos(f) * (float)m);
				l = (int)(1.5F + MathHelper.sin(f) * (float)m);
				BlockPos blockPos2 = blockPos.add(k, j - 3 + m / 2, l);
				method_35375(testableWorld, biConsumer, random, blockPos2, treeFeatureConfig);
			}

			list.add(new FoliagePlacer.TreeNode(blockPos.add(k, j, l), -2, false));
		}

		return list;
	}
}
