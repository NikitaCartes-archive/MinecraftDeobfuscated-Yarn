package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;

public class OreFeatureConfig implements FeatureConfig {
	public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RuleTest.field_25012.fieldOf("target").forGetter(oreFeatureConfig -> oreFeatureConfig.target),
					BlockState.CODEC.fieldOf("state").forGetter(oreFeatureConfig -> oreFeatureConfig.state),
					Codec.intRange(0, 64).fieldOf("size").forGetter(oreFeatureConfig -> oreFeatureConfig.size)
				)
				.apply(instance, OreFeatureConfig::new)
	);
	public final RuleTest target;
	public final int size;
	public final BlockState state;

	public OreFeatureConfig(RuleTest ruleTest, BlockState state, int size) {
		this.size = size;
		this.state = state;
		this.target = ruleTest;
	}

	public static final class Rules {
		public static final RuleTest BASE_STONE_OVERWORLD = new TagMatchRuleTest(BlockTags.field_25806);
		public static final RuleTest NETHERRACK = new BlockMatchRuleTest(Blocks.field_10515);
		public static final RuleTest BASE_STONE_NETHER = new TagMatchRuleTest(BlockTags.field_25807);
	}
}
