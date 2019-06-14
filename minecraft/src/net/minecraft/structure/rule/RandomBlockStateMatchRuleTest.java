package net.minecraft.structure.rule;

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
		this(BlockState.deserialize(dynamic.get("blockstate").orElseEmptyMap()), dynamic.get("probability").asFloat(1.0F));
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return blockState == this.blockState && random.nextFloat() < this.probability;
	}

	@Override
	protected RuleTest method_16766() {
		return RuleTest.field_16984;
	}

	@Override
	protected <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
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
