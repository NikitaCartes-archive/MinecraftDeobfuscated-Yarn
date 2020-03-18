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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class RandomPatchFeatureConfig
implements FeatureConfig {
    public final BlockStateProvider stateProvider;
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

    private RandomPatchFeatureConfig(BlockStateProvider stateProvider, BlockPlacer blockPlacer, Set<Block> whitelist, Set<BlockState> blacklist, int tries, int spreadX, int spreadY, int spreadZ, boolean canReplace, boolean project, boolean needsWater) {
        this.stateProvider = stateProvider;
        this.blockPlacer = blockPlacer;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.tries = tries;
        this.spreadX = spreadX;
        this.spreadY = spreadY;
        this.spreadZ = spreadZ;
        this.canReplace = canReplace;
        this.project = project;
        this.needsWater = needsWater;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(ops.createString("state_provider"), this.stateProvider.serialize(ops)).put(ops.createString("block_placer"), this.blockPlacer.serialize(ops)).put(ops.createString("whitelist"), ops.createList(this.whitelist.stream().map(block -> BlockState.serialize(ops, block.getDefaultState()).getValue()))).put(ops.createString("blacklist"), ops.createList(this.blacklist.stream().map(blockState -> BlockState.serialize(ops, blockState).getValue()))).put(ops.createString("tries"), ops.createInt(this.tries)).put(ops.createString("xspread"), ops.createInt(this.spreadX)).put(ops.createString("yspread"), ops.createInt(this.spreadY)).put(ops.createString("zspread"), ops.createInt(this.spreadZ)).put(ops.createString("can_replace"), ops.createBoolean(this.canReplace)).put(ops.createString("project"), ops.createBoolean(this.project)).put(ops.createString("need_water"), ops.createBoolean(this.needsWater));
        return new Dynamic<T>(ops, ops.createMap(builder.build()));
    }

    public static <T> RandomPatchFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockStateProviderType<T> blockStateProviderType = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier(dynamic.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        BlockPlacerType<T> blockPlacerType = Registry.BLOCK_PLACER_TYPE.get(new Identifier(dynamic.get("block_placer").get("type").asString().orElseThrow(RuntimeException::new)));
        return new RandomPatchFeatureConfig((BlockStateProvider)blockStateProviderType.deserialize(dynamic.get("state_provider").orElseEmptyMap()), (BlockPlacer)blockPlacerType.deserialize(dynamic.get("block_placer").orElseEmptyMap()), dynamic.get("whitelist").asList(BlockState::deserialize).stream().map(AbstractBlock.AbstractBlockState::getBlock).collect(Collectors.toSet()), (Set<BlockState>)Sets.newHashSet(dynamic.get("blacklist").asList(BlockState::deserialize)), dynamic.get("tries").asInt(128), dynamic.get("xspread").asInt(7), dynamic.get("yspread").asInt(3), dynamic.get("zspread").asInt(7), dynamic.get("can_replace").asBoolean(false), dynamic.get("project").asBoolean(true), dynamic.get("need_water").asBoolean(false));
    }

    public static class Builder {
        private final BlockStateProvider stateProvider;
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

        public Builder(BlockStateProvider stateProvider, BlockPlacer blockPlacer) {
            this.stateProvider = stateProvider;
            this.blockPlacer = blockPlacer;
        }

        public Builder whitelist(Set<Block> whitelist) {
            this.whitelist = whitelist;
            return this;
        }

        public Builder blacklist(Set<BlockState> blacklist) {
            this.blacklist = blacklist;
            return this;
        }

        public Builder tries(int tries) {
            this.tries = tries;
            return this;
        }

        public Builder spreadX(int spreadX) {
            this.spreadX = spreadX;
            return this;
        }

        public Builder spreadY(int spreadY) {
            this.spreadY = spreadY;
            return this;
        }

        public Builder spreadZ(int spreadZ) {
            this.spreadZ = spreadZ;
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

