package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class class_5865 extends class_5863 {
	public static final Codec<class_5865> field_29012 = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("min").forGetter(arg -> arg.field_29013),
						Codec.FLOAT.fieldOf("max").forGetter(arg -> arg.field_29014),
						Codec.FLOAT.fieldOf("plateau").forGetter(arg -> arg.field_29015)
					)
					.apply(instance, class_5865::new)
		)
		.comapFlatMap(
			arg -> {
				if (arg.field_29014 < arg.field_29013) {
					return DataResult.error("Max must be larger than min: [" + arg.field_29013 + ", " + arg.field_29014 + "]");
				} else {
					return arg.field_29015 > arg.field_29014 - arg.field_29013
						? DataResult.error("Plateau can at most be the full span: [" + arg.field_29013 + ", " + arg.field_29014 + "]")
						: DataResult.success(arg);
				}
			},
			Function.identity()
		);
	private float field_29013;
	private float field_29014;
	private float field_29015;

	public static class_5865 method_33926(float f, float g, float h) {
		return new class_5865(f, g, h);
	}

	private class_5865(float f, float g, float h) {
		this.field_29013 = f;
		this.field_29014 = g;
		this.field_29015 = h;
	}

	@Override
	public float method_33920(Random random) {
		float f = this.field_29014 - this.field_29013;
		float g = (f - this.field_29015) / 2.0F;
		float h = f - g;
		return this.field_29013 + random.nextFloat() * h + random.nextFloat() * g;
	}

	@Override
	public float method_33915() {
		return this.field_29013;
	}

	@Override
	public float method_33921() {
		return this.field_29014;
	}

	@Override
	public class_5864<?> method_33923() {
		return class_5864.field_29011;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_5865 lv = (class_5865)object;
			return this.field_29013 == lv.field_29013 && this.field_29014 == lv.field_29014 && this.field_29015 == lv.field_29015;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_29013, this.field_29014, this.field_29015});
	}

	public String toString() {
		return "trapezoid(" + this.field_29015 + ") in [" + this.field_29013 + '-' + this.field_29014 + ']';
	}
}
