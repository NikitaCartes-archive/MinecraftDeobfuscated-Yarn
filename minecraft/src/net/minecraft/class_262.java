package net.minecraft;

final class class_262 extends class_251 {
	private final class_251 field_1393;
	private final int field_1392;
	private final int field_1391;
	private final int field_1390;
	private final int field_1389;
	private final int field_1388;
	private final int field_1394;

	public class_262(class_251 arg, int i, int j, int k, int l, int m, int n) {
		super(l - i, m - j, n - k);
		this.field_1393 = arg;
		this.field_1392 = i;
		this.field_1391 = j;
		this.field_1390 = k;
		this.field_1389 = l;
		this.field_1388 = m;
		this.field_1394 = n;
	}

	@Override
	public boolean method_1063(int i, int j, int k) {
		return this.field_1393.method_1063(this.field_1392 + i, this.field_1391 + j, this.field_1390 + k);
	}

	@Override
	public void method_1049(int i, int j, int k, boolean bl, boolean bl2) {
		this.field_1393.method_1049(this.field_1392 + i, this.field_1391 + j, this.field_1390 + k, bl, bl2);
	}

	@Override
	public int method_1055(class_2350.class_2351 arg) {
		return Math.max(0, this.field_1393.method_1055(arg) - arg.method_10173(this.field_1392, this.field_1391, this.field_1390));
	}

	@Override
	public int method_1045(class_2350.class_2351 arg) {
		return Math.min(
			arg.method_10173(this.field_1389, this.field_1388, this.field_1394),
			this.field_1393.method_1045(arg) - arg.method_10173(this.field_1392, this.field_1391, this.field_1390)
		);
	}
}
