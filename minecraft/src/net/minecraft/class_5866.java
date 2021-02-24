package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;

public class class_5866 extends class_5863 {
	public static final Codec<class_5866> field_29016 = RecordCodecBuilder.create(
			instance -> instance.group(Codec.FLOAT.fieldOf("base").forGetter(arg -> arg.field_29017), Codec.FLOAT.fieldOf("spread").forGetter(arg -> arg.field_29018))
					.apply(instance, class_5866::new)
		)
		.comapFlatMap(
			arg -> arg.field_29018 < 0.0F ? DataResult.error("Spread must be non-negative, got: " + arg.field_29018) : DataResult.success(arg), Function.identity()
		);
	private final float field_29017;
	private final float field_29018;

	private class_5866(float f, float g) {
		this.field_29017 = f;
		this.field_29018 = g;
	}

	public static class_5866 method_33934(float f, float g) {
		return new class_5866(f, g);
	}

	@Override
	public float method_33920(Random random) {
		return this.field_29018 == 0.0F ? this.field_29017 : MathHelper.nextBetween(random, this.field_29017, this.field_29017 + this.field_29018);
	}

	@Override
	public float method_33915() {
		return this.field_29017;
	}

	@Override
	public float method_33921() {
		return this.field_29017 + this.field_29018;
	}

	@Override
	public class_5864<?> method_33923() {
		return class_5864.field_29009;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_5866 lv = (class_5866)object;
			return this.field_29017 == lv.field_29017 && this.field_29018 == lv.field_29018;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_29017, this.field_29018});
	}

	public String toString() {
		return "[" + this.field_29017 + '-' + (this.field_29017 + this.field_29018) + ']';
	}
}
