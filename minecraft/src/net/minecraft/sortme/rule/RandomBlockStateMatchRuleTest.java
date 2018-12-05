package net.minecraft.sortme.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public class RandomBlockStateMatchRuleTest extends AbstractRuleTest {
	private final BlockState blockState;
	private final float probability;

	public RandomBlockStateMatchRuleTest(BlockState blockState, float f) {
		this.blockState = blockState;
		this.probability = f;
	}

	public <T> RandomBlockStateMatchRuleTest(Dynamic<T> dynamic) {
		this(BlockState.deserialize((Dynamic<T>)dynamic.get("blockstate").orElse(dynamic.emptyMap())), dynamic.getFloat("probability", 1.0F));
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return blockState == this.blockState && random.nextFloat() < this.probability;
	}

	@Override
	protected RuleTest getRuleTest() {
		return RuleTest.field_16984;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("blockstate"),
					BlockState.serialize(dynamicOps, this.blockState).getValue(),
					dynamicOps.createString("probability"),
					dynamicOps.createFloat(this.probability)
				)
			)
		);
	}
}
