package net.minecraft;

import javax.annotation.Nullable;

public class class_1473 extends class_1427 implements class_1603 {
	private static final class_2940<Byte> field_6873 = class_2945.method_12791(class_1473.class, class_2943.field_13319);

	public class_1473(class_1299<? extends class_1473> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1381(this, 1.25, 20, 10.0F));
		this.field_6201.method_6277(2, new class_1394(this, 1.0, 1.0000001E-5F));
		this.field_6201.method_6277(3, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(4, new class_1376(this));
		this.field_6185.method_6277(1, new class_1400(this, class_1308.class, 10, true, false, arg -> arg instanceof class_1569));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(4.0);
		this.method_5996(class_1612.field_7357).method_6192(0.2F);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6873, (byte)16);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("Pumpkin", this.method_6643());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10545("Pumpkin")) {
			this.method_6642(arg.method_10577("Pumpkin"));
		}
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (!this.field_6002.field_9236) {
			int i = class_3532.method_15357(this.field_5987);
			int j = class_3532.method_15357(this.field_6010);
			int k = class_3532.method_15357(this.field_6035);
			if (this.method_5637()) {
				this.method_5643(class_1282.field_5859, 1.0F);
			}

			if (this.field_6002.method_8310(new class_2338(i, 0, k)).method_8707(new class_2338(i, j, k)) > 1.0F) {
				this.method_5643(class_1282.field_5854, 1.0F);
			}

			if (!this.field_6002.method_8450().method_8355(class_1928.field_19388)) {
				return;
			}

			class_2680 lv = class_2246.field_10477.method_9564();

			for (int l = 0; l < 4; l++) {
				i = class_3532.method_15357(this.field_5987 + (double)((float)(l % 2 * 2 - 1) * 0.25F));
				j = class_3532.method_15357(this.field_6010);
				k = class_3532.method_15357(this.field_6035 + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
				class_2338 lv2 = new class_2338(i, j, k);
				if (this.field_6002.method_8320(lv2).method_11588() && this.field_6002.method_8310(lv2).method_8707(lv2) < 0.8F && lv.method_11591(this.field_6002, lv2)) {
					this.field_6002.method_8501(lv2, lv);
				}
			}
		}
	}

	@Override
	public void method_7105(class_1309 arg, float f) {
		class_1680 lv = new class_1680(this.field_6002, this);
		double d = arg.field_6010 + (double)arg.method_5751() - 1.1F;
		double e = arg.field_5987 - this.field_5987;
		double g = d - lv.field_6010;
		double h = arg.field_6035 - this.field_6035;
		float i = class_3532.method_15368(e * e + h * h) * 0.2F;
		lv.method_7485(e, g + (double)i, h, 1.6F, 12.0F);
		this.method_5783(class_3417.field_14745, 1.0F, 1.0F / (this.method_6051().nextFloat() * 0.4F + 0.8F));
		this.field_6002.method_8649(lv);
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 1.7F;
	}

	@Override
	protected boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() == class_1802.field_8868 && this.method_6643() && !this.field_6002.field_9236) {
			this.method_6642(false);
			lv.method_7956(1, arg, arg2x -> arg2x.method_20236(arg2));
		}

		return super.method_5992(arg, arg2);
	}

	public boolean method_6643() {
		return (this.field_6011.method_12789(field_6873) & 16) != 0;
	}

	public void method_6642(boolean bl) {
		byte b = this.field_6011.method_12789(field_6873);
		if (bl) {
			this.field_6011.method_12778(field_6873, (byte)(b | 16));
		} else {
			this.field_6011.method_12778(field_6873, (byte)(b & -17));
		}
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14655;
	}

	@Nullable
	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14830;
	}

	@Nullable
	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14594;
	}
}
