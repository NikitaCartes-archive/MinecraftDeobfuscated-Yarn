package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.AbstractRandom;

public class BlockStateMatchRuleTest extends RuleTest {
	public static final Codec<BlockStateMatchRuleTest> CODEC = BlockState.CODEC
		.fieldOf("block_state")
		.<BlockStateMatchRuleTest>xmap(BlockStateMatchRuleTest::new, blockStateMatchRuleTest -> blockStateMatchRuleTest.blockState)
		.codec();
	private final BlockState blockState;

	public BlockStateMatchRuleTest(BlockState blockState) {
		this.blockState = blockState;
	}

	@Override
	public boolean test(BlockState state, AbstractRandom random) {
		return state == this.blockState;
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.BLOCKSTATE_MATCH;
	}
}
