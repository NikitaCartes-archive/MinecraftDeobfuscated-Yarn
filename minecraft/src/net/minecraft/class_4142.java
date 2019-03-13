package net.minecraft;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_4142 {
	private final class_4115 field_18460;
	private final float field_18461;
	private final int field_18462;

	public class_4142(BlockPos blockPos, float f, int i) {
		this(new class_4099(blockPos), f, i);
	}

	public class_4142(Vec3d vec3d, float f, int i) {
		this(new class_4099(new BlockPos(vec3d)), f, i);
	}

	public class_4142(class_4115 arg, float f, int i) {
		this.field_18460 = arg;
		this.field_18461 = f;
		this.field_18462 = i;
	}

	public class_4115 method_19094() {
		return this.field_18460;
	}

	public float method_19095() {
		return this.field_18461;
	}

	public int method_19096() {
		return this.field_18462;
	}
}
