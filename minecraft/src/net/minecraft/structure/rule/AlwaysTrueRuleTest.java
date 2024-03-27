package net.minecraft.structure.rule;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.Random;

public class AlwaysTrueRuleTest extends RuleTest {
	public static final MapCodec<AlwaysTrueRuleTest> CODEC = MapCodec.unit((Supplier<AlwaysTrueRuleTest>)(() -> AlwaysTrueRuleTest.INSTANCE));
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
