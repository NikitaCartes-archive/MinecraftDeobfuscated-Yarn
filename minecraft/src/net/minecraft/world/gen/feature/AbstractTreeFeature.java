package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
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
	public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> configFactory) {
		super(configFactory);
	}

	protected static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
		return world.testBlockState(
			pos,
			blockState -> {
				Block block = blockState.getBlock();
				return blockState.isAir()
					|| blockState.matches(BlockTags.LEAVES)
					|| isDirt(block)
					|| block.isIn(BlockTags.LOGS)
					|| block.isIn(BlockTags.SAPLINGS)
					|| block == Blocks.VINE;
			}
		);
	}

	public static boolean isAir(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, BlockState::isAir);
	}

	protected static boolean isNaturalDirt(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> {
			Block block = blockState.getBlock();
			return isDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM;
		});
	}

	protected static boolean isVine(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> blockState.getBlock() == Blocks.VINE);
	}

	public static boolean isWater(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> blockState.getBlock() == Blocks.WATER);
	}

	public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> blockState.isAir() || blockState.matches(BlockTags.LEAVES));
	}

	public static boolean isNaturalDirtOrGrass(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> isDirt(blockState.getBlock()));
	}

	protected static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> {
			Block block = blockState.getBlock();
			return isDirt(block) || block == Blocks.FARMLAND;
		});
	}

	public static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> {
			Material material = blockState.getMaterial();
			return material == Material.REPLACEABLE_PLANT;
		});
	}

	protected void setToDirt(ModifiableTestableWorld world, BlockPos pos) {
		if (!isNaturalDirt(world, pos)) {
			this.setBlockState(world, pos, Blocks.DIRT.getDefaultState());
		}
	}

	protected boolean setLogBlockState(
		ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> logPositions, BlockBox blockBox, TreeFeatureConfig config
	) {
		if (!isAirOrLeaves(world, pos) && !isReplaceablePlant(world, pos) && !isWater(world, pos)) {
			return false;
		} else {
			this.setBlockState(world, pos, config.trunkProvider.getBlockState(random, pos), blockBox);
			logPositions.add(pos.toImmutable());
			return true;
		}
	}

	protected boolean setLeavesBlockState(
		ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> leavesPositions, BlockBox blockBox, TreeFeatureConfig config
	) {
		if (!isAirOrLeaves(world, pos) && !isReplaceablePlant(world, pos) && !isWater(world, pos)) {
			return false;
		} else {
			this.setBlockState(world, pos, config.leavesProvider.getBlockState(random, pos), blockBox);
			leavesPositions.add(pos.toImmutable());
			return true;
		}
	}

	@Override
	protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
		this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
	}

	protected final void setBlockState(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState, BlockBox blockBox) {
		this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
		blockBox.encompass(new BlockBox(blockPos, blockPos));
	}

	private void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		modifiableWorld.setBlockState(blockPos, blockState, 19);
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
				treeFeatureConfig.decorators.forEach(treeDecorator -> treeDecorator.generate(iWorld, random, list, list2, set3, blockBox));
			}

			VoxelSet voxelSet = this.method_23380(iWorld, blockBox, set, set3);
			Structure.method_20532(iWorld, 3, voxelSet, blockBox.minX, blockBox.minY, blockBox.minZ);
			return true;
		} else {
			return false;
		}
	}

	private VoxelSet method_23380(IWorld iWorld, BlockBox blockBox, Set<BlockPos> set, Set<BlockPos> set2) {
		List<Set<BlockPos>> list = Lists.<Set<BlockPos>>newArrayList();
		VoxelSet voxelSet = new BitSetVoxelSet(blockBox.getBlockCountX(), blockBox.getBlockCountY(), blockBox.getBlockCountZ());
		int i = 6;

		for (int j = 0; j < 6; j++) {
			list.add(Sets.newHashSet());
		}

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (BlockPos blockPos : Lists.newArrayList(set2)) {
				if (blockBox.contains(blockPos)) {
					voxelSet.set(blockPos.getX() - blockBox.minX, blockPos.getY() - blockBox.minY, blockPos.getZ() - blockBox.minZ, true, true);
				}
			}

			for (BlockPos blockPosx : Lists.newArrayList(set)) {
				if (blockBox.contains(blockPosx)) {
					voxelSet.set(blockPosx.getX() - blockBox.minX, blockPosx.getY() - blockBox.minY, blockPosx.getZ() - blockBox.minZ, true, true);
				}

				for (Direction direction : Direction.values()) {
					pooledMutable.set(blockPosx).setOffset(direction);
					if (!set.contains(pooledMutable)) {
						BlockState blockState = iWorld.getBlockState(pooledMutable);
						if (blockState.contains(Properties.DISTANCE_1_7)) {
							((Set)list.get(0)).add(pooledMutable.toImmutable());
							this.setBlockStateWithoutUpdatingNeighbors(iWorld, pooledMutable, blockState.with(Properties.DISTANCE_1_7, Integer.valueOf(1)));
							if (blockBox.contains(pooledMutable)) {
								voxelSet.set(pooledMutable.getX() - blockBox.minX, pooledMutable.getY() - blockBox.minY, pooledMutable.getZ() - blockBox.minZ, true, true);
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
						voxelSet.set(blockPos2.getX() - blockBox.minX, blockPos2.getY() - blockBox.minY, blockPos2.getZ() - blockBox.minZ, true, true);
					}

					for (Direction direction2 : Direction.values()) {
						pooledMutable.set(blockPos2).setOffset(direction2);
						if (!set3.contains(pooledMutable) && !set4.contains(pooledMutable)) {
							BlockState blockState2 = iWorld.getBlockState(pooledMutable);
							if (blockState2.contains(Properties.DISTANCE_1_7)) {
								int l = (Integer)blockState2.get(Properties.DISTANCE_1_7);
								if (l > k + 1) {
									BlockState blockState3 = blockState2.with(Properties.DISTANCE_1_7, Integer.valueOf(k + 1));
									this.setBlockStateWithoutUpdatingNeighbors(iWorld, pooledMutable, blockState3);
									if (blockBox.contains(pooledMutable)) {
										voxelSet.set(pooledMutable.getX() - blockBox.minX, pooledMutable.getY() - blockBox.minY, pooledMutable.getZ() - blockBox.minZ, true, true);
									}

									set4.add(pooledMutable.toImmutable());
								}
							}
						}
					}
				}
			}
		}

		return voxelSet;
	}

	protected abstract boolean generate(
		ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, BlockBox blockBox, T config
	);
}
