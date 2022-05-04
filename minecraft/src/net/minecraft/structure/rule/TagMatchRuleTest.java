package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;

public class TagMatchRuleTest extends RuleTest {
	public static final Codec<TagMatchRuleTest> CODEC = TagKey.unprefixedCodec(Registry.BLOCK_KEY)
		.fieldOf("tag")
		.<TagMatchRuleTest>xmap(TagMatchRuleTest::new, tagMatchRuleTest -> tagMatchRuleTest.tag)
		.codec();
	private final TagKey<Block> tag;

	public TagMatchRuleTest(TagKey<Block> tag) {
		this.tag = tag;
	}

	@Override
	public boolean test(BlockState state, AbstractRandom random) {
		return state.isIn(this.tag);
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.TAG_MATCH;
	}
}
