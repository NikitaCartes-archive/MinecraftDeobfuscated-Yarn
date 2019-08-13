package net.minecraft.structure.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockMatchRuleTest extends AbstractRuleTest {
	private final Block block;

	public BlockMatchRuleTest(Block block) {
		this.block = block;
	}

	public <T> BlockMatchRuleTest(Dynamic<T> dynamic) {
		this(Registry.BLOCK.get(new Identifier(dynamic.get("block").asString(""))));
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return blockState.getBlock() == this.block;
	}

	@Override
	protected RuleTest getRuleTest() {
		return RuleTest.field_16981;
	}

	@Override
	protected <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("block"), dynamicOps.createString(Registry.BLOCK.getId(this.block).toString())))
		);
	}
}
