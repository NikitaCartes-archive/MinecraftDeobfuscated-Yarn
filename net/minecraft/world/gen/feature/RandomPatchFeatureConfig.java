/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;
import net.minecraft.world.gen.stateprovider.StateProvider;
import net.minecraft.world.gen.stateprovider.StateProviderType;

public class RandomPatchFeatureConfig
implements FeatureConfig {
    public final StateProvider stateProvider;
    public final BlockPlacer blockPlacer;
    public final Set<Block> whitelist;
    public final Set<BlockState> blacklist;
    public final int tries;
    public final int spreadX;
    public final int spreadY;
    public final int spreadZ;
    public final boolean canReplace;
    public final boolean project;
    public final boolean needsWater;

    private RandomPatchFeatureConfig(StateProvider stateProvider, BlockPlacer blockPlacer, Set<Block> set, Set<BlockState> set2, int i, int j, int k, int l, boolean bl, boolean bl2, boolean bl3) {
        this.stateProvider = stateProvider;
        this.blockPlacer = blockPlacer;
        this.whitelist = set;
        this.blacklist = set2;
        this.tries = i;
        this.spreadX = j;
        this.spreadY = k;
        this.spreadZ = l;
        this.canReplace = bl;
        this.project = bl2;
        this.needsWater = bl3;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("state_provider"), this.stateProvider.serialize(dynamicOps)).put(dynamicOps.createString("block_placer"), this.blockPlacer.serialize(dynamicOps)).put(dynamicOps.createString("whitelist"), dynamicOps.createList(this.whitelist.stream().map(block -> BlockState.serialize(dynamicOps, block.getDefaultState()).getValue()))).put(dynamicOps.createString("blacklist"), dynamicOps.createList(this.blacklist.stream().map(blockState -> BlockState.serialize(dynamicOps, blockState).getValue()))).put(dynamicOps.createString("tries"), dynamicOps.createInt(this.tries)).put(dynamicOps.createString("xspread"), dynamicOps.createInt(this.spreadX)).put(dynamicOps.createString("yspread"), dynamicOps.createInt(this.spreadY)).put(dynamicOps.createString("zspread"), dynamicOps.createInt(this.spreadZ)).put(dynamicOps.createString("can_replace"), dynamicOps.createBoolean(this.canReplace)).put(dynamicOps.createString("project"), dynamicOps.createBoolean(this.project)).put(dynamicOps.createString("need_water"), dynamicOps.createBoolean(this.needsWater));
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(builder.build()));
    }

    public static <T> RandomPatchFeatureConfig deserialize(Dynamic<T> dynamic) {
        StateProviderType<T> stateProviderType = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier(dynamic.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        BlockPlacerType<T> blockPlacerType = Registry.BLOCK_PLACER_TYPE.get(new Identifier(dynamic.get("block_placer").get("type").asString().orElseThrow(RuntimeException::new)));
        return new RandomPatchFeatureConfig((StateProvider)stateProviderType.deserialize(dynamic.get("state_provider").orElseEmptyMap()), (BlockPlacer)blockPlacerType.deserialize(dynamic.get("block_placer").orElseEmptyMap()), dynamic.get("whitelist").asList(BlockState::deserialize).stream().map(BlockState::getBlock).collect(Collectors.toSet()), (Set<BlockState>)Sets.newHashSet(dynamic.get("blacklist").asList(BlockState::deserialize)), dynamic.get("tries").asInt(128), dynamic.get("xspread").asInt(7), dynamic.get("yspread").asInt(3), dynamic.get("zspread").asInt(7), dynamic.get("can_replace").asBoolean(false), dynamic.get("project").asBoolean(true), dynamic.get("need_water").asBoolean(false));
    }

    public static class Builder {
        private final StateProvider stateProvider;
        private final BlockPlacer blockPlacer;
        private Set<Block> whitelist = ImmutableSet.of();
        private Set<BlockState> blacklist = ImmutableSet.of();
        private int tries = 64;
        private int spreadX = 7;
        private int spreadY = 3;
        private int spreadZ = 7;
        private boolean canReplace;
        private boolean project = true;
        private boolean needsWater = false;

        public Builder(StateProvider stateProvider, BlockPlacer blockPlacer) {
            this.stateProvider = stateProvider;
            this.blockPlacer = blockPlacer;
        }

        public Builder whitelist(Set<Block> set) {
            this.whitelist = set;
            return this;
        }

        public Builder blacklist(Set<BlockState> set) {
            this.blacklist = set;
            return this;
        }

        public Builder tries(int i) {
            this.tries = i;
            return this;
        }

        public Builder spreadX(int i) {
            this.spreadX = i;
            return this;
        }

        public Builder spreadY(int i) {
            this.spreadY = i;
            return this;
        }

        public Builder spreadZ(int i) {
            this.spreadZ = i;
            return this;
        }

        public Builder canReplace() {
            this.canReplace = true;
            return this;
        }

        public Builder cannotProject() {
            this.project = false;
            return this;
        }

        public Builder needsWater() {
            this.needsWater = true;
            return this;
        }

        public RandomPatchFeatureConfig build() {
            return new RandomPatchFeatureConfig(this.stateProvider, this.blockPlacer, this.whitelist, this.blacklist, this.tries, this.spreadX, this.spreadY, this.spreadZ, this.canReplace, this.project, this.needsWater);
        }
    }
}

