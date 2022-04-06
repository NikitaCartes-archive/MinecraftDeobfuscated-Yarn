package net.minecraft.world.gen.root;

import com.mojang.datafixers.Products.P1;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public abstract class RootPlacer {
	public static final Codec<RootPlacer> TYPE_CODEC = Registry.ROOT_PLACER_TYPE.getCodec().dispatch(RootPlacer::getType, RootPlacerType::getCodec);
	protected final BlockStateProvider rootProvider;

	protected static <P extends RootPlacer> P1<Mu<P>, BlockStateProvider> method_43182(Instance<P> instance) {
		return instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("root_provider").forGetter(rootPlacer -> rootPlacer.rootProvider));
	}

	public RootPlacer(BlockStateProvider rootProvider) {
		this.rootProvider = rootProvider;
	}

	protected abstract RootPlacerType<?> getType();

	public abstract Optional<BlockPos> generate(
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, BlockPos pos, TreeFeatureConfig config
	);

	protected void placeRoots(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, BlockPos pos, TreeFeatureConfig config) {
		replacer.accept(pos, this.applyWaterlogging(world, pos, this.rootProvider.getBlockState(random, pos)));
	}

	protected BlockState applyWaterlogging(TestableWorld world, BlockPos pos, BlockState state) {
		if (state.contains(Properties.WATERLOGGED)) {
			boolean bl = world.testFluidState(pos, fluidState -> fluidState.isIn(FluidTags.WATER));
			return state.with(Properties.WATERLOGGED, Boolean.valueOf(bl));
		} else {
			return state;
		}
	}
}
