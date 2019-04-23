/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public final Target target;
    public final int size;
    public final BlockState state;

    public OreFeatureConfig(Target target, BlockState blockState, int i) {
        this.size = i;
        this.state = blockState;
        this.target = target;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("size"), dynamicOps.createInt(this.size), dynamicOps.createString("target"), dynamicOps.createString(this.target.getName()), dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue())));
    }

    public static OreFeatureConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("size").asInt(0);
        Target target = Target.byName(dynamic.get("target").asString(""));
        BlockState blockState = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new OreFeatureConfig(target, blockState, i);
    }

    public static enum Target {
        NATURAL_STONE("natural_stone", blockState -> {
            if (blockState != null) {
                Block block = blockState.getBlock();
                return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
            }
            return false;
        }),
        NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK));

        private static final Map<String, Target> nameMap;
        private final String name;
        private final Predicate<BlockState> predicate;

        private Target(String string2, Predicate<BlockState> predicate) {
            this.name = string2;
            this.predicate = predicate;
        }

        public String getName() {
            return this.name;
        }

        public static Target byName(String string) {
            return nameMap.get(string);
        }

        public Predicate<BlockState> getCondition() {
            return this.predicate;
        }

        static {
            nameMap = Arrays.stream(Target.values()).collect(Collectors.toMap(Target::getName, target -> target));
        }
    }
}

