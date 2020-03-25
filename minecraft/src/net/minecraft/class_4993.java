package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class class_4993 extends class_4995 {
	private final float field_23339;
	private final float field_23340;
	private final int field_23341;
	private final int field_23342;

	public class_4993(float f, float g, int i, int j) {
		if (i >= j) {
			throw new IllegalArgumentException("Invalid range: [" + i + "," + j + "]");
		} else {
			this.field_23339 = f;
			this.field_23340 = g;
			this.field_23341 = i;
			this.field_23342 = j;
		}
	}

	public <T> class_4993(Dynamic<T> dynamic) {
		this(dynamic.get("min_chance").asFloat(0.0F), dynamic.get("max_chance").asFloat(0.0F), dynamic.get("min_dist").asInt(0), dynamic.get("max_dist").asInt(0));
	}

	@Override
	public boolean method_26406(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
		int i = blockPos2.getManhattanDistance(blockPos3);
		float f = random.nextFloat();
		return (double)f
			<= MathHelper.clampedLerp(
				(double)this.field_23339, (double)this.field_23340, MathHelper.getLerpProgress((double)i, (double)this.field_23341, (double)this.field_23342)
			);
	}

	@Override
	protected class_4996 method_26404() {
		return class_4996.field_23345;
	}

	@Override
	protected <T> Dynamic<T> method_26405(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("min_chance"),
					dynamicOps.createFloat(this.field_23339),
					dynamicOps.createString("max_chance"),
					dynamicOps.createFloat(this.field_23340),
					dynamicOps.createString("min_dist"),
					dynamicOps.createFloat((float)this.field_23341),
					dynamicOps.createString("max_dist"),
					dynamicOps.createFloat((float)this.field_23342)
				)
			)
		);
	}
}
