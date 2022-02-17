package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class RandomBlockTagMatchRuleTest extends RuleTest {
	public static final Codec<RandomBlockTagMatchRuleTest> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("block_tag").forGetter(ruleTest -> ruleTest.blockTag),
					Codec.FLOAT.fieldOf("probability").forGetter(ruleTest -> ruleTest.probability)
				)
				.apply(instance, RandomBlockTagMatchRuleTest::new)
	);
	private final Identifier blockTag;
	private final float probability;

	public RandomBlockTagMatchRuleTest(Tag.Identified<Block> blockTag, float probability) {
		this(blockTag.getId(), probability);
	}

	public RandomBlockTagMatchRuleTest(Identifier blockTagId, float probability) {
		this.blockTag = blockTagId;
		this.probability = probability;
	}

	@Override
	public boolean test(BlockState state, Random random) {
		return this.getTagPredicate().test(state) && random.nextFloat() < this.probability;
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.RANDOM_BLOCK_TAG_MATCH;
	}

	private Predicate<BlockState> getTagPredicate() {
		Tag<Block> tag = BlockTags.getTagGroup().getTag(this.blockTag);
		return tag == null ? blockState -> false : blockState -> blockState.isIn(tag);
	}
}
