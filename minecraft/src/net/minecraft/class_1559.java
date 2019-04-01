package net.minecraft;

public class class_1559 extends class_1588 {
	private static final class_4051 field_18128 = new class_4051().method_18418(5.0).method_18424();
	private int field_7250;
	private boolean field_7251;

	public class_1559(class_1299<? extends class_1559> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6194 = 3;
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1347(this));
		this.field_6201.method_6277(2, new class_1366(this, 1.0, false));
		this.field_6201.method_6277(3, new class_1394(this, 1.0));
		this.field_6201.method_6277(7, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(8, new class_1376(this));
		this.field_6185.method_6277(1, new class_1399(this).method_6318());
		this.field_6185.method_6277(2, new class_1400(this, class_1657.class, true));
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 0.1F;
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(8.0);
		this.method_5996(class_1612.field_7357).method_6192(0.25);
		this.method_5996(class_1612.field_7363).method_6192(2.0);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15137;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14582;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15230;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_14678, 0.15F, 1.0F);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_7250 = arg.method_10550("Lifetime");
		this.field_7251 = arg.method_10577("PlayerSpawned");
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Lifetime", this.field_7250);
		arg.method_10556("PlayerSpawned", this.field_7251);
	}

	@Override
	public void method_5773() {
		this.field_6283 = this.field_6031;
		super.method_5773();
	}

	@Override
	public void method_5636(float f) {
		this.field_6031 = f;
		super.method_5636(f);
	}

	@Override
	public double method_5678() {
		return 0.1;
	}

	public boolean method_7023() {
		return this.field_7251;
	}

	public void method_7022(boolean bl) {
		this.field_7251 = bl;
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.field_6002.field_9236) {
			for (int i = 0; i < 2; i++) {
				this.field_6002
					.method_8406(
						class_2398.field_11214,
						this.field_5987 + (this.field_5974.nextDouble() - 0.5) * (double)this.method_17681(),
						this.field_6010 + this.field_5974.nextDouble() * (double)this.method_17682(),
						this.field_6035 + (this.field_5974.nextDouble() - 0.5) * (double)this.method_17681(),
						(this.field_5974.nextDouble() - 0.5) * 2.0,
						-this.field_5974.nextDouble(),
						(this.field_5974.nextDouble() - 0.5) * 2.0
					);
			}
		} else {
			if (!this.method_5947()) {
				this.field_7250++;
			}

			if (this.field_7250 >= 2400) {
				this.method_5650();
			}
		}
	}

	@Override
	protected boolean method_7075() {
		return true;
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		if (super.method_5979(arg, arg2)) {
			class_1657 lv = this.field_6002.method_18462(field_18128, this);
			return lv == null;
		} else {
			return false;
		}
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6293;
	}
}
