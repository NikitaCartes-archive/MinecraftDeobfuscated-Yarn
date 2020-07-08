/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RuleTest.field_25012.fieldOf("target")).forGetter(oreFeatureConfig -> oreFeatureConfig.target), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(oreFeatureConfig -> oreFeatureConfig.state), ((MapCodec)Codec.intRange(0, 64).fieldOf("size")).forGetter(oreFeatureConfig -> oreFeatureConfig.size)).apply((Applicative<OreFeatureConfig, ?>)instance, OreFeatureConfig::new));
    public final RuleTest target;
    public final int size;
    public final BlockState state;

    public OreFeatureConfig(RuleTest ruleTest, BlockState state, int size) {
        this.size = size;
        this.state = state;
        this.target = ruleTest;
    }

    public static final class class_5436 {
        public static final RuleTest field_25845 = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        public static final RuleTest field_25846 = new BlockMatchRuleTest(Blocks.NETHERRACK);
        public static final RuleTest field_25847 = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
    }
}

