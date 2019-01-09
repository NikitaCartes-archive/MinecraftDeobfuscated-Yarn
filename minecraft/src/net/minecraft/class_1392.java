package net.minecraft;

public class class_1392 extends class_1358 {
	private int field_6624;
	private final class_1646 field_6623;

	public class_1392(class_1646 arg) {
		super(arg, class_1646.class, 3.0F, 0.02F);
		this.field_6623 = arg;
	}

	@Override
	public void method_6269() {
		super.method_6269();
		if (this.field_6623.method_7234() && this.field_6484 instanceof class_1646 && ((class_1646)this.field_6484).method_7239()) {
			this.field_6624 = 10;
		} else {
			this.field_6624 = 0;
		}
	}

	@Override
	public void method_6268() {
		super.method_6268();
		if (this.field_6624 > 0) {
			this.field_6624--;
			if (this.field_6624 == 0) {
				class_1277 lv = this.field_6623.method_7242();

				for (int i = 0; i < lv.method_5439(); i++) {
					class_1799 lv2 = lv.method_5438(i);
					class_1799 lv3 = class_1799.field_8037;
					if (!lv2.method_7960()) {
						class_1792 lv4 = lv2.method_7909();
						if ((lv4 == class_1802.field_8229 || lv4 == class_1802.field_8567 || lv4 == class_1802.field_8179 || lv4 == class_1802.field_8186)
							&& lv2.method_7947() > 3) {
							int j = lv2.method_7947() / 2;
							lv2.method_7934(j);
							lv3 = new class_1799(lv4, j);
						} else if (lv4 == class_1802.field_8861 && lv2.method_7947() > 5) {
							int j = lv2.method_7947() / 2 / 3 * 3;
							int k = j / 3;
							lv2.method_7934(j);
							lv3 = new class_1799(class_1802.field_8229, k);
						}

						if (lv2.method_7960()) {
							lv.method_5447(i, class_1799.field_8037);
						}
					}

					if (!lv3.method_7960()) {
						double d = this.field_6623.field_6010 - 0.3F + (double)this.field_6623.method_5751();
						class_1542 lv5 = new class_1542(this.field_6623.field_6002, this.field_6623.field_5987, d, this.field_6623.field_6035, lv3);
						float f = 0.3F;
						float g = this.field_6623.field_6241;
						float h = this.field_6623.field_5965;
						lv5.field_5967 = (double)(-class_3532.method_15374(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(h * (float) (Math.PI / 180.0)) * 0.3F);
						lv5.field_6006 = (double)(class_3532.method_15362(g * (float) (Math.PI / 180.0)) * class_3532.method_15362(h * (float) (Math.PI / 180.0)) * 0.3F);
						lv5.field_5984 = (double)(-class_3532.method_15374(h * (float) (Math.PI / 180.0)) * 0.3F + 0.1F);
						lv5.method_6988();
						this.field_6623.field_6002.method_8649(lv5);
						break;
					}
				}
			}
		}
	}
}
