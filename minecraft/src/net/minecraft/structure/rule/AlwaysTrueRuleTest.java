package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public class AlwaysTrueRuleTest extends AbstractRuleTest {
	public static final AlwaysTrueRuleTest INSTANCE = new AlwaysTrueRuleTest();

	private AlwaysTrueRuleTest() {
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return true;
	}

	@Override
	protected RuleTest method_16766() {
		return RuleTest.field_16982;
	}

	@Override
	protected <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
