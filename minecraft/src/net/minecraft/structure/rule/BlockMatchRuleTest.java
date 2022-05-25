package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

public class BlockMatchRuleTest extends RuleTest {
	public static final Codec<BlockMatchRuleTest> CODEC = Registry.BLOCK
		.getCodec()
		.fieldOf("block")
		.<BlockMatchRuleTest>xmap(BlockMatchRuleTest::new, ruleTest -> ruleTest.block)
		.codec();
	private final Block block;

	public BlockMatchRuleTest(Block block) {
		this.block = block;
	}

	@Override
	public boolean test(BlockState state, Random random) {
		return state.isOf(this.block);
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.BLOCK_MATCH;
	}
}
