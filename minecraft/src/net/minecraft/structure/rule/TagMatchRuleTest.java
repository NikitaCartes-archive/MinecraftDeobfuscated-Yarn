package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;

public class TagMatchRuleTest extends RuleTest {
	public static final Codec<TagMatchRuleTest> field_25014 = Tag.method_28134(BlockTags::getContainer)
		.fieldOf("tag")
		.<TagMatchRuleTest>xmap(TagMatchRuleTest::new, tagMatchRuleTest -> tagMatchRuleTest.tag)
		.codec();
	private final Tag<Block> tag;

	public TagMatchRuleTest(Tag<Block> tag) {
		this.tag = tag;
	}

	@Override
	public boolean test(BlockState state, Random random) {
		return state.isIn(this.tag);
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.TAG_MATCH;
	}
}
