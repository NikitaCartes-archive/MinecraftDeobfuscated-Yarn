package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_3746;
import net.minecraft.class_3747;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.FeatureConfig;

public abstract class TreeFeature<T extends FeatureConfig> extends Feature<T> {
	public TreeFeature(Function<Dynamic<?>, ? extends T> function, boolean bl) {
		super(function, bl);
	}

	protected static boolean method_16432(class_3746 arg, BlockPos blockPos) {
		return arg.method_16358(
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

	protected static boolean method_16424(class_3746 arg, BlockPos blockPos) {
		return arg.method_16358(blockPos, BlockState::isAir);
	}

	protected static boolean method_16419(class_3746 arg, BlockPos blockPos) {
		return arg.method_16358(blockPos, blockState -> Block.isNaturalDirt(blockState.getBlock()));
	}

	protected static boolean method_16422(class_3746 arg, BlockPos blockPos) {
		return arg.method_16358(blockPos, blockState -> blockState.getBlock() == Blocks.field_10382);
	}

	protected static boolean method_16416(class_3746 arg, BlockPos blockPos) {
		return arg.method_16358(blockPos, blockState -> blockState.matches(BlockTags.field_15503));
	}

	protected static boolean method_16420(class_3746 arg, BlockPos blockPos) {
		return arg.method_16358(blockPos, blockState -> blockState.isAir() || blockState.matches(BlockTags.field_15503));
	}

	protected static boolean method_16430(class_3746 arg, BlockPos blockPos) {
		return arg.method_16358(blockPos, blockState -> {
			Block block = blockState.getBlock();
			return Block.isNaturalDirt(block) || block == Blocks.field_10219;
		});
	}

	protected static boolean method_16433(class_3746 arg, BlockPos blockPos) {
		return arg.method_16358(blockPos, blockState -> {
			Block block = blockState.getBlock();
			return Block.isNaturalDirt(block) || block == Blocks.field_10219 || block == Blocks.field_10362;
		});
	}

	protected static boolean method_16425(class_3746 arg, BlockPos blockPos) {
		return arg.method_16358(blockPos, blockState -> {
			Material material = blockState.getMaterial();
			return material == Material.REPLACEABLE_PLANT;
		});
	}

	protected void method_16427(class_3747 arg, BlockPos blockPos) {
		if (!method_16419(arg, blockPos)) {
			this.method_13153(arg, blockPos, Blocks.field_10566.getDefaultState());
		}
	}

	@Override
	protected void method_13153(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		this.method_12774(modifiableWorld, blockPos, blockState);
	}

	protected final void method_12773(Set<BlockPos> set, ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		this.method_12774(modifiableWorld, blockPos, blockState);
		if (BlockTags.field_15475.contains(blockState.getBlock())) {
			set.add(blockPos.toImmutable());
		}
	}

	private void method_12774(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		if (this.emitNeighborBlockUpdates) {
			modifiableWorld.setBlockState(blockPos, blockState, 19);
		} else {
			modifiableWorld.setBlockState(blockPos, blockState, 18);
		}
	}

	@Override
	public final boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, Random random, BlockPos blockPos, T featureConfig
	) {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		boolean bl = this.method_12775(set, iWorld, random, blockPos);
		List<Set<BlockPos>> list = Lists.<Set<BlockPos>>newArrayList();
		int i = 6;

		for (int j = 0; j < 6; j++) {
			list.add(Sets.newHashSet());
		}

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			if (bl && !set.isEmpty()) {
				for (BlockPos blockPos2 : Lists.newArrayList(set)) {
					for (Direction direction : Direction.values()) {
						pooledMutable.set(blockPos2).method_10118(direction);
						if (!set.contains(pooledMutable)) {
							BlockState blockState = iWorld.getBlockState(pooledMutable);
							if (blockState.contains(Properties.DISTANCE_1_7)) {
								((Set)list.get(0)).add(pooledMutable.toImmutable());
								this.method_12774(iWorld, pooledMutable, blockState.with(Properties.DISTANCE_1_7, Integer.valueOf(1)));
							}
						}
					}
				}
			}

			for (int k = 1; k < 6; k++) {
				Set<BlockPos> set2 = (Set<BlockPos>)list.get(k - 1);
				Set<BlockPos> set3 = (Set<BlockPos>)list.get(k);

				for (BlockPos blockPos3 : set2) {
					for (Direction direction2 : Direction.values()) {
						pooledMutable.set(blockPos3).method_10118(direction2);
						if (!set2.contains(pooledMutable) && !set3.contains(pooledMutable)) {
							BlockState blockState2 = iWorld.getBlockState(pooledMutable);
							if (blockState2.contains(Properties.DISTANCE_1_7)) {
								int l = (Integer)blockState2.get(Properties.DISTANCE_1_7);
								if (l > k + 1) {
									BlockState blockState3 = blockState2.with(Properties.DISTANCE_1_7, Integer.valueOf(k + 1));
									this.method_12774(iWorld, pooledMutable, blockState3);
									set3.add(pooledMutable.toImmutable());
								}
							}
						}
					}
				}
			}
		}

		return bl;
	}

	protected abstract boolean method_12775(Set<BlockPos> set, class_3747 arg, Random random, BlockPos blockPos);
}
