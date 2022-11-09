package net.minecraft.world.gen.trunk;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public abstract class TrunkPlacer {
	public static final Codec<TrunkPlacer> TYPE_CODEC = Registries.TRUNK_PLACER_TYPE.getCodec().dispatch(TrunkPlacer::getType, TrunkPlacerType::getCodec);
	private static final int MAX_BASE_HEIGHT = 32;
	private static final int MAX_RANDOM_HEIGHT = 24;
	public static final int field_31530 = 80;
	protected final int baseHeight;
	protected final int firstRandomHeight;
	protected final int secondRandomHeight;

	protected static <P extends TrunkPlacer> P3<Mu<P>, Integer, Integer, Integer> fillTrunkPlacerFields(Instance<P> instance) {
		return instance.group(
			Codec.intRange(0, 32).fieldOf("base_height").forGetter(placer -> placer.baseHeight),
			Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter(placer -> placer.firstRandomHeight),
			Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter(placer -> placer.secondRandomHeight)
		);
	}

	public TrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
		this.baseHeight = baseHeight;
		this.firstRandomHeight = firstRandomHeight;
		this.secondRandomHeight = secondRandomHeight;
	}

	protected abstract TrunkPlacerType<?> getType();

	/**
	 * Generates the trunk blocks and return a list of tree nodes to place foliage around
	 */
	public abstract List<FoliagePlacer.TreeNode> generate(
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config
	);

	public int getHeight(Random random) {
		return this.baseHeight + random.nextInt(this.firstRandomHeight + 1) + random.nextInt(this.secondRandomHeight + 1);
	}

	private static boolean canGenerate(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> Feature.isSoil(state) && !state.isOf(Blocks.GRASS_BLOCK) && !state.isOf(Blocks.MYCELIUM));
	}

	protected static void setToDirt(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config) {
		if (config.forceDirt || !canGenerate(world, pos)) {
			replacer.accept(pos, config.dirtProvider.get(random, pos));
		}
	}

	protected boolean getAndSetState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config) {
		return this.getAndSetState(world, replacer, random, pos, config, Function.identity());
	}

	protected boolean getAndSetState(
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		Random random,
		BlockPos pos,
		TreeFeatureConfig config,
		Function<BlockState, BlockState> function
	) {
		if (this.canReplace(world, pos)) {
			replacer.accept(pos, (BlockState)function.apply(config.trunkProvider.get(random, pos)));
			return true;
		} else {
			return false;
		}
	}

	protected void trySetState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos.Mutable pos, TreeFeatureConfig config) {
		if (this.canReplaceOrIsLog(world, pos)) {
			this.getAndSetState(world, replacer, random, pos, config);
		}
	}

	protected boolean canReplace(TestableWorld world, BlockPos pos) {
		return TreeFeature.canReplace(world, pos);
	}

	public boolean canReplaceOrIsLog(TestableWorld world, BlockPos pos) {
		return this.canReplace(world, pos) || world.testBlockState(pos, state -> state.isIn(BlockTags.LOGS));
	}
}
