/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Target.field_24898.fieldOf("target")).forGetter(oreFeatureConfig -> oreFeatureConfig.target), ((MapCodec)BlockState.field_24734.fieldOf("state")).forGetter(oreFeatureConfig -> oreFeatureConfig.state), ((MapCodec)Codec.INT.fieldOf("size")).withDefault(0).forGetter(oreFeatureConfig -> oreFeatureConfig.size)).apply((Applicative<OreFeatureConfig, ?>)instance, OreFeatureConfig::new));
    public final Target target;
    public final int size;
    public final BlockState state;

    public OreFeatureConfig(Target target, BlockState state, int size) {
        this.size = size;
        this.state = state;
        this.target = target;
    }

    public static enum Target implements StringIdentifiable
    {
        NATURAL_STONE("natural_stone", blockState -> {
            if (blockState != null) {
                return blockState.isOf(Blocks.STONE) || blockState.isOf(Blocks.GRANITE) || blockState.isOf(Blocks.DIORITE) || blockState.isOf(Blocks.ANDESITE);
            }
            return false;
        }),
        NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK)),
        NETHER_ORE_REPLACEABLES("nether_ore_replaceables", blockState -> {
            if (blockState != null) {
                return blockState.isOf(Blocks.NETHERRACK) || blockState.isOf(Blocks.BASALT) || blockState.isOf(Blocks.BLACKSTONE);
            }
            return false;
        });

        public static final Codec<Target> field_24898;
        private static final Map<String, Target> nameMap;
        private final String name;
        private final Predicate<BlockState> predicate;

        private Target(String name, Predicate<BlockState> predicate) {
            this.name = name;
            this.predicate = predicate;
        }

        public String getName() {
            return this.name;
        }

        public static Target byName(String name) {
            return nameMap.get(name);
        }

        public Predicate<BlockState> getCondition() {
            return this.predicate;
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24898 = StringIdentifiable.method_28140(Target::values, Target::byName);
            nameMap = Arrays.stream(Target.values()).collect(Collectors.toMap(Target::getName, target -> target));
        }
    }
}

