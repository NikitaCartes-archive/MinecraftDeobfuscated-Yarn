/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.StateProvider;

public class BranchedTreeFeatureConfig
extends TreeFeatureConfig {
    public final FoliagePlacer foliagePlacer;
    public final int heightRandA;
    public final int heightRandB;
    public final int trunkHeight;
    public final int trunkHeightRandom;
    public final int trunkTopOffset;
    public final int trunkTopOffsetRandom;
    public final int foliageHeight;
    public final int foliageHeightRandom;
    public final int maxWaterDepth;
    public final boolean noVines;

    protected BranchedTreeFeatureConfig(StateProvider stateProvider, StateProvider stateProvider2, FoliagePlacer foliagePlacer, List<TreeDecorator> list, int i, int j, int k, int l, int m, int n, int o, int p, int q, int r, boolean bl) {
        super(stateProvider, stateProvider2, list, i);
        this.foliagePlacer = foliagePlacer;
        this.heightRandA = j;
        this.heightRandB = k;
        this.trunkHeight = l;
        this.trunkHeightRandom = m;
        this.trunkTopOffset = n;
        this.trunkTopOffsetRandom = o;
        this.foliageHeight = p;
        this.foliageHeightRandom = q;
        this.maxWaterDepth = r;
        this.noVines = bl;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("foliage_placer"), this.foliagePlacer.serialize(dynamicOps)).put(dynamicOps.createString("height_rand_a"), dynamicOps.createInt(this.heightRandA)).put(dynamicOps.createString("height_rand_b"), dynamicOps.createInt(this.heightRandB)).put(dynamicOps.createString("trunk_height"), dynamicOps.createInt(this.trunkHeight)).put(dynamicOps.createString("trunk_height_random"), dynamicOps.createInt(this.trunkHeightRandom)).put(dynamicOps.createString("trunk_top_offset"), dynamicOps.createInt(this.trunkTopOffset)).put(dynamicOps.createString("trunk_top_offset_random"), dynamicOps.createInt(this.trunkTopOffsetRandom)).put(dynamicOps.createString("foliage_height"), dynamicOps.createInt(this.foliageHeight)).put(dynamicOps.createString("foliage_height_random"), dynamicOps.createInt(this.foliageHeightRandom)).put(dynamicOps.createString("max_water_depth"), dynamicOps.createInt(this.maxWaterDepth)).put(dynamicOps.createString("ignore_vines"), dynamicOps.createBoolean(this.noVines));
        Dynamic<T> dynamic = new Dynamic<T>(dynamicOps, dynamicOps.createMap(builder.build()));
        return dynamic.merge(super.serialize(dynamicOps));
    }

    public static <T> BranchedTreeFeatureConfig deserialize2(Dynamic<T> dynamic) {
        TreeFeatureConfig treeFeatureConfig = TreeFeatureConfig.deserialize(dynamic);
        FoliagePlacerType<T> foliagePlacerType = Registry.FOLIAGE_PLACER_TYPE.get(new Identifier(dynamic.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
        return new BranchedTreeFeatureConfig(treeFeatureConfig.trunkProvider, treeFeatureConfig.leavesProvider, (FoliagePlacer)foliagePlacerType.deserialize(dynamic.get("foliage_placer").orElseEmptyMap()), treeFeatureConfig.decorators, treeFeatureConfig.baseHeight, dynamic.get("height_rand_a").asInt(0), dynamic.get("height_rand_b").asInt(0), dynamic.get("trunk_height").asInt(-1), dynamic.get("trunk_height_random").asInt(0), dynamic.get("trunk_top_offset").asInt(0), dynamic.get("trunk_top_offset_random").asInt(0), dynamic.get("foliage_height").asInt(-1), dynamic.get("foliage_height_random").asInt(0), dynamic.get("max_water_depth").asInt(0), dynamic.get("ignore_vines").asBoolean(false));
    }

    public static class Builder
    extends TreeFeatureConfig.Builder {
        private final FoliagePlacer foliagePlacer;
        private List<TreeDecorator> treeDecorators = ImmutableList.of();
        private int field_21272;
        private int heightRandA;
        private int heightRandB;
        private int trunkHeight = -1;
        private int trunkHeightRandom;
        private int trunkTopOffset;
        private int trunkTopOffsetRandom;
        private int foliageHeight = -1;
        private int foliageHeightRandom;
        private int maxWaterDepth;
        private boolean noVines;

        public Builder(StateProvider stateProvider, StateProvider stateProvider2, FoliagePlacer foliagePlacer) {
            super(stateProvider, stateProvider2);
            this.foliagePlacer = foliagePlacer;
        }

        public Builder treeDecorators(List<TreeDecorator> list) {
            this.treeDecorators = list;
            return this;
        }

        @Override
        public Builder baseHeight(int i) {
            this.field_21272 = i;
            return this;
        }

        public Builder heightRandA(int i) {
            this.heightRandA = i;
            return this;
        }

        public Builder heightRandB(int i) {
            this.heightRandB = i;
            return this;
        }

        public Builder trunkHeight(int i) {
            this.trunkHeight = i;
            return this;
        }

        public Builder trunkHeightRandom(int i) {
            this.trunkHeightRandom = i;
            return this;
        }

        public Builder trunkTopOffset(int i) {
            this.trunkTopOffset = i;
            return this;
        }

        public Builder trunkTopOffsetRandom(int i) {
            this.trunkTopOffsetRandom = i;
            return this;
        }

        public Builder foliageHeight(int i) {
            this.foliageHeight = i;
            return this;
        }

        public Builder foliageHeightRandom(int i) {
            this.foliageHeightRandom = i;
            return this;
        }

        public Builder maxWaterDepth(int i) {
            this.maxWaterDepth = i;
            return this;
        }

        public Builder noVines() {
            this.noVines = true;
            return this;
        }

        @Override
        public BranchedTreeFeatureConfig build() {
            return new BranchedTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.treeDecorators, this.field_21272, this.heightRandA, this.heightRandB, this.trunkHeight, this.trunkHeightRandom, this.trunkTopOffset, this.trunkTopOffsetRandom, this.foliageHeight, this.foliageHeightRandom, this.maxWaterDepth, this.noVines);
        }

        @Override
        public /* synthetic */ TreeFeatureConfig build() {
            return this.build();
        }

        @Override
        public /* synthetic */ TreeFeatureConfig.Builder baseHeight(int i) {
            return this.baseHeight(i);
        }
    }
}

