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

public abstract class AbstractTreeFeature<T extends AbstractTreeFeatureConfig> extends Feature<T> {
	public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> function) {
		super(function);
	}

	protected static boolean canTreeReplace(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(
			blockPos,
			blockState -> {
				Block block = blockState.getBlock();
				return blockState.isAir()
					|| blockState.matches(BlockTags.LEAVES)
					|| method_23396(block)
					|| block.matches(BlockTags.LOGS)
					|| block.matches(BlockTags.SAPLINGS)
					|| block == Blocks.VINE;
			}
		);
	}

	public static boolean isAir(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, BlockState::isAir);
	}

	protected static boolean isNaturalDirt(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> {
			Block block = blockState.getBlock();
			return method_23396(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM;
		});
	}

	protected static boolean isLeaves(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> blockState.getBlock() == Blocks.VINE);
	}

	public static boolean isWater(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> blockState.getBlock() == Blocks.WATER);
	}

	public static boolean isAirOrLeaves(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> blockState.isAir() || blockState.matches(BlockTags.LEAVES));
	}

	public static boolean isNaturalDirtOrGrass(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> method_23396(blockState.getBlock()));
	}

	protected static boolean isDirtOrGrass(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> {
			Block block = blockState.getBlock();
			return method_23396(block) || block == Blocks.FARMLAND;
		});
	}

	public static boolean isReplaceablePlant(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> {
			Material material = blockState.getMaterial();
			return material == Material.REPLACEABLE_PLANT;
		});
	}

	protected void setToDirt(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		if (!isNaturalDirt(modifiableTestableWorld, blockPos)) {
			this.setBlockState(modifiableTestableWorld, blockPos, Blocks.DIRT.getDefaultState());
		}
	}

	protected boolean method_23382(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		Set<BlockPos> set,
		BlockBox blockBox,
		AbstractTreeFeatureConfig abstractTreeFeatureConfig
	) {
		if (!isAirOrLeaves(modifiableTestableWorld, blockPos)
			&& !isReplaceablePlant(modifiableTestableWorld, blockPos)
			&& !isWater(modifiableTestableWorld, blockPos)) {
			return false;
		} else {
			this.setBlockState(modifiableTestableWorld, blockPos, abstractTreeFeatureConfig.trunkProvider.getBlockState(random, blockPos), blockBox);
			set.add(blockPos.toImmutable());
			return true;
		}
	}

	protected boolean method_23383(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		Set<BlockPos> set,
		BlockBox blockBox,
		AbstractTreeFeatureConfig abstractTreeFeatureConfig
	) {
		if (!isAirOrLeaves(modifiableTestableWorld, blockPos)
			&& !isReplaceablePlant(modifiableTestableWorld, blockPos)
			&& !isWater(modifiableTestableWorld, blockPos)) {
			return false;
		} else {
			this.setBlockState(modifiableTestableWorld, blockPos, abstractTreeFeatureConfig.leavesProvider.getBlockState(random, blockPos), blockBox);
			set.add(blockPos.toImmutable());
			return true;
		}
	}

	@Override
	protected void setBlockState(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
	}

	protected final void setBlockState(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState, BlockBox blockBox) {
		this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
		blockBox.encompass(new BlockBox(blockPos, blockPos));
	}

	private void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		modifiableWorld.setBlockState(blockPos, blockState, 19);
	}

	public final boolean method_22362(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, T abstractTreeFeatureConfig
	) {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		Set<BlockPos> set2 = Sets.<BlockPos>newHashSet();
		Set<BlockPos> set3 = Sets.<BlockPos>newHashSet();
		BlockBox blockBox = BlockBox.empty();
		boolean bl = this.generate(iWorld, random, blockPos, set, set2, blockBox, abstractTreeFeatureConfig);
		if (blockBox.minX <= blockBox.maxX && bl && !set.isEmpty()) {
			if (!abstractTreeFeatureConfig.decorators.isEmpty()) {
				List<BlockPos> list = Lists.<BlockPos>newArrayList(set);
				List<BlockPos> list2 = Lists.<BlockPos>newArrayList(set2);
				list.sort(Comparator.comparingInt(Vec3i::getY));
				list2.sort(Comparator.comparingInt(Vec3i::getY));
				abstractTreeFeatureConfig.decorators.forEach(treeDecorator -> treeDecorator.method_23469(iWorld, random, list, list2, set3, blockBox));
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
					pooledMutable.method_10114(blockPosx).method_10118(direction);
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
						pooledMutable.method_10114(blockPos2).method_10118(direction2);
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
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		Set<BlockPos> set,
		Set<BlockPos> set2,
		BlockBox blockBox,
		T abstractTreeFeatureConfig
	);
}
