package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_2869 {
	public static final float[] field_13059 = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	protected final class_1937 field_13058;
	private final class_2874 field_13055;
	protected boolean field_13057;
	protected boolean field_13056;
	protected final float[] field_13053 = new float[16];
	private final float[] field_13054 = new float[4];

	public class_2869(class_1937 arg, class_2874 arg2) {
		this.field_13058 = arg;
		this.field_13055 = arg2;
		this.method_12447();
	}

	protected void method_12447() {
		float f = 0.0F;

		for (int i = 0; i <= 15; i++) {
			float g = 1.0F - (float)i / 15.0F;
			this.field_13053[i] = (1.0F - g) / (g * 3.0F + 1.0F) * 1.0F + 0.0F;
		}
	}

	public int method_12454(long l) {
		return (int)(l / 24000L % 8L + 8L) % 8;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public float[] method_12446(float f, float g) {
		float h = 0.4F;
		float i = class_3532.method_15362(f * (float) (Math.PI * 2)) - 0.0F;
		float j = -0.0F;
		if (i >= -0.4F && i <= 0.4F) {
			float k = (i - -0.0F) / 0.4F * 0.5F + 0.5F;
			float l = 1.0F - (1.0F - class_3532.method_15374(k * (float) Math.PI)) * 0.99F;
			l *= l;
			this.field_13054[0] = k * 0.3F + 0.7F;
			this.field_13054[1] = k * k * 0.7F + 0.2F;
			this.field_13054[2] = k * k * 0.0F + 0.2F;
			this.field_13054[3] = l;
			return this.field_13054;
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_12455() {
		return 128.0F;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_12449() {
		return true;
	}

	@Nullable
	public class_2338 method_12466() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public double method_12459() {
		return this.field_13058.method_8401().method_153() == class_1942.field_9277 ? 1.0 : 0.03125;
	}

	public boolean method_12465() {
		return this.field_13057;
	}

	public boolean method_12451() {
		return this.field_13055.method_12491();
	}

	public boolean method_12467() {
		return this.field_13056;
	}

	public float[] method_12456() {
		return this.field_13053;
	}

	public class_2784 method_12463() {
		return new class_2784();
	}

	public void method_12457(class_3222 arg) {
	}

	public void method_12458(class_3222 arg) {
	}

	public void method_12450() {
	}

	public void method_12461() {
	}

	public abstract class_2794<?> method_12443();

	@Nullable
	public abstract class_2338 method_12452(class_1923 arg, boolean bl);

	@Nullable
	public abstract class_2338 method_12444(int i, int j, boolean bl);

	public abstract float method_12464(long l, float f);

	public abstract boolean method_12462();

	@Environment(EnvType.CLIENT)
	public abstract class_243 method_12445(float f, float g);

	public abstract boolean method_12448();

	@Environment(EnvType.CLIENT)
	public abstract boolean method_12453(int i, int j);

	public abstract class_2874 method_12460();
}
