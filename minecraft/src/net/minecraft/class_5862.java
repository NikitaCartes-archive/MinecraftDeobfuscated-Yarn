package net.minecraft;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;

public class class_5862 extends class_5863 {
	public static class_5862 field_29003 = method_33908(0.0F);
	public static final Codec<class_5862> field_29004 = Codec.either(
			Codec.FLOAT,
			RecordCodecBuilder.create(instance -> instance.group(Codec.FLOAT.fieldOf("value").forGetter(arg -> arg.field_29005)).apply(instance, class_5862::new))
		)
		.xmap(either -> either.map(class_5862::method_33908, arg -> arg), arg -> Either.left(arg.field_29005));
	private float field_29005;

	public static class_5862 method_33908(float f) {
		return f == 0.0F ? field_29003 : new class_5862(f);
	}

	private class_5862(float f) {
		this.field_29005 = f;
	}

	public float method_33914() {
		return this.field_29005;
	}

	@Override
	public float method_33920(Random random) {
		return this.field_29005;
	}

	@Override
	public float method_33915() {
		return this.field_29005;
	}

	@Override
	public float method_33921() {
		return this.field_29005 + 1.0F;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_5862 lv = (class_5862)object;
			return this.field_29005 == lv.field_29005;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Float.hashCode(this.field_29005);
	}

	@Override
	public class_5864<?> method_33923() {
		return class_5864.field_29008;
	}

	public String toString() {
		return Float.toString(this.field_29005);
	}
}
