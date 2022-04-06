package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;

public class BlockMatchRuleTest extends RuleTest {
	public static final Codec<BlockMatchRuleTest> CODEC = Registry.BLOCK
		.getCodec()
		.fieldOf("block")
		.<BlockMatchRuleTest>xmap(BlockMatchRuleTest::new, blockMatchRuleTest -> blockMatchRuleTest.block)
		.codec();
	private final Block block;

	public BlockMatchRuleTest(Block block) {
		this.block = block;
	}

	@Override
	public boolean test(BlockState state, AbstractRandom random) {
		return state.isOf(this.block);
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.BLOCK_MATCH;
	}
}
