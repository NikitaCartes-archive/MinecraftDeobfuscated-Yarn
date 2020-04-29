package net.minecraft.structure.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RandomBlockMatchRuleTest extends RuleTest {
	private final Block block;
	private final float probability;

	public RandomBlockMatchRuleTest(Block block, float probability) {
		this.block = block;
		this.probability = probability;
	}

	public <T> RandomBlockMatchRuleTest(Dynamic<T> dynamic) {
		this(Registry.BLOCK.get(new Identifier(dynamic.get("block").asString(""))), dynamic.get("probability").asFloat(1.0F));
	}

	@Override
	public boolean test(BlockState state, Random random) {
		return state.isOf(this.block) && random.nextFloat() < this.probability;
	}

	@Override
	protected RuleTestType getType() {
		return RuleTestType.RANDOM_BLOCK_MATCH;
	}

	@Override
	protected <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("block"),
					ops.createString(Registry.BLOCK.getId(this.block).toString()),
					ops.createString("probability"),
					ops.createFloat(this.probability)
				)
			)
		);
	}
}
