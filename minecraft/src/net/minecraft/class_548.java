package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_548 extends class_572<class_1531> {
	public class_548() {
		this(0.0F);
	}

	public class_548(float f) {
		this(f, 64, 32);
	}

	protected class_548(float f, int i, int j) {
		super(f, 0.0F, i, j);
	}

	public void method_17066(class_1531 arg, float f, float g, float h, float i, float j, float k) {
		this.field_3398.field_3654 = (float) (Math.PI / 180.0) * arg.method_6921().method_10256();
		this.field_3398.field_3675 = (float) (Math.PI / 180.0) * arg.method_6921().method_10257();
		this.field_3398.field_3674 = (float) (Math.PI / 180.0) * arg.method_6921().method_10258();
		this.field_3398.method_2851(0.0F, 1.0F, 0.0F);
		this.field_3391.field_3654 = (float) (Math.PI / 180.0) * arg.method_6923().method_10256();
		this.field_3391.field_3675 = (float) (Math.PI / 180.0) * arg.method_6923().method_10257();
		this.field_3391.field_3674 = (float) (Math.PI / 180.0) * arg.method_6923().method_10258();
		this.field_3390.field_3654 = (float) (Math.PI / 180.0) * arg.method_6930().method_10256();
		this.field_3390.field_3675 = (float) (Math.PI / 180.0) * arg.method_6930().method_10257();
		this.field_3390.field_3674 = (float) (Math.PI / 180.0) * arg.method_6930().method_10258();
		this.field_3401.field_3654 = (float) (Math.PI / 180.0) * arg.method_6903().method_10256();
		this.field_3401.field_3675 = (float) (Math.PI / 180.0) * arg.method_6903().method_10257();
		this.field_3401.field_3674 = (float) (Math.PI / 180.0) * arg.method_6903().method_10258();
		this.field_3397.field_3654 = (float) (Math.PI / 180.0) * arg.method_6917().method_10256();
		this.field_3397.field_3675 = (float) (Math.PI / 180.0) * arg.method_6917().method_10257();
		this.field_3397.field_3674 = (float) (Math.PI / 180.0) * arg.method_6917().method_10258();
		this.field_3397.method_2851(1.9F, 11.0F, 0.0F);
		this.field_3392.field_3654 = (float) (Math.PI / 180.0) * arg.method_6900().method_10256();
		this.field_3392.field_3675 = (float) (Math.PI / 180.0) * arg.method_6900().method_10257();
		this.field_3392.field_3674 = (float) (Math.PI / 180.0) * arg.method_6900().method_10258();
		this.field_3392.method_2851(-1.9F, 11.0F, 0.0F);
		this.field_3394.method_17138(this.field_3398);
	}
}
