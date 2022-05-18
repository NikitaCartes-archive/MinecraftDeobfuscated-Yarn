package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class BendingTrunkPlacer extends TrunkPlacer {
	public static final Codec<BendingTrunkPlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillTrunkPlacerFields(instance)
				.<Integer, IntProvider>and(
					instance.group(
						Codecs.POSITIVE_INT.optionalFieldOf("min_height_for_leaves", 1).forGetter(placer -> placer.minHeightForLeaves),
						IntProvider.createValidatingCodec(1, 64).fieldOf("bend_length").forGetter(placer -> placer.bendLength)
					)
				)
				.apply(instance, BendingTrunkPlacer::new)
	);
	private final int minHeightForLeaves;
	private final IntProvider bendLength;

	public BendingTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, int minHeightForLeaves, IntProvider bendLength) {
		super(baseHeight, firstRandomHeight, secondRandomHeight);
		this.minHeightForLeaves = minHeightForLeaves;
		this.bendLength = bendLength;
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TrunkPlacerType.BENDING_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config
	) {
		Direction direction = Direction.Type.HORIZONTAL.random(random);
		int i = height - 1;
		BlockPos.Mutable mutable = startPos.mutableCopy();
		BlockPos blockPos = mutable.down();
		setToDirt(world, replacer, random, blockPos, config);
		List<FoliagePlacer.TreeNode> list = Lists.<FoliagePlacer.TreeNode>newArrayList();

		for (int j = 0; j <= i; j++) {
			if (j + 1 >= i + random.nextInt(2)) {
				mutable.move(direction);
			}

			if (TreeFeature.canReplace(world, mutable)) {
				this.getAndSetState(world, replacer, random, mutable, config);
			}

			if (j >= this.minHeightForLeaves) {
				list.add(new FoliagePlacer.TreeNode(mutable.toImmutable(), 0, false));
			}

			mutable.move(Direction.UP);
		}

		int j = this.bendLength.get(random);

		for (int k = 0; k <= j; k++) {
			if (TreeFeature.canReplace(world, mutable)) {
				this.getAndSetState(world, replacer, random, mutable, config);
			}

			list.add(new FoliagePlacer.TreeNode(mutable.toImmutable(), 0, false));
			mutable.move(direction);
		}

		return list;
	}
}
