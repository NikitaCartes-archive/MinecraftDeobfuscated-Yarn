package net.minecraft;

import javax.annotation.Nullable;

public class class_1515 extends class_1512 {
	private class_243 field_7041;
	private int field_7040;

	public class_1515(class_1510 arg) {
		super(arg);
	}

	@Override
	public void method_6853() {
		if (this.field_7040++ % 10 == 0) {
			float f = (this.field_7036.method_6051().nextFloat() - 0.5F) * 8.0F;
			float g = (this.field_7036.method_6051().nextFloat() - 0.5F) * 4.0F;
			float h = (this.field_7036.method_6051().nextFloat() - 0.5F) * 8.0F;
			this.field_7036
				.field_6002
				.method_8406(
					class_2398.field_11221,
					this.field_7036.field_5987 + (double)f,
					this.field_7036.field_6010 + 2.0 + (double)g,
					this.field_7036.field_6035 + (double)h,
					0.0,
					0.0,
					0.0
				);
		}
	}

	@Override
	public void method_6855() {
		this.field_7040++;
		if (this.field_7041 == null) {
			class_2338 lv = this.field_7036.field_6002.method_8598(class_2902.class_2903.field_13197, class_3033.field_13600);
			this.field_7041 = new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260());
		}

		double d = this.field_7041.method_1028(this.field_7036.field_5987, this.field_7036.field_6010, this.field_7036.field_6035);
		if (!(d < 100.0) && !(d > 22500.0) && !this.field_7036.field_5976 && !this.field_7036.field_5992) {
			this.field_7036.method_6033(1.0F);
		} else {
			this.field_7036.method_6033(0.0F);
		}
	}

	@Override
	public void method_6856() {
		this.field_7041 = null;
		this.field_7040 = 0;
	}

	@Override
	public float method_6846() {
		return 3.0F;
	}

	@Nullable
	@Override
	public class_243 method_6851() {
		return this.field_7041;
	}

	@Override
	public class_1527<class_1515> method_6849() {
		return class_1527.field_7068;
	}
}
