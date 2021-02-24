package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;

public class class_5861 extends class_5863 {
	public static final Codec<class_5861> field_28998 = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("mean").forGetter(arg -> arg.field_28999),
						Codec.FLOAT.fieldOf("deviation").forGetter(arg -> arg.field_29000),
						Codec.FLOAT.fieldOf("min").forGetter(arg -> arg.field_29001),
						Codec.FLOAT.fieldOf("max").forGetter(arg -> arg.field_29002)
					)
					.apply(instance, class_5861::new)
		)
		.comapFlatMap(
			arg -> arg.field_29002 < arg.field_29001
					? DataResult.error("Max must be larger than min: [" + arg.field_29001 + ", " + arg.field_29002 + "]")
					: DataResult.success(arg),
			Function.identity()
		);
	private float field_28999;
	private float field_29000;
	private float field_29001;
	private float field_29002;

	public static class_5861 method_33900(float f, float g, float h, float i) {
		return new class_5861(f, g, h, i);
	}

	private class_5861(float f, float g, float h, float i) {
		this.field_28999 = f;
		this.field_29000 = g;
		this.field_29001 = h;
		this.field_29002 = i;
	}

	@Override
	public float method_33920(Random random) {
		return method_33903(random, this.field_28999, this.field_29000, this.field_29001, this.field_29002);
	}

	public static float method_33903(Random random, float f, float g, float h, float i) {
		return MathHelper.clamp(MathHelper.nextGaussian(random, f, g), h, i);
	}

	@Override
	public float method_33915() {
		return this.field_29001;
	}

	@Override
	public float method_33921() {
		return this.field_29002;
	}

	@Override
	public class_5864<?> method_33923() {
		return class_5864.field_29010;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_5861 lv = (class_5861)object;
			return this.field_28999 == lv.field_28999 && this.field_29000 == lv.field_29000 && this.field_29001 == lv.field_29001 && this.field_29002 == lv.field_29002;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_28999, this.field_29000, this.field_29001, this.field_29002});
	}

	public String toString() {
		return "normal(" + this.field_28999 + ", " + this.field_29000 + ") in [" + this.field_29001 + '-' + this.field_29002 + ']';
	}
}
