package net.minecraft.sortme.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public class BlockStateMatchRuleTest extends AbstractRuleTest {
	private final BlockState blockState;

	public BlockStateMatchRuleTest(BlockState blockState) {
		this.blockState = blockState;
	}

	public <T> BlockStateMatchRuleTest(Dynamic<T> dynamic) {
		this(BlockState.deserialize(dynamic.get("blockstate").orElseEmptyMap()));
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return blockState == this.blockState;
	}

	@Override
	protected RuleTest method_16766() {
		return RuleTest.field_16985;
	}

	@Override
	protected <T> Dynamic<T> method_16769(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("blockstate"), BlockState.serialize(dynamicOps, this.blockState).getValue()))
		);
	}
}
