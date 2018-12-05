package net.minecraft.sortme.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TagMatchRuleTest extends AbstractRuleTest {
	private final Tag<Block> tag;

	public TagMatchRuleTest(Tag<Block> tag) {
		this.tag = tag;
	}

	public <T> TagMatchRuleTest(Dynamic<T> dynamic) {
		this(BlockTags.getContainer().get(new Identifier(dynamic.getString("tag"))));
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return blockState.matches(this.tag);
	}

	@Override
	protected RuleTest getRuleTest() {
		return RuleTest.field_16983;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("tag"), dynamicOps.createString(this.tag.getId().toString()))));
	}
}
