package net.minecraft.structure.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RandomBlockMatchRuleTest extends AbstractRuleTest {
	private final Block block;
	private final float probability;

	public RandomBlockMatchRuleTest(Block block, float f) {
		this.block = block;
		this.probability = f;
	}

	public <T> RandomBlockMatchRuleTest(Dynamic<T> dynamic) {
		this(Registry.BLOCK.get(new Identifier(dynamic.get("block").asString(""))), dynamic.get("probability").asFloat(1.0F));
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return blockState.getBlock() == this.block && random.nextFloat() < this.probability;
	}

	@Override
	protected RuleTest getRuleTest() {
		return RuleTest.RANDOM_BLOCK_MATCH;
	}

	@Override
	protected <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("block"),
					dynamicOps.createString(Registry.BLOCK.getId(this.block).toString()),
					dynamicOps.createString("probability"),
					dynamicOps.createFloat(this.probability)
				)
			)
		);
	}
}
