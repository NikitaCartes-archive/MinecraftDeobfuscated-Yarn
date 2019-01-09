package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_502 extends class_503.class_504 {
	private final class_500 field_3072;
	protected final class_310 field_3073;
	protected final class_1131 field_3070;
	private long field_3071;

	protected class_502(class_500 arg, class_1131 arg2) {
		this.field_3072 = arg;
		this.field_3070 = arg2;
		this.field_3073 = class_310.method_1551();
	}

	@Override
	public void method_1903(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_1907();
		int n = this.method_1906();
		this.field_3073.field_1772.method_1729(class_1074.method_4662("lanServer.title"), (float)(m + 32 + 3), (float)(n + 1), 16777215);
		this.field_3073.field_1772.method_1729(this.field_3070.method_4813(), (float)(m + 32 + 3), (float)(n + 12), 8421504);
		if (this.field_3073.field_1690.field_1815) {
			this.field_3073.field_1772.method_1729(class_1074.method_4662("selectServer.hiddenAddress"), (float)(m + 32 + 3), (float)(n + 12 + 11), 3158064);
		} else {
			this.field_3073.field_1772.method_1729(this.field_3070.method_4812(), (float)(m + 32 + 3), (float)(n + 12 + 11), 3158064);
		}
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		this.field_3072.method_2544(this.method_1908());
		if (class_156.method_658() - this.field_3071 < 250L) {
			this.field_3072.method_2536();
		}

		this.field_3071 = class_156.method_658();
		return false;
	}

	public class_1131 method_2559() {
		return this.field_3070;
	}
}
