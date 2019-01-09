package net.minecraft;

public class class_3721 extends class_2586 implements class_3000 {
	public int field_17095;
	public boolean field_17096;
	public class_2350 field_17097;

	public class_3721() {
		super(class_2591.field_16413);
	}

	@Override
	public boolean method_11004(int i, int j) {
		if (i == 1) {
			this.field_17097 = class_2350.method_10143(j);
			this.field_17095 = 0;
			this.field_17096 = true;
			return true;
		} else {
			return super.method_11004(i, j);
		}
	}

	@Override
	public void method_16896() {
		if (this.field_17096) {
			this.field_17095++;
		}

		if (this.field_17095 >= 50) {
			this.field_17096 = false;
			this.field_17095 = 0;
		}
	}

	public void method_17031(class_2350 arg) {
		this.field_17097 = arg;
		if (this.field_17096) {
			this.field_17095 = 0;
		} else {
			this.field_17096 = true;
		}

		if (!this.field_11863.field_9236) {
			this.field_11863.method_8427(this.method_11016(), this.method_11010().method_11614(), 1, arg.method_10146());
		}
	}
}
