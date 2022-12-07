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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.root.AboveRootPlacement;
import net.minecraft.world.gen.root.RootPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public abstract class RootPlacer {
    public static final Codec<RootPlacer> TYPE_CODEC = Registries.ROOT_PLACER_TYPE.getCodec().dispatch(RootPlacer::getType, RootPlacerType::getCodec);
    protected final IntProvider trunkOffsetY;
    protected final BlockStateProvider rootProvider;
    protected final Optional<AboveRootPlacement> aboveRootPlacement;

    protected static <P extends RootPlacer> Products.P3<RecordCodecBuilder.Mu<P>, IntProvider, BlockStateProvider, Optional<AboveRootPlacement>> method_43182(RecordCodecBuilder.Instance<P> instance) {
        return instance.group(((MapCodec)IntProvider.VALUE_CODEC.fieldOf("trunk_offset_y")).forGetter(rootPlacer -> rootPlacer.trunkOffsetY), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("root_provider")).forGetter(rootPlacer -> rootPlacer.rootProvider), AboveRootPlacement.CODEC.optionalFieldOf("above_root_placement").forGetter(rootPlacer -> rootPlacer.aboveRootPlacement));
    }

    public RootPlacer(IntProvider trunkOffsetY, BlockStateProvider rootProvider, Optional<AboveRootPlacement> aboveRootPlacement) {
        this.trunkOffsetY = trunkOffsetY;
        this.rootProvider = rootProvider;
        this.aboveRootPlacement = aboveRootPlacement;
    }

    protected abstract RootPlacerType<?> getType();

    public abstract boolean generate(TestableWorld var1, BiConsumer<BlockPos, BlockState> var2, Random var3, BlockPos var4, BlockPos var5, TreeFeatureConfig var6);

    protected boolean canGrowThrough(TestableWorld world, BlockPos pos) {
        return TreeFeature.canReplace(world, pos);
    }

    protected void placeRoots(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config) {
        if (!this.canGrowThrough(world, pos)) {
            return;
        }
        replacer.accept(pos, this.applyWaterlogging(world, pos, this.rootProvider.get(random, pos)));
        if (this.aboveRootPlacement.isPresent()) {
            AboveRootPlacement aboveRootPlacement = this.aboveRootPlacement.get();
            BlockPos blockPos = pos.up();
            if (random.nextFloat() < aboveRootPlacement.aboveRootPlacementChance() && world.testBlockState(blockPos, AbstractBlock.AbstractBlockState::isAir)) {
                replacer.accept(blockPos, this.applyWaterlogging(world, blockPos, aboveRootPlacement.aboveRootProvider().get(random, blockPos)));
            }
        }
    }

    protected BlockState applyWaterlogging(TestableWorld world, BlockPos pos, BlockState state) {
        if (state.contains(Properties.WATERLOGGED)) {
            boolean bl = world.testFluidState(pos, fluidState -> fluidState.isIn(FluidTags.WATER));
            return (BlockState)state.with(Properties.WATERLOGGED, bl);
        }
        return state;
    }

    public BlockPos trunkOffset(BlockPos pos, Random random) {
        return pos.up(this.trunkOffsetY.get(random));
    }
}

