package net.minecraft.structure.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockMatchRuleTest extends RuleTest {
	private final Block block;

	public BlockMatchRuleTest(Block block) {
		this.block = block;
	}

	public <T> BlockMatchRuleTest(Dynamic<T> dynamic) {
		this(Registry.BLOCK.get(new Identifier(dynamic.get("block").asString(""))));
	}

	@Override
	public boolean test(BlockState state, Random random) {
		return state.getBlock() == this.block;
	}

	@Override
	protected RuleTestType getType() {
		return RuleTestType.BLOCK_MATCH;
	}

	@Override
	protected <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("block"), ops.createString(Registry.BLOCK.getId(this.block).toString()))));
	}
}
