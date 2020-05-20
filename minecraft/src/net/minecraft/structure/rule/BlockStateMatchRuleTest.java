package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;

public class BlockStateMatchRuleTest extends RuleTest {
	public static final Codec<BlockStateMatchRuleTest> field_25001 = BlockState.field_24734
		.fieldOf("block_state")
		.<BlockStateMatchRuleTest>xmap(BlockStateMatchRuleTest::new, blockStateMatchRuleTest -> blockStateMatchRuleTest.blockState)
		.codec();
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
