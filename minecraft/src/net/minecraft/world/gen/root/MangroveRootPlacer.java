package net.minecraft.world.gen.root;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class MangroveRootPlacer extends RootPlacer {
	public static final int field_38769 = 8;
	public static final int field_38770 = 15;
	public static final Codec<MangroveRootPlacer> CODEC = RecordCodecBuilder.create(
		instance -> method_43182(instance)
				.and(MangroveRootPlacement.CODEC.fieldOf("mangrove_root_placement").forGetter(rootPlacer -> rootPlacer.mangroveRootPlacement))
				.apply(instance, MangroveRootPlacer::new)
	);
	private final MangroveRootPlacement mangroveRootPlacement;

	public MangroveRootPlacer(
		IntProvider trunkOffsetY, BlockStateProvider rootProvider, Optional<AboveRootPlacement> aboveRootPlacement, MangroveRootPlacement mangroveRootPlacement
	) {
		super(trunkOffsetY, rootProvider, aboveRootPlacement);
		this.mangroveRootPlacement = mangroveRootPlacement;
	}

	@Override
	public boolean generate(
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, BlockPos trunkPos, TreeFeatureConfig config
	) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		BlockPos.Mutable mutable = pos.mutableCopy();

		while (mutable.getY() < trunkPos.getY()) {
			if (!this.canGrowThrough(world, mutable)) {
				return false;
			}

			mutable.move(Direction.UP);
		}

		list.add(trunkPos.down());

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = trunkPos.offset(direction);
			List<BlockPos> list2 = Lists.<BlockPos>newArrayList();
			if (!this.canGrow(world, random, blockPos, direction, trunkPos, list2, 0)) {
				return false;
			}

			list.addAll(list2);
			list.add(trunkPos.offset(direction));
		}

		for (BlockPos blockPos2 : list) {
			this.placeRoots(world, replacer, random, blockPos2, config);
		}

		return true;
	}

	private boolean canGrow(
		TestableWorld world, Random random, BlockPos pos, Direction direction, BlockPos origin, List<BlockPos> offshootPositions, int rootLength
	) {
		int i = this.mangroveRootPlacement.maxRootLength();
		if (rootLength != i && offshootPositions.size() <= i) {
			for (BlockPos blockPos : this.getOffshootPositions(pos, direction, random, origin)) {
				if (this.canGrowThrough(world, blockPos)) {
					offshootPositions.add(blockPos);
					if (!this.canGrow(world, random, blockPos, direction, origin, offshootPositions, rootLength + 1)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	protected List<BlockPos> getOffshootPositions(BlockPos pos, Direction direction, Random random, BlockPos origin) {
		BlockPos blockPos = pos.down();
		BlockPos blockPos2 = pos.offset(direction);
		int i = pos.getManhattanDistance(origin);
		int j = this.mangroveRootPlacement.maxRootWidth();
		float f = this.mangroveRootPlacement.randomSkewChance();
		if (i > j - 3 && i <= j) {
			return random.nextFloat() < f ? List.of(blockPos, blockPos2.down()) : List.of(blockPos);
		} else if (i > j) {
			return List.of(blockPos);
		} else if (random.nextFloat() < f) {
			return List.of(blockPos);
		} else {
			return random.nextBoolean() ? List.of(blockPos2) : List.of(blockPos);
		}
	}

	@Override
	protected boolean canGrowThrough(TestableWorld world, BlockPos pos) {
		return super.canGrowThrough(world, pos) || world.testBlockState(pos, state -> state.isIn(this.mangroveRootPlacement.canGrowThrough()));
	}

	@Override
	protected void placeRoots(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config) {
		if (world.testBlockState(pos, state -> state.isIn(this.mangroveRootPlacement.muddyRootsIn()))) {
			BlockState blockState = this.mangroveRootPlacement.muddyRootsProvider().get(random, pos);
			replacer.accept(pos, this.applyWaterlogging(world, pos, blockState));
		} else {
			super.placeRoots(world, replacer, random, pos, config);
		}
	}

	@Override
	protected RootPlacerType<?> getType() {
		return RootPlacerType.MANGROVE_ROOT_PLACER;
	}
}
