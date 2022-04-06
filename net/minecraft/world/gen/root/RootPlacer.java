/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.root;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.gen.root.RootPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public abstract class RootPlacer {
    public static final Codec<RootPlacer> TYPE_CODEC = Registry.ROOT_PLACER_TYPE.getCodec().dispatch(RootPlacer::getType, RootPlacerType::getCodec);
    protected final BlockStateProvider rootProvider;

    protected static <P extends RootPlacer> Products.P1<RecordCodecBuilder.Mu<P>, BlockStateProvider> method_43182(RecordCodecBuilder.Instance<P> instance) {
        return instance.group(((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("root_provider")).forGetter(rootPlacer -> rootPlacer.rootProvider));
    }

    public RootPlacer(BlockStateProvider rootProvider) {
        this.rootProvider = rootProvider;
    }

    protected abstract RootPlacerType<?> getType();

    public abstract Optional<BlockPos> generate(TestableWorld var1, BiConsumer<BlockPos, BlockState> var2, AbstractRandom var3, BlockPos var4, TreeFeatureConfig var5);

    protected void placeRoots(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, BlockPos pos, TreeFeatureConfig config) {
        replacer.accept(pos, this.applyWaterlogging(world, pos, this.rootProvider.getBlockState(random, pos)));
    }

    protected BlockState applyWaterlogging(TestableWorld world, BlockPos pos, BlockState state) {
        if (state.contains(Properties.WATERLOGGED)) {
            boolean bl = world.testFluidState(pos, fluidState -> fluidState.isIn(FluidTags.WATER));
            return (BlockState)state.with(Properties.WATERLOGGED, bl);
        }
        return state;
    }
}

