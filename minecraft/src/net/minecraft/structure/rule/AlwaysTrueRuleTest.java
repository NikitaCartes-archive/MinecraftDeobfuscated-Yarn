package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;

public class AlwaysTrueRuleTest extends RuleTest {
	public static final Codec<AlwaysTrueRuleTest> field_24994 = Codec.unit((Supplier<AlwaysTrueRuleTest>)(() -> AlwaysTrueRuleTest.INSTANCE));
	public static final AlwaysTrueRuleTest INSTANCE = new AlwaysTrueRuleTest();

	private AlwaysTrueRuleTest() {
	}

	@Override
	public boolean test(BlockState state, Random random) {
		return true;
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.ALWAYS_TRUE;
	}
}
