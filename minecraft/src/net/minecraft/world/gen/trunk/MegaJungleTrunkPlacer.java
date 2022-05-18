package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
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
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config
	) {
		List<FoliagePlacer.TreeNode> list = Lists.<FoliagePlacer.TreeNode>newArrayList();
		list.addAll(super.generate(world, replacer, random, height, startPos, config));

		for (int i = height - 2 - random.nextInt(4); i > height / 2; i -= 2 + random.nextInt(4)) {
			float f = random.nextFloat() * (float) (Math.PI * 2);
			int j = 0;
			int k = 0;

			for (int l = 0; l < 5; l++) {
				j = (int)(1.5F + MathHelper.cos(f) * (float)l);
				k = (int)(1.5F + MathHelper.sin(f) * (float)l);
				BlockPos blockPos = startPos.add(j, i - 3 + l / 2, k);
				this.getAndSetState(world, replacer, random, blockPos, config);
			}

			list.add(new FoliagePlacer.TreeNode(startPos.add(j, i, k), -2, false));
		}

		return list;
	}
}
