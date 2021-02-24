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
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.list(class_5876.field_29067).fieldOf("targets")).forGetter(oreFeatureConfig -> oreFeatureConfig.field_29063), ((MapCodec)Codec.intRange(0, 64).fieldOf("size")).forGetter(oreFeatureConfig -> oreFeatureConfig.size), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("discard_chance_on_air_exposure")).forGetter(oreFeatureConfig -> Float.valueOf(oreFeatureConfig.field_29064))).apply((Applicative<OreFeatureConfig, ?>)instance, OreFeatureConfig::new));
    public final List<class_5876> field_29063;
    public final int size;
    public final float field_29064;

    public OreFeatureConfig(List<class_5876> list, int i, float f) {
        this.size = i;
        this.field_29063 = list;
        this.field_29064 = f;
    }

    public OreFeatureConfig(List<class_5876> list, int i) {
        this(list, i, 0.0f);
    }

    public OreFeatureConfig(RuleTest ruleTest, BlockState blockState, int i, float f) {
        this(ImmutableList.of(new class_5876(ruleTest, blockState)), i, f);
    }

    public OreFeatureConfig(RuleTest ruleTest, BlockState blockState, int i) {
        this(ImmutableList.of(new class_5876(ruleTest, blockState)), i, 0.0f);
    }

    public static class_5876 method_33994(RuleTest ruleTest, BlockState blockState) {
        return new class_5876(ruleTest, blockState);
    }

    public static class class_5876 {
        public static final Codec<class_5876> field_29067 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RuleTest.field_25012.fieldOf("target")).forGetter(arg -> arg.field_29068), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(arg -> arg.field_29069)).apply((Applicative<class_5876, ?>)instance, class_5876::new));
        public final RuleTest field_29068;
        public final BlockState field_29069;

        private class_5876(RuleTest ruleTest, BlockState blockState) {
            this.field_29068 = ruleTest;
            this.field_29069 = blockState;
        }
    }

    public static final class Rules {
        public static final RuleTest BASE_STONE_OVERWORLD = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        public static final RuleTest field_29065 = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        public static final RuleTest field_29066 = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        public static final RuleTest NETHERRACK = new BlockMatchRuleTest(Blocks.NETHERRACK);
        public static final RuleTest BASE_STONE_NETHER = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
    }
}

