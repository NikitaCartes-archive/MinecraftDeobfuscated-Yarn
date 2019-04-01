package net.minecraft;

public class class_1428 extends class_1429 {
	private static final class_1856 field_6742 = class_1856.method_8091(class_1802.field_8317, class_1802.field_8188, class_1802.field_8706, class_1802.field_8309);
	public float field_6741;
	public float field_6743;
	public float field_6738;
	public float field_6736;
	public float field_6737 = 1.0F;
	public int field_6739 = this.field_5974.nextInt(6000) + 6000;
	public boolean field_6740;

	public class_1428(class_1299<? extends class_1428> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_5941(class_7.field_18, 0.0F);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1374(this, 1.4));
		this.field_6201.method_6277(2, new class_1341(this, 1.0));
		this.field_6201.method_6277(3, new class_1391(this, 1.0, false, field_6742));
		this.field_6201.method_6277(4, new class_1353(this, 1.1));
		this.field_6201.method_6277(5, new class_1394(this, 1.0));
		this.field_6201.method_6277(6, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(7, new class_1376(this));
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return arg2.field_18068;
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(4.0);
		this.method_5996(class_1612.field_7357).method_6192(0.25);
	}

	@Override
	public void method_6007() {
		super.method_6007();
		this.field_6736 = this.field_6741;
		this.field_6738 = this.field_6743;
		this.field_6743 = (float)((double)this.field_6743 + (double)(this.field_5952 ? -1 : 4) * 0.3);
		this.field_6743 = class_3532.method_15363(this.field_6743, 0.0F, 1.0F);
		if (!this.field_5952 && this.field_6737 < 1.0F) {
			this.field_6737 = 1.0F;
		}

		this.field_6737 = (float)((double)this.field_6737 * 0.9);
		class_243 lv = this.method_18798();
		if (!this.field_5952 && lv.field_1351 < 0.0) {
			this.method_18799(lv.method_18805(1.0, 0.6, 1.0));
		}

		this.field_6741 = this.field_6741 + this.field_6737 * 2.0F;
		if (!this.field_6002.field_9236 && this.method_5805() && !this.method_6109() && !this.method_6472() && --this.field_6739 <= 0) {
			this.method_5783(class_3417.field_15219, 1.0F, (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F);
			this.method_5706(class_1802.field_8803);
			this.field_6739 = this.field_5974.nextInt(6000) + 6000;
		}
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14871;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14601;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15140;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_14685, 0.15F, 1.0F);
	}

	public class_1428 method_6471(class_1296 arg) {
		return class_1299.field_6132.method_5883(this.field_6002);
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return field_6742.method_8093(arg);
	}

	@Override
	protected int method_6110(class_1657 arg) {
		return this.method_6472() ? 10 : super.method_6110(arg);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_6740 = arg.method_10577("IsChickenJockey");
		if (arg.method_10545("EggLayTime")) {
			this.field_6739 = arg.method_10550("EggLayTime");
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("IsChickenJockey", this.field_6740);
		arg.method_10569("EggLayTime", this.field_6739);
	}

	@Override
	public boolean method_5974(double d) {
		return this.method_6472() && !this.method_5782();
	}

	@Override
	public void method_5865(class_1297 arg) {
		super.method_5865(arg);
		float f = class_3532.method_15374(this.field_6283 * (float) (Math.PI / 180.0));
		float g = class_3532.method_15362(this.field_6283 * (float) (Math.PI / 180.0));
		float h = 0.1F;
		float i = 0.0F;
		arg.method_5814(
			this.field_5987 + (double)(0.1F * f), this.field_6010 + (double)(this.method_17682() * 0.5F) + arg.method_5678() + 0.0, this.field_6035 - (double)(0.1F * g)
		);
		if (arg instanceof class_1309) {
			((class_1309)arg).field_6283 = this.field_6283;
		}
	}

	public boolean method_6472() {
		return this.field_6740;
	}

	public void method_6473(boolean bl) {
		this.field_6740 = bl;
	}
}
