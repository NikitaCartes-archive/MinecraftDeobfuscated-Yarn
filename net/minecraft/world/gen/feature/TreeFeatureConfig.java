/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class TreeFeatureConfig
implements FeatureConfig {
    public static final Codec<TreeFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("trunk_provider")).forGetter(treeFeatureConfig -> treeFeatureConfig.trunkProvider), ((MapCodec)TrunkPlacer.TYPE_CODEC.fieldOf("trunk_placer")).forGetter(treeFeatureConfig -> treeFeatureConfig.trunkPlacer), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("foliage_provider")).forGetter(treeFeatureConfig -> treeFeatureConfig.foliageProvider), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("sapling_provider")).forGetter(treeFeatureConfig -> treeFeatureConfig.field_33933), ((MapCodec)FoliagePlacer.TYPE_CODEC.fieldOf("foliage_placer")).forGetter(treeFeatureConfig -> treeFeatureConfig.foliagePlacer), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("dirt_provider")).forGetter(treeFeatureConfig -> treeFeatureConfig.dirtProvider), ((MapCodec)FeatureSize.TYPE_CODEC.fieldOf("minimum_size")).forGetter(treeFeatureConfig -> treeFeatureConfig.minimumSize), ((MapCodec)TreeDecorator.TYPE_CODEC.listOf().fieldOf("decorators")).forGetter(treeFeatureConfig -> treeFeatureConfig.decorators), ((MapCodec)Codec.BOOL.fieldOf("ignore_vines")).orElse(false).forGetter(treeFeatureConfig -> treeFeatureConfig.ignoreVines), ((MapCodec)Codec.BOOL.fieldOf("force_dirt")).orElse(false).forGetter(treeFeatureConfig -> treeFeatureConfig.forceDirt)).apply((Applicative<TreeFeatureConfig, ?>)instance, TreeFeatureConfig::new));
    public final BlockStateProvider trunkProvider;
    public final BlockStateProvider dirtProvider;
    public final TrunkPlacer trunkPlacer;
    public final BlockStateProvider foliageProvider;
    public final BlockStateProvider field_33933;
    public final FoliagePlacer foliagePlacer;
    public final FeatureSize minimumSize;
    public final List<TreeDecorator> decorators;
    public final boolean ignoreVines;
    public final boolean forceDirt;

    protected TreeFeatureConfig(BlockStateProvider trunkProvider, TrunkPlacer trunkPlacer, BlockStateProvider foliageProvider, BlockStateProvider blockStateProvider, FoliagePlacer foliagePlacer, BlockStateProvider blockStateProvider2, FeatureSize featureSize, List<TreeDecorator> list, boolean bl, boolean bl2) {
        this.trunkProvider = trunkProvider;
        this.trunkPlacer = trunkPlacer;
        this.foliageProvider = foliageProvider;
        this.foliagePlacer = foliagePlacer;
        this.dirtProvider = blockStateProvider2;
        this.field_33933 = blockStateProvider;
        this.minimumSize = featureSize;
        this.decorators = list;
        this.ignoreVines = bl;
        this.forceDirt = bl2;
    }

    public TreeFeatureConfig setTreeDecorators(List<TreeDecorator> decorators) {
        return new TreeFeatureConfig(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.field_33933, this.foliagePlacer, this.dirtProvider, this.minimumSize, decorators, this.ignoreVines, this.forceDirt);
    }

    public static class Builder {
        public final BlockStateProvider trunkProvider;
        private final TrunkPlacer trunkPlacer;
        public final BlockStateProvider foliageProvider;
        public final BlockStateProvider field_33934;
        private final FoliagePlacer foliagePlacer;
        private BlockStateProvider dirtProvider;
        private final FeatureSize minimumSize;
        private List<TreeDecorator> decorators = ImmutableList.of();
        private boolean ignoreVines;
        private boolean forceDirt;

        public Builder(BlockStateProvider trunkProvider, TrunkPlacer trunkPlacer, BlockStateProvider foliageProvider, BlockStateProvider blockStateProvider, FoliagePlacer foliagePlacer, FeatureSize featureSize) {
            this.trunkProvider = trunkProvider;
            this.trunkPlacer = trunkPlacer;
            this.foliageProvider = foliageProvider;
            this.field_33934 = blockStateProvider;
            this.dirtProvider = new SimpleBlockStateProvider(Blocks.DIRT.getDefaultState());
            this.foliagePlacer = foliagePlacer;
            this.minimumSize = featureSize;
        }

        public Builder dirtProvider(BlockStateProvider dirtProvider) {
            this.dirtProvider = dirtProvider;
            return this;
        }

        public Builder decorators(List<TreeDecorator> decorators) {
            this.decorators = decorators;
            return this;
        }

        public Builder ignoreVines() {
            this.ignoreVines = true;
            return this;
        }

        public Builder forceDirt() {
            this.forceDirt = true;
            return this;
        }

        public TreeFeatureConfig build() {
            return new TreeFeatureConfig(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.field_33934, this.foliagePlacer, this.dirtProvider, this.minimumSize, this.decorators, this.ignoreVines, this.forceDirt);
        }
    }
}

