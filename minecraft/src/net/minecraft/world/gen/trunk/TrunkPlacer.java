package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public abstract class TrunkPlacer {
	private final int baseHeight;
	private final int firstRandomHeight;
	private final int secondRandomHeight;
	protected final TrunkPlacerType<?> type;

	public TrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, TrunkPlacerType<?> type) {
		this.baseHeight = baseHeight;
		this.firstRandomHeight = firstRandomHeight;
		this.secondRandomHeight = secondRandomHeight;
		this.type = type;
	}

	/**
	 * Generates the trunk blocks and return a list of tree nodes to place foliage around
	 */
	public abstract List<FoliagePlacer.TreeNode> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig
	);

	public int getHeight(Random random) {
		return this.baseHeight + random.nextInt(this.firstRandomHeight + 1) + random.nextInt(this.secondRandomHeight + 1);
	}

	protected static void method_27404(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState, BlockBox blockBox) {
		AbstractTreeFeature.setBlockStateWithoutUpdatingNeighbors(modifiableWorld, blockPos, blockState);
		blockBox.encompass(new BlockBox(blockPos, blockPos));
	}

	private static boolean method_27403(TestableWorld testableWorld, BlockPos blockPos) {
		return testableWorld.testBlockState(blockPos, blockState -> {
			Block block = blockState.getBlock();
			return Feature.isDirt(block) && !blockState.isOf(Blocks.GRASS_BLOCK) && !blockState.isOf(Blocks.MYCELIUM);
		});
	}

	protected static void method_27400(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		if (!method_27403(modifiableTestableWorld, blockPos)) {
			AbstractTreeFeature.setBlockStateWithoutUpdatingNeighbors(modifiableTestableWorld, blockPos, Blocks.DIRT.getDefaultState());
		}
	}

	protected static boolean method_27402(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig
	) {
		if (AbstractTreeFeature.canReplace(modifiableTestableWorld, blockPos)) {
			method_27404(modifiableTestableWorld, blockPos, treeFeatureConfig.trunkProvider.getBlockState(random, blockPos), blockBox);
			set.add(blockPos.toImmutable());
			return true;
		} else {
			return false;
		}
	}

	protected static void method_27401(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos.Mutable mutable,
		Set<BlockPos> set,
		BlockBox blockBox,
		TreeFeatureConfig treeFeatureConfig
	) {
		if (AbstractTreeFeature.canTreeReplace(modifiableTestableWorld, mutable)) {
			method_27402(modifiableTestableWorld, random, mutable, set, blockBox, treeFeatureConfig);
		}
	}

	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("type"), ops.createString(Registry.TRUNK_PLACER_TYPE.getId(this.type).toString()))
			.put(ops.createString("base_height"), ops.createInt(this.baseHeight))
			.put(ops.createString("height_rand_a"), ops.createInt(this.firstRandomHeight))
			.put(ops.createString("height_rand_b"), ops.createInt(this.secondRandomHeight));
		return new Dynamic<>(ops, ops.createMap(builder.build())).getValue();
	}
}
