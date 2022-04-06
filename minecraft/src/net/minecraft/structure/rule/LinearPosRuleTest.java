package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;

public class LinearPosRuleTest extends PosRuleTest {
	public static final Codec<LinearPosRuleTest> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter(linearPosRuleTest -> linearPosRuleTest.minChance),
					Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter(linearPosRuleTest -> linearPosRuleTest.maxChance),
					Codec.INT.fieldOf("min_dist").orElse(0).forGetter(linearPosRuleTest -> linearPosRuleTest.minDistance),
					Codec.INT.fieldOf("max_dist").orElse(0).forGetter(linearPosRuleTest -> linearPosRuleTest.maxDistance)
				)
				.apply(instance, LinearPosRuleTest::new)
	);
	private final float minChance;
	private final float maxChance;
	private final int minDistance;
	private final int maxDistance;

	public LinearPosRuleTest(float minChance, float maxChance, int minDistance, int maxDistance) {
		if (minDistance >= maxDistance) {
			throw new IllegalArgumentException("Invalid range: [" + minDistance + "," + maxDistance + "]");
		} else {
			this.minChance = minChance;
			this.maxChance = maxChance;
			this.minDistance = minDistance;
			this.maxDistance = maxDistance;
		}
	}

	@Override
	public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos pivot, AbstractRandom random) {
		int i = blockPos2.getManhattanDistance(pivot);
		float f = random.nextFloat();
		return f <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress((float)i, (float)this.minDistance, (float)this.maxDistance));
	}

	@Override
	protected PosRuleTestType<?> getType() {
		return PosRuleTestType.LINEAR_POS;
	}
}
