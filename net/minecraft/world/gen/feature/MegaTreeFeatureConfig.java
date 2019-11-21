/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.stateprovider.StateProvider;

public class MegaTreeFeatureConfig
extends TreeFeatureConfig {
    public final int heightInterval;
    public final int crownHeight;

    protected MegaTreeFeatureConfig(StateProvider stateProvider, StateProvider stateProvider2, List<TreeDecorator> list, int i, int j, int k) {
        super(stateProvider, stateProvider2, list, i);
        this.heightInterval = j;
        this.crownHeight = k;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        Dynamic<T> dynamic = new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("height_interval"), dynamicOps.createInt(this.heightInterval), dynamicOps.createString("crown_height"), dynamicOps.createInt(this.crownHeight))));
        return dynamic.merge(super.serialize(dynamicOps));
    }

    public static <T> MegaTreeFeatureConfig method_23408(Dynamic<T> dynamic) {
        TreeFeatureConfig treeFeatureConfig = TreeFeatureConfig.deserialize(dynamic);
        return new MegaTreeFeatureConfig(treeFeatureConfig.trunkProvider, treeFeatureConfig.leavesProvider, treeFeatureConfig.decorators, treeFeatureConfig.baseHeight, dynamic.get("height_interval").asInt(0), dynamic.get("crown_height").asInt(0));
    }

    public static class Builder
    extends TreeFeatureConfig.Builder {
        private List<TreeDecorator> field_21234 = ImmutableList.of();
        private int field_21235;
        private int heightInterval;
        private int crownHeight;

        public Builder(StateProvider stateProvider, StateProvider stateProvider2) {
            super(stateProvider, stateProvider2);
        }

        public Builder method_23411(List<TreeDecorator> list) {
            this.field_21234 = list;
            return this;
        }

        @Override
        public Builder baseHeight(int i) {
            this.field_21235 = i;
            return this;
        }

        public Builder heightInterval(int i) {
            this.heightInterval = i;
            return this;
        }

        public Builder crownHeight(int i) {
            this.crownHeight = i;
            return this;
        }

        @Override
        public MegaTreeFeatureConfig build() {
            return new MegaTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.field_21234, this.field_21235, this.heightInterval, this.crownHeight);
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

