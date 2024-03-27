package net.minecraft.structure.rule;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.Random;

public class BlockStateMatchRuleTest extends RuleTest {
	public static final MapCodec<BlockStateMatchRuleTest> CODEC = BlockState.CODEC
		.fieldOf("block_state")
		.xmap(BlockStateMatchRuleTest::new, ruleTest -> ruleTest.blockState);
	private final BlockState blockState;

	public BlockStateMatchRuleTest(BlockState blockState) {
		this.blockState = blockState;
	}

	@Override
	public boolean test(BlockState state, Random random) {
		return state == this.blockState;
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.BLOCKSTATE_MATCH;
	}
}
