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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class AbstractTreeFeature<T extends FeatureConfig> extends Feature<T> {
	public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> configFactory, boolean emitNeighborBlockUpdates) {
		super(configFactory, emitNeighborBlockUpdates);
	}

	protected static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
		return world.testBlockState(
			pos,
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

	protected static boolean isAir(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, BlockState::isAir);
	}

	protected static boolean isNaturalDirt(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> Block.isNaturalDirt(blockState.getBlock()));
	}

	protected static boolean isWater(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> blockState.getBlock() == Blocks.WATER);
	}

	protected static boolean isLeaves(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> blockState.matches(BlockTags.LEAVES));
	}

	protected static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> blockState.isAir() || blockState.matches(BlockTags.LEAVES));
	}

	protected static boolean isNaturalDirtOrGrass(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> {
			Block block = blockState.getBlock();
			return Block.isNaturalDirt(block) || block == Blocks.GRASS_BLOCK;
		});
	}

	protected static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, blockState -> {
			Block block = blockState.getBlock();
			return Block.isNaturalDirt(block) || block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND;
		});
	}

	protected static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
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

	@Override
	protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
		this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
	}

	protected final void setBlockState(Set<BlockPos> logPositions, ModifiableWorld world, BlockPos pos, BlockState state, BlockBox blockBox) {
		this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
		blockBox.encompass(new BlockBox(pos, pos));
		if (BlockTags.LOGS.contains(state.getBlock())) {
			logPositions.add(pos.toImmutable());
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
	public final boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, T config) {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		BlockBox blockBox = BlockBox.empty();
		boolean bl = this.generate(set, world, random, pos, blockBox);
		if (blockBox.minX > blockBox.maxX) {
			return false;
		} else {
			List<Set<BlockPos>> list = Lists.<Set<BlockPos>>newArrayList();
			int i = 6;

			for (int j = 0; j < 6; j++) {
				list.add(Sets.newHashSet());
			}

			VoxelSet voxelSet = new BitSetVoxelSet(blockBox.getBlockCountX(), blockBox.getBlockCountY(), blockBox.getBlockCountZ());

			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				if (bl && !set.isEmpty()) {
					for (BlockPos blockPos : Lists.newArrayList(set)) {
						if (blockBox.contains(blockPos)) {
							voxelSet.set(blockPos.getX() - blockBox.minX, blockPos.getY() - blockBox.minY, blockPos.getZ() - blockBox.minZ, true, true);
						}

						for (Direction direction : Direction.values()) {
							pooledMutable.set(blockPos).setOffset(direction);
							if (!set.contains(pooledMutable)) {
								BlockState blockState = world.getBlockState(pooledMutable);
								if (blockState.contains(Properties.DISTANCE_1_7)) {
									((Set)list.get(0)).add(pooledMutable.toImmutable());
									this.setBlockStateWithoutUpdatingNeighbors(world, pooledMutable, blockState.with(Properties.DISTANCE_1_7, Integer.valueOf(1)));
									if (blockBox.contains(pooledMutable)) {
										voxelSet.set(pooledMutable.getX() - blockBox.minX, pooledMutable.getY() - blockBox.minY, pooledMutable.getZ() - blockBox.minZ, true, true);
									}
								}
							}
						}
					}
				}

				for (int k = 1; k < 6; k++) {
					Set<BlockPos> set2 = (Set<BlockPos>)list.get(k - 1);
					Set<BlockPos> set3 = (Set<BlockPos>)list.get(k);

					for (BlockPos blockPos2 : set2) {
						if (blockBox.contains(blockPos2)) {
							voxelSet.set(blockPos2.getX() - blockBox.minX, blockPos2.getY() - blockBox.minY, blockPos2.getZ() - blockBox.minZ, true, true);
						}

						for (Direction direction2 : Direction.values()) {
							pooledMutable.set(blockPos2).setOffset(direction2);
							if (!set2.contains(pooledMutable) && !set3.contains(pooledMutable)) {
								BlockState blockState2 = world.getBlockState(pooledMutable);
								if (blockState2.contains(Properties.DISTANCE_1_7)) {
									int l = (Integer)blockState2.get(Properties.DISTANCE_1_7);
									if (l > k + 1) {
										BlockState blockState3 = blockState2.with(Properties.DISTANCE_1_7, Integer.valueOf(k + 1));
										this.setBlockStateWithoutUpdatingNeighbors(world, pooledMutable, blockState3);
										if (blockBox.contains(pooledMutable)) {
											voxelSet.set(pooledMutable.getX() - blockBox.minX, pooledMutable.getY() - blockBox.minY, pooledMutable.getZ() - blockBox.minZ, true, true);
										}

										set3.add(pooledMutable.toImmutable());
									}
								}
							}
						}
					}
				}
			}

			Structure.method_20532(world, 3, voxelSet, blockBox.minX, blockBox.minY, blockBox.minZ);
			return bl;
		}
	}

	protected abstract boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox);
}
