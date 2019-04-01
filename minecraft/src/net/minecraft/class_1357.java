package net.minecraft;

public class class_1357 extends class_4017 {
	private static final int[] field_6474 = new int[]{0, 1, 4, 5, 6, 7};
	private final class_1433 field_6471;
	private final int field_6472;
	private boolean field_6473;

	public class_1357(class_1433 arg, int i) {
		this.field_6471 = arg;
		this.field_6472 = i;
	}

	@Override
	public boolean method_6264() {
		if (this.field_6471.method_6051().nextInt(this.field_6472) != 0) {
			return false;
		} else {
			class_2350 lv = this.field_6471.method_5755();
			int i = lv.method_10148();
			int j = lv.method_10165();
			class_2338 lv2 = new class_2338(this.field_6471);

			for (int k : field_6474) {
				if (!this.method_6284(lv2, i, j, k) || !this.method_6282(lv2, i, j, k)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean method_6284(class_2338 arg, int i, int j, int k) {
		class_2338 lv = arg.method_10069(i * k, 0, j * k);
		return this.field_6471.field_6002.method_8316(lv).method_15767(class_3486.field_15517)
			&& !this.field_6471.field_6002.method_8320(lv).method_11620().method_15801();
	}

	private boolean method_6282(class_2338 arg, int i, int j, int k) {
		return this.field_6471.field_6002.method_8320(arg.method_10069(i * k, 1, j * k)).method_11588()
			&& this.field_6471.field_6002.method_8320(arg.method_10069(i * k, 2, j * k)).method_11588();
	}

	@Override
	public boolean method_6266() {
		double d = this.field_6471.method_18798().field_1351;
		return (!(d * d < 0.03F) || this.field_6471.field_5965 == 0.0F || !(Math.abs(this.field_6471.field_5965) < 10.0F) || !this.field_6471.method_5799())
			&& !this.field_6471.field_5952;
	}

	@Override
	public boolean method_6267() {
		return false;
	}

	@Override
	public void method_6269() {
		class_2350 lv = this.field_6471.method_5755();
		this.field_6471.method_18799(this.field_6471.method_18798().method_1031((double)lv.method_10148() * 0.6, 0.7, (double)lv.method_10165() * 0.6));
		this.field_6471.method_5942().method_6340();
	}

	@Override
	public void method_6270() {
		this.field_6471.field_5965 = 0.0F;
	}

	@Override
	public void method_6268() {
		boolean bl = this.field_6473;
		if (!bl) {
			class_3610 lv = this.field_6471.field_6002.method_8316(new class_2338(this.field_6471));
			this.field_6473 = lv.method_15767(class_3486.field_15517);
		}

		if (this.field_6473 && !bl) {
			this.field_6471.method_5783(class_3417.field_14707, 1.0F, 1.0F);
		}

		class_243 lv2 = this.field_6471.method_18798();
		if (lv2.field_1351 * lv2.field_1351 < 0.03F && this.field_6471.field_5965 != 0.0F) {
			this.field_6471.field_5965 = this.method_18251(this.field_6471.field_5965, 0.0F, 0.2F);
		} else {
			double d = Math.sqrt(class_1297.method_17996(lv2));
			double e = Math.signum(-lv2.field_1351) * Math.acos(d / lv2.method_1033()) * 180.0F / (float)Math.PI;
			this.field_6471.field_5965 = (float)e;
		}
	}
}
