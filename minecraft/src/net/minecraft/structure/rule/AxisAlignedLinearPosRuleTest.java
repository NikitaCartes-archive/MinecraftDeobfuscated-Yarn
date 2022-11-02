package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class AxisAlignedLinearPosRuleTest extends PosRuleTest {
	public static final Codec<AxisAlignedLinearPosRuleTest> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter(ruleTest -> ruleTest.minChance),
					Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter(ruleTest -> ruleTest.maxChance),
					Codec.INT.fieldOf("min_dist").orElse(0).forGetter(ruleTest -> ruleTest.minDistance),
					Codec.INT.fieldOf("max_dist").orElse(0).forGetter(ruleTest -> ruleTest.maxDistance),
					Direction.Axis.CODEC.fieldOf("axis").orElse(Direction.Axis.Y).forGetter(ruleTest -> ruleTest.axis)
				)
				.apply(instance, AxisAlignedLinearPosRuleTest::new)
	);
	private final float minChance;
	private final float maxChance;
	private final int minDistance;
	private final int maxDistance;
	private final Direction.Axis axis;

	public AxisAlignedLinearPosRuleTest(float minChance, float maxChance, int minDistance, int maxDistance, Direction.Axis axis) {
		if (minDistance >= maxDistance) {
			throw new IllegalArgumentException("Invalid range: [" + minDistance + "," + maxDistance + "]");
		} else {
			this.minChance = minChance;
			this.maxChance = maxChance;
			this.minDistance = minDistance;
			this.maxDistance = maxDistance;
			this.axis = axis;
		}
	}

	@Override
	public boolean test(BlockPos originalPos, BlockPos currentPos, BlockPos pivot, Random random) {
		Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
		float f = (float)Math.abs((currentPos.getX() - pivot.getX()) * direction.getOffsetX());
		float g = (float)Math.abs((currentPos.getY() - pivot.getY()) * direction.getOffsetY());
		float h = (float)Math.abs((currentPos.getZ() - pivot.getZ()) * direction.getOffsetZ());
		int i = (int)(f + g + h);
		float j = random.nextFloat();
		return j <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress((float)i, (float)this.minDistance, (float)this.maxDistance));
	}

	@Override
	protected PosRuleTestType<?> getType() {
		return PosRuleTestType.AXIS_ALIGNED_LINEAR_POS;
	}
}
