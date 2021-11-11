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
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.list(Target.CODEC).fieldOf("targets")).forGetter(oreFeatureConfig -> oreFeatureConfig.targets), ((MapCodec)Codec.intRange(0, 64).fieldOf("size")).forGetter(oreFeatureConfig -> oreFeatureConfig.size), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("discard_chance_on_air_exposure")).forGetter(oreFeatureConfig -> Float.valueOf(oreFeatureConfig.discardOnAirChance))).apply((Applicative<OreFeatureConfig, ?>)instance, OreFeatureConfig::new));
    public final List<Target> targets;
    public final int size;
    public final float discardOnAirChance;

    public OreFeatureConfig(List<Target> targets, int size, float discardOnAirChance) {
        this.size = size;
        this.targets = targets;
        this.discardOnAirChance = discardOnAirChance;
    }

    public OreFeatureConfig(List<Target> targets, int size) {
        this(targets, size, 0.0f);
    }

    public OreFeatureConfig(RuleTest test, BlockState state, int size, float discardOnAirChance) {
        this(ImmutableList.of(new Target(test, state)), size, discardOnAirChance);
    }

    public OreFeatureConfig(RuleTest test, BlockState state, int size) {
        this(ImmutableList.of(new Target(test, state)), size, 0.0f);
    }

    public static Target createTarget(RuleTest test, BlockState state) {
        return new Target(test, state);
    }

    public static class Target {
        public static final Codec<Target> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RuleTest.TYPE_CODEC.fieldOf("target")).forGetter(target -> target.target), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(target -> target.state)).apply((Applicative<Target, ?>)instance, Target::new));
        public final RuleTest target;
        public final BlockState state;

        Target(RuleTest target, BlockState state) {
            this.target = target;
            this.state = state;
        }
    }
}

