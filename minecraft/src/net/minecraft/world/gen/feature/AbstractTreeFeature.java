package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class AbstractTreeFeature<T extends FeatureConfig> extends Feature<T> {
	public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> function, boolean bl) {
		super(function, bl);
	}

	protected static boolean canTreeReplace(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(
			blockPos,
			blockState -> {
				Block block = blockState.getBlock();
				return blockState.isAir()
					|| blockState.matches(BlockTags.LEAVES)
					|| block == Blocks.GRASS_BLOCK
					|| Block.isNaturalDirt(block)
					|| block.matches(BlockTags.LOGS)
					|| block.matches(BlockTags.SAPLINGS)
					|| block == Blocks.VINE;
			}
		);
	}

	protected static boolean isAir(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, BlockState::isAir);
	}

	protected static boolean isNaturalDirt(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> Block.isNaturalDirt(blockState.getBlock()));
	}

	protected static boolean isWater(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> blockState.getBlock() == Blocks.WATER);
	}

	protected static boolean isLeaves(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> blockState.matches(BlockTags.LEAVES));
	}

	protected static boolean isAirOrLeaves(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> blockState.isAir() || blockState.matches(BlockTags.LEAVES));
	}

	protected static boolean isNaturalDirtOrGrass(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> {
			Block block = blockState.getBlock();
			return Block.isNaturalDirt(block) || block == Blocks.GRASS_BLOCK;
		});
	}

	protected static boolean isDirtOrGrass(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> {
			Block block = blockState.getBlock();
			return Block.isNaturalDirt(block) || block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND;
		});
	}

	protected static boolean isReplaceablePlant(TestableWorld testableWorld, BlockPos blockPos) {
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

	@Override
	protected void setBlockState(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
	}

	protected final void setBlockState(
		Set<BlockPos> set, ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState, MutableIntBoundingBox mutableIntBoundingBox
	) {
		this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
		mutableIntBoundingBox.setFrom(new MutableIntBoundingBox(blockPos, blockPos));
		if (BlockTags.LOGS.contains(blockState.getBlock())) {
			set.add(blockPos.toImmutable());
		}
	}

	private void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		if (this.emitNeighborBlockUpdates) {
			modifiableWorld.setBlockState(blockPos, blockState, 19);
		} else {
			modifiableWorld.setBlockState(blockPos, blockState, 18);
		}
	}

	@Override
	public final boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, T featureConfig) {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.empty();
		boolean bl = this.generate(set, iWorld, random, blockPos, mutableIntBoundingBox);
		if (mutableIntBoundingBox.minX > mutableIntBoundingBox.maxX) {
			return false;
		} else {
			List<Set<BlockPos>> list = Lists.<Set<BlockPos>>newArrayList();
			int i = 6;

			for (int j = 0; j < 6; j++) {
				list.add(Sets.newHashSet());
			}

			VoxelSet voxelSet = new BitSetVoxelSet(
				mutableIntBoundingBox.getBlockCountX(), mutableIntBoundingBox.getBlockCountY(), mutableIntBoundingBox.getBlockCountZ()
			);

			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				if (bl && !set.isEmpty()) {
					for (BlockPos blockPos2 : Lists.newArrayList(set)) {
						if (mutableIntBoundingBox.contains(blockPos2)) {
							voxelSet.set(
								blockPos2.getX() - mutableIntBoundingBox.minX, blockPos2.getY() - mutableIntBoundingBox.minY, blockPos2.getZ() - mutableIntBoundingBox.minZ, true, true
							);
						}

						for (Direction direction : Direction.values()) {
							pooledMutable.method_10114(blockPos2).method_10118(direction);
							if (!set.contains(pooledMutable)) {
								BlockState blockState = iWorld.getBlockState(pooledMutable);
								if (blockState.contains(Properties.DISTANCE_1_7)) {
									((Set)list.get(0)).add(pooledMutable.toImmutable());
									this.setBlockStateWithoutUpdatingNeighbors(iWorld, pooledMutable, blockState.with(Properties.DISTANCE_1_7, Integer.valueOf(1)));
									if (mutableIntBoundingBox.contains(pooledMutable)) {
										voxelSet.set(
											pooledMutable.getX() - mutableIntBoundingBox.minX,
											pooledMutable.getY() - mutableIntBoundingBox.minY,
											pooledMutable.getZ() - mutableIntBoundingBox.minZ,
											true,
											true
										);
									}
								}
							}
						}
					}
				}

				for (int k = 1; k < 6; k++) {
					Set<BlockPos> set2 = (Set<BlockPos>)list.get(k - 1);
					Set<BlockPos> set3 = (Set<BlockPos>)list.get(k);

					for (BlockPos blockPos3 : set2) {
						if (mutableIntBoundingBox.contains(blockPos3)) {
							voxelSet.set(
								blockPos3.getX() - mutableIntBoundingBox.minX, blockPos3.getY() - mutableIntBoundingBox.minY, blockPos3.getZ() - mutableIntBoundingBox.minZ, true, true
							);
						}

						for (Direction direction2 : Direction.values()) {
							pooledMutable.method_10114(blockPos3).method_10118(direction2);
							if (!set2.contains(pooledMutable) && !set3.contains(pooledMutable)) {
								BlockState blockState2 = iWorld.getBlockState(pooledMutable);
								if (blockState2.contains(Properties.DISTANCE_1_7)) {
									int l = (Integer)blockState2.get(Properties.DISTANCE_1_7);
									if (l > k + 1) {
										BlockState blockState3 = blockState2.with(Properties.DISTANCE_1_7, Integer.valueOf(k + 1));
										this.setBlockStateWithoutUpdatingNeighbors(iWorld, pooledMutable, blockState3);
										if (mutableIntBoundingBox.contains(pooledMutable)) {
											voxelSet.set(
												pooledMutable.getX() - mutableIntBoundingBox.minX,
												pooledMutable.getY() - mutableIntBoundingBox.minY,
												pooledMutable.getZ() - mutableIntBoundingBox.minZ,
												true,
												true
											);
										}

										set3.add(pooledMutable.toImmutable());
									}
								}
							}
						}
					}
				}
			}

			Structure.method_20532(iWorld, 3, voxelSet, mutableIntBoundingBox.minX, mutableIntBoundingBox.minY, mutableIntBoundingBox.minZ);
			return bl;
		}
	}

	protected abstract boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	);
}
