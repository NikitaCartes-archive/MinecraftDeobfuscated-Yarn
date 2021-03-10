package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class BendingTrunkPlacer extends TrunkPlacer {
	public static final Codec<BendingTrunkPlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillTrunkPlacerFields(instance)
				.<Integer, UniformIntDistribution>and(
					instance.group(
						Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("min_height_for_leaves", 1).forGetter(bendingTrunkPlacer -> bendingTrunkPlacer.minHeightForLeaves),
						UniformIntDistribution.createValidatedCodec(1, 32, 32).fieldOf("bend_length").forGetter(bendingTrunkPlacer -> bendingTrunkPlacer.bendLength)
					)
				)
				.apply(instance, BendingTrunkPlacer::new)
	);
	private final int minHeightForLeaves;
	private final UniformIntDistribution bendLength;

	public BendingTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, int minHeightForLeaves, UniformIntDistribution bendLength) {
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
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> placedStates, BlockBox box, TreeFeatureConfig config
	) {
		Direction direction = Direction.Type.HORIZONTAL.random(random);
		int i = trunkHeight - 1;
		BlockPos.Mutable mutable = pos.mutableCopy();
		BlockPos blockPos = mutable.down();
		setToDirt(world, random, blockPos, config);
		List<FoliagePlacer.TreeNode> list = Lists.<FoliagePlacer.TreeNode>newArrayList();

		for (int j = 0; j <= i; j++) {
			if (j + 1 >= i + random.nextInt(2)) {
				mutable.move(direction);
			}

			if (TreeFeature.canReplace(world, mutable)) {
				getAndSetState(world, random, mutable, placedStates, box, config);
			}

			if (j >= this.minHeightForLeaves) {
				list.add(new FoliagePlacer.TreeNode(mutable.toImmutable(), 0, false));
			}

			mutable.move(Direction.UP);
		}

		int j = this.bendLength.getValue(random);

		for (int k = 0; k <= j; k++) {
			if (TreeFeature.canReplace(world, mutable)) {
				getAndSetState(world, random, mutable, placedStates, box, config);
			}

			list.add(new FoliagePlacer.TreeNode(mutable.toImmutable(), 0, false));
			mutable.move(direction);
		}

		return list;
	}
}
