package net.minecraft;

public class class_2379 {
	protected final float field_11165;
	protected final float field_11164;
	protected final float field_11163;

	public class_2379(float f, float g, float h) {
		this.field_11165 = !Float.isInfinite(f) && !Float.isNaN(f) ? f % 360.0F : 0.0F;
		this.field_11164 = !Float.isInfinite(g) && !Float.isNaN(g) ? g % 360.0F : 0.0F;
		this.field_11163 = !Float.isInfinite(h) && !Float.isNaN(h) ? h % 360.0F : 0.0F;
	}

	public class_2379(class_2499 arg) {
		this(arg.method_10604(0), arg.method_10604(1), arg.method_10604(2));
	}

	public class_2499 method_10255() {
		class_2499 lv = new class_2499();
		lv.method_10606(new class_2494(this.field_11165));
		lv.method_10606(new class_2494(this.field_11164));
		lv.method_10606(new class_2494(this.field_11163));
		return lv;
	}

	public boolean equals(Object object) {
		if (!(object instanceof class_2379)) {
			return false;
		} else {
			class_2379 lv = (class_2379)object;
			return this.field_11165 == lv.field_11165 && this.field_11164 == lv.field_11164 && this.field_11163 == lv.field_11163;
		}
	}

	public float method_10256() {
		return this.field_11165;
	}

	public float method_10257() {
		return this.field_11164;
	}

	public float method_10258() {
		return this.field_11163;
	}
}
