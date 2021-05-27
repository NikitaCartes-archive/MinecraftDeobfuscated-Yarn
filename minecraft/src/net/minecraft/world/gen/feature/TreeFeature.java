package net.minecraft.world.gen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class TreeFeature extends Feature<TreeFeatureConfig> {
	private static final int field_31519 = 19;

	public TreeFeature(Codec<TreeFeatureConfig> codec) {
		super(codec);
	}

	public static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
		return canReplace(world, pos) || world.testBlockState(pos, state -> state.isIn(BlockTags.LOGS));
	}

	private static boolean isVine(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> state.isOf(Blocks.VINE));
	}

	private static boolean isWater(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> state.isOf(Blocks.WATER));
	}

	public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> state.isAir() || state.isIn(BlockTags.LEAVES));
	}

	private static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> {
			Material material = state.getMaterial();
			return material == Material.REPLACEABLE_PLANT;
		});
	}

	private static void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state, Block.NOTIFY_ALL | Block.FORCE_STATE);
	}

	public static boolean canReplace(TestableWorld world, BlockPos pos) {
		return isAirOrLeaves(world, pos) || isReplaceablePlant(world, pos) || isWater(world, pos);
	}

	private boolean generate(
		StructureWorldAccess world,
		Random random,
		BlockPos pos,
		BiConsumer<BlockPos, BlockState> biConsumer,
		BiConsumer<BlockPos, BlockState> biConsumer2,
		TreeFeatureConfig treeFeatureConfig
	) {
		int i = treeFeatureConfig.trunkPlacer.getHeight(random);
		int j = treeFeatureConfig.foliagePlacer.getRandomHeight(random, i, treeFeatureConfig);
		int k = i - j;
		int l = treeFeatureConfig.foliagePlacer.getRandomRadius(random, k);
		if (pos.getY() < world.getBottomY() + 1 || pos.getY() + i + 1 > world.getTopY()) {
			return false;
		} else if (!treeFeatureConfig.field_33933.getBlockState(random, pos).canPlaceAt(world, pos)) {
			return false;
		} else {
			OptionalInt optionalInt = treeFeatureConfig.minimumSize.getMinClippedHeight();
			int m = this.getTopPosition(world, i, pos, treeFeatureConfig);
			if (m >= i || optionalInt.isPresent() && m >= optionalInt.getAsInt()) {
				List<FoliagePlacer.TreeNode> list = treeFeatureConfig.trunkPlacer.generate(world, biConsumer, random, m, pos, treeFeatureConfig);
				list.forEach(treeNode -> treeFeatureConfig.foliagePlacer.generate(world, biConsumer2, random, treeFeatureConfig, m, treeNode, j, l));
				return true;
			} else {
				return false;
			}
		}
	}

	private int getTopPosition(TestableWorld world, int height, BlockPos pos, TreeFeatureConfig config) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i <= height + 1; i++) {
			int j = config.minimumSize.getRadius(height, i);

			for (int k = -j; k <= j; k++) {
				for (int l = -j; l <= j; l++) {
					mutable.set(pos, k, i, l);
					if (!canTreeReplace(world, mutable) || !config.ignoreVines && isVine(world, mutable)) {
						return i - 2;
					}
				}
			}
		}

		return height;
	}

	@Override
	protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
		setBlockStateWithoutUpdatingNeighbors(world, pos, state);
	}

	@Override
	public final boolean generate(FeatureContext<TreeFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		TreeFeatureConfig treeFeatureConfig = context.getConfig();
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		Set<BlockPos> set2 = Sets.<BlockPos>newHashSet();
		Set<BlockPos> set3 = Sets.<BlockPos>newHashSet();
		BiConsumer<BlockPos, BlockState> biConsumer = (blockPosx, blockState) -> {
			set.add(blockPosx.toImmutable());
			structureWorldAccess.setBlockState(blockPosx, blockState, Block.NOTIFY_ALL | Block.FORCE_STATE);
		};
		BiConsumer<BlockPos, BlockState> biConsumer2 = (blockPosx, blockState) -> {
			set2.add(blockPosx.toImmutable());
			structureWorldAccess.setBlockState(blockPosx, blockState, Block.NOTIFY_ALL | Block.FORCE_STATE);
		};
		BiConsumer<BlockPos, BlockState> biConsumer3 = (blockPosx, blockState) -> {
			set3.add(blockPosx.toImmutable());
			structureWorldAccess.setBlockState(blockPosx, blockState, Block.NOTIFY_ALL | Block.FORCE_STATE);
		};
		boolean bl = this.generate(structureWorldAccess, random, blockPos, biConsumer, biConsumer2, treeFeatureConfig);
		if (bl && (!set.isEmpty() || !set2.isEmpty())) {
			if (!treeFeatureConfig.decorators.isEmpty()) {
				List<BlockPos> list = Lists.<BlockPos>newArrayList(set);
				List<BlockPos> list2 = Lists.<BlockPos>newArrayList(set2);
				list.sort(Comparator.comparingInt(Vec3i::getY));
				list2.sort(Comparator.comparingInt(Vec3i::getY));
				treeFeatureConfig.decorators.forEach(treeDecorator -> treeDecorator.generate(structureWorldAccess, biConsumer3, random, list, list2));
			}

			return (Boolean)BlockBox.encompassPositions(Iterables.concat(set, set2, set3)).map(blockBox -> {
				VoxelSet voxelSet = placeLogsAndLeaves(structureWorldAccess, blockBox, set, set3);
				Structure.updateCorner(structureWorldAccess, Block.NOTIFY_ALL, voxelSet, blockBox.getMinX(), blockBox.getMinY(), blockBox.getMinZ());
				return true;
			}).orElse(false);
		} else {
			return false;
		}
	}

	private static VoxelSet placeLogsAndLeaves(WorldAccess worldAccess, BlockBox blockBox, Set<BlockPos> set, Set<BlockPos> set2) {
		List<Set<BlockPos>> list = Lists.<Set<BlockPos>>newArrayList();
		VoxelSet voxelSet = new BitSetVoxelSet(blockBox.getBlockCountX(), blockBox.getBlockCountY(), blockBox.getBlockCountZ());
		int i = 6;

		for (int j = 0; j < 6; j++) {
			list.add(Sets.newHashSet());
		}

		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (BlockPos blockPos : Lists.newArrayList(set2)) {
			if (blockBox.contains(blockPos)) {
				voxelSet.set(blockPos.getX() - blockBox.getMinX(), blockPos.getY() - blockBox.getMinY(), blockPos.getZ() - blockBox.getMinZ());
			}
		}

		for (BlockPos blockPosx : Lists.newArrayList(set)) {
			if (blockBox.contains(blockPosx)) {
				voxelSet.set(blockPosx.getX() - blockBox.getMinX(), blockPosx.getY() - blockBox.getMinY(), blockPosx.getZ() - blockBox.getMinZ());
			}

			for (Direction direction : Direction.values()) {
				mutable.set(blockPosx, direction);
				if (!set.contains(mutable)) {
					BlockState blockState = worldAccess.getBlockState(mutable);
					if (blockState.contains(Properties.DISTANCE_1_7)) {
						((Set)list.get(0)).add(mutable.toImmutable());
						setBlockStateWithoutUpdatingNeighbors(worldAccess, mutable, blockState.with(Properties.DISTANCE_1_7, Integer.valueOf(1)));
						if (blockBox.contains(mutable)) {
							voxelSet.set(mutable.getX() - blockBox.getMinX(), mutable.getY() - blockBox.getMinY(), mutable.getZ() - blockBox.getMinZ());
						}
					}
				}
			}
		}

		for (int k = 1; k < 6; k++) {
			Set<BlockPos> set3 = (Set<BlockPos>)list.get(k - 1);
			Set<BlockPos> set4 = (Set<BlockPos>)list.get(k);

			for (BlockPos blockPos2 : set3) {
				if (blockBox.contains(blockPos2)) {
					voxelSet.set(blockPos2.getX() - blockBox.getMinX(), blockPos2.getY() - blockBox.getMinY(), blockPos2.getZ() - blockBox.getMinZ());
				}

				for (Direction direction2 : Direction.values()) {
					mutable.set(blockPos2, direction2);
					if (!set3.contains(mutable) && !set4.contains(mutable)) {
						BlockState blockState2 = worldAccess.getBlockState(mutable);
						if (blockState2.contains(Properties.DISTANCE_1_7)) {
							int l = (Integer)blockState2.get(Properties.DISTANCE_1_7);
							if (l > k + 1) {
								BlockState blockState3 = blockState2.with(Properties.DISTANCE_1_7, Integer.valueOf(k + 1));
								setBlockStateWithoutUpdatingNeighbors(worldAccess, mutable, blockState3);
								if (blockBox.contains(mutable)) {
									voxelSet.set(mutable.getX() - blockBox.getMinX(), mutable.getY() - blockBox.getMinY(), mutable.getZ() - blockBox.getMinZ());
								}

								set4.add(mutable.toImmutable());
							}
						}
					}
				}
			}
		}

		return voxelSet;
	}
}
