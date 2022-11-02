package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class AlwaysTruePosRuleTest extends PosRuleTest {
	public static final Codec<AlwaysTruePosRuleTest> CODEC = Codec.unit((Supplier<AlwaysTruePosRuleTest>)(() -> AlwaysTruePosRuleTest.INSTANCE));
	public static final AlwaysTruePosRuleTest INSTANCE = new AlwaysTruePosRuleTest();

	private AlwaysTruePosRuleTest() {
	}

	@Override
	public boolean test(BlockPos originalPos, BlockPos currentPos, BlockPos pivot, Random random) {
		return true;
	}

	@Override
	protected PosRuleTestType<?> getType() {
		return PosRuleTestType.ALWAYS_TRUE;
	}
}
