package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class AbstractTreeFeature<T extends TreeFeatureConfig> extends Feature<T> {
	public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> function) {
		super(function);
	}

	protected static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
		return world.testBlockState(
			pos,
			state -> {
				Block block = state.getBlock();
				return state.isAir()
					|| state.isIn(BlockTags.LEAVES)
					|| isDirt(block)
					|| block.isIn(BlockTags.LOGS)
					|| block.isIn(BlockTags.SAPLINGS)
					|| block == Blocks.VINE;
			}
		);
	}

	public static boolean isAir(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, AbstractBlock.AbstractBlockState::isAir);
	}

	protected static boolean isNaturalDirt(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> {
			Block block = state.getBlock();
			return isDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM;
		});
	}

	protected static boolean isVine(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> state.getBlock() == Blocks.VINE);
	}

	public static boolean isWater(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> state.getBlock() == Blocks.WATER);
	}

	public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> state.isAir() || state.isIn(BlockTags.LEAVES));
	}

	public static boolean isNaturalDirtOrGrass(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> isDirt(state.getBlock()));
	}

	protected static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> {
			Block block = state.getBlock();
			return isDirt(block) || block == Blocks.FARMLAND;
		});
	}

	public static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> {
			Material material = state.getMaterial();
			return material == Material.REPLACEABLE_PLANT;
		});
	}

	protected void setToDirt(ModifiableTestableWorld world, BlockPos pos) {
		if (!isNaturalDirt(world, pos)) {
			this.setBlockState(world, pos, Blocks.DIRT.getDefaultState());
		}
	}

	protected boolean setLogBlockState(
		ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> logPositions, BlockBox box, TreeFeatureConfig config
	) {
		if (!isAirOrLeaves(world, pos) && !isReplaceablePlant(world, pos) && !isWater(world, pos)) {
			return false;
		} else {
			this.setBlockState(world, pos, config.trunkProvider.getBlockState(random, pos), box);
			logPositions.add(pos.toImmutable());
			return true;
		}
	}

	protected boolean setLeavesBlockState(
		ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> leavesPositions, BlockBox box, TreeFeatureConfig config
	) {
		if (!isAirOrLeaves(world, pos) && !isReplaceablePlant(world, pos) && !isWater(world, pos)) {
			return false;
		} else {
			this.setBlockState(world, pos, config.leavesProvider.getBlockState(random, pos), box);
			leavesPositions.add(pos.toImmutable());
			return true;
		}
	}

	@Override
	protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
		this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
	}

	protected final void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state, BlockBox box) {
		this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
		box.encompass(new BlockBox(pos, pos));
	}

	private void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state, 19);
	}

	public final boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, T treeFeatureConfig
	) {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		Set<BlockPos> set2 = Sets.<BlockPos>newHashSet();
		Set<BlockPos> set3 = Sets.<BlockPos>newHashSet();
		BlockBox blockBox = BlockBox.empty();
		boolean bl = this.generate(iWorld, random, blockPos, set, set2, blockBox, treeFeatureConfig);
		if (blockBox.minX <= blockBox.maxX && bl && !set.isEmpty()) {
			if (!treeFeatureConfig.decorators.isEmpty()) {
				List<BlockPos> list = Lists.<BlockPos>newArrayList(set);
				List<BlockPos> list2 = Lists.<BlockPos>newArrayList(set2);
				list.sort(Comparator.comparingInt(Vec3i::getY));
				list2.sort(Comparator.comparingInt(Vec3i::getY));
				treeFeatureConfig.decorators.forEach(decorator -> decorator.generate(iWorld, random, list, list2, set3, blockBox));
			}

			VoxelSet voxelSet = this.placeLogsAndLeaves(iWorld, blockBox, set, set3);
			Structure.updateCorner(iWorld, 3, voxelSet, blockBox.minX, blockBox.minY, blockBox.minZ);
			return true;
		} else {
			return false;
		}
	}

	private VoxelSet placeLogsAndLeaves(IWorld world, BlockBox box, Set<BlockPos> logs, Set<BlockPos> leaves) {
		List<Set<BlockPos>> list = Lists.<Set<BlockPos>>newArrayList();
		VoxelSet voxelSet = new BitSetVoxelSet(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ());
		int i = 6;

		for (int j = 0; j < 6; j++) {
			list.add(Sets.newHashSet());
		}

		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (BlockPos blockPos : Lists.newArrayList(leaves)) {
			if (box.contains(blockPos)) {
				voxelSet.set(blockPos.getX() - box.minX, blockPos.getY() - box.minY, blockPos.getZ() - box.minZ, true, true);
			}
		}

		for (BlockPos blockPosx : Lists.newArrayList(logs)) {
			if (box.contains(blockPosx)) {
				voxelSet.set(blockPosx.getX() - box.minX, blockPosx.getY() - box.minY, blockPosx.getZ() - box.minZ, true, true);
			}

			for (Direction direction : Direction.values()) {
				mutable.set(blockPosx, direction);
				if (!logs.contains(mutable)) {
					BlockState blockState = world.getBlockState(mutable);
					if (blockState.contains(Properties.DISTANCE_1_7)) {
						((Set)list.get(0)).add(mutable.toImmutable());
						this.setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState.with(Properties.DISTANCE_1_7, Integer.valueOf(1)));
						if (box.contains(mutable)) {
							voxelSet.set(mutable.getX() - box.minX, mutable.getY() - box.minY, mutable.getZ() - box.minZ, true, true);
						}
					}
				}
			}
		}

		for (int k = 1; k < 6; k++) {
			Set<BlockPos> set = (Set<BlockPos>)list.get(k - 1);
			Set<BlockPos> set2 = (Set<BlockPos>)list.get(k);

			for (BlockPos blockPos2 : set) {
				if (box.contains(blockPos2)) {
					voxelSet.set(blockPos2.getX() - box.minX, blockPos2.getY() - box.minY, blockPos2.getZ() - box.minZ, true, true);
				}

				for (Direction direction2 : Direction.values()) {
					mutable.set(blockPos2, direction2);
					if (!set.contains(mutable) && !set2.contains(mutable)) {
						BlockState blockState2 = world.getBlockState(mutable);
						if (blockState2.contains(Properties.DISTANCE_1_7)) {
							int l = (Integer)blockState2.get(Properties.DISTANCE_1_7);
							if (l > k + 1) {
								BlockState blockState3 = blockState2.with(Properties.DISTANCE_1_7, Integer.valueOf(k + 1));
								this.setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState3);
								if (box.contains(mutable)) {
									voxelSet.set(mutable.getX() - box.minX, mutable.getY() - box.minY, mutable.getZ() - box.minZ, true, true);
								}

								set2.add(mutable.toImmutable());
							}
						}
					}
				}
			}
		}

		return voxelSet;
	}

	protected abstract boolean generate(
		ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, BlockBox box, T config
	);
}
