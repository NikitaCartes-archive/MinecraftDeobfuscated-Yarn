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
					|| blockState.matches(BlockTags.field_15503)
					|| block == Blocks.field_10219
					|| Block.isNaturalDirt(block)
					|| block.matches(BlockTags.field_15475)
					|| block.matches(BlockTags.field_15462)
					|| block == Blocks.field_10597;
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
		return testableWorld.testBlockState(blockPos, blockState -> blockState.getBlock() == Blocks.field_10382);
	}

	protected static boolean isLeaves(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> blockState.matches(BlockTags.field_15503));
	}

	protected static boolean isAirOrLeaves(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> blockState.isAir() || blockState.matches(BlockTags.field_15503));
	}

	protected static boolean isNaturalDirtOrGrass(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> {
			Block block = blockState.getBlock();
			return Block.isNaturalDirt(block) || block == Blocks.field_10219;
		});
	}

	protected static boolean isDirtOrGrass(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> {
			Block block = blockState.getBlock();
			return Block.isNaturalDirt(block) || block == Blocks.field_10219 || block == Blocks.field_10362;
		});
	}

	protected static boolean isReplaceablePlant(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> {
			Material material = blockState.method_11620();
			return material == Material.REPLACEABLE_PLANT;
		});
	}

	protected void setToDirt(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		if (!isNaturalDirt(modifiableTestableWorld, blockPos)) {
			this.setBlockState(modifiableTestableWorld, blockPos, Blocks.field_10566.method_9564());
		}
	}

	@Override
	protected void setBlockState(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
	}

	protected final void method_12773(
		Set<BlockPos> set, ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState, MutableIntBoundingBox mutableIntBoundingBox
	) {
		this.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
		mutableIntBoundingBox.setFrom(new MutableIntBoundingBox(blockPos, blockPos));
		if (BlockTags.field_15475.contains(blockState.getBlock())) {
			set.add(blockPos.toImmutable());
		}
	}

	private void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		if (this.emitNeighborBlockUpdates) {
			modifiableWorld.method_8652(blockPos, blockState, 19);
		} else {
			modifiableWorld.method_8652(blockPos, blockState, 18);
		}
	}

	@Override
	public final boolean method_13151(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, T featureConfig
	) {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.empty();
		boolean bl = this.method_12775(set, iWorld, random, blockPos, mutableIntBoundingBox);
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
								BlockState blockState = iWorld.method_8320(pooledMutable);
								if (blockState.method_11570(Properties.field_12541)) {
									((Set)list.get(0)).add(pooledMutable.toImmutable());
									this.setBlockStateWithoutUpdatingNeighbors(iWorld, pooledMutable, blockState.method_11657(Properties.field_12541, Integer.valueOf(1)));
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
								BlockState blockState2 = iWorld.method_8320(pooledMutable);
								if (blockState2.method_11570(Properties.field_12541)) {
									int l = (Integer)blockState2.method_11654(Properties.field_12541);
									if (l > k + 1) {
										BlockState blockState3 = blockState2.method_11657(Properties.field_12541, Integer.valueOf(k + 1));
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

	protected abstract boolean method_12775(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	);
}
