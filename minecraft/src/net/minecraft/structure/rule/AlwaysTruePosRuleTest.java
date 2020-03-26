package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class AlwaysTruePosRuleTest extends PosRuleTest {
	public static final AlwaysTruePosRuleTest INSTANCE = new AlwaysTruePosRuleTest();

	private AlwaysTruePosRuleTest() {
	}

	@Override
	public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
		return true;
	}

	@Override
	protected PosRuleTestType getType() {
		return PosRuleTestType.ALWAYS_TRUE;
	}

	@Override
	protected <T> Dynamic<T> serializeContents(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.emptyMap());
	}
}
