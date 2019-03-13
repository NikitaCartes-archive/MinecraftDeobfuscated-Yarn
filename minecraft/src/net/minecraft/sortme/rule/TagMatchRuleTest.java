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
	private final Tag<Block> field_16747;

	public TagMatchRuleTest(Tag<Block> tag) {
		this.field_16747 = tag;
	}

	public <T> TagMatchRuleTest(Dynamic<T> dynamic) {
		this(BlockTags.method_15073().get(new Identifier(dynamic.get("tag").asString(""))));
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return blockState.method_11602(this.field_16747);
	}

	@Override
	protected RuleTest method_16766() {
		return RuleTest.field_16983;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("tag"), dynamicOps.createString(this.field_16747.getId().toString())))
		);
	}
}
