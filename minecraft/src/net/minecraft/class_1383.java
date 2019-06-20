package net.minecraft;

import java.util.EnumSet;

public class class_1383<T extends class_1588 & class_1603 & class_3745> extends class_1352 {
	private final T field_6593;
	private class_1383.class_3744 field_16528 = class_1383.class_3744.field_16534;
	private final double field_6590;
	private final float field_6591;
	private int field_6592;
	private int field_16529;

	public class_1383(T arg, double d, float f) {
		this.field_6593 = arg;
		this.field_6590 = d;
		this.field_6591 = f * f;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
	}

	@Override
	public boolean method_6264() {
		return this.method_19996() && this.method_6310();
	}

	private boolean method_6310() {
		return this.field_6593.method_18809(class_1802.field_8399);
	}

	@Override
	public boolean method_6266() {
		return this.method_19996() && (this.method_6264() || !this.field_6593.method_5942().method_6357()) && this.method_6310();
	}

	private boolean method_19996() {
		return this.field_6593.method_5968() != null && this.field_6593.method_5968().method_5805();
	}

	@Override
	public void method_6270() {
		super.method_6270();
		this.field_6593.method_19540(false);
		this.field_6593.method_5980(null);
		this.field_6592 = 0;
		if (this.field_6593.method_6115()) {
			this.field_6593.method_6021();
			this.field_6593.method_7110(false);
			class_1764.method_7782(this.field_6593.method_6030(), false);
		}
	}

	@Override
	public void method_6268() {
		class_1309 lv = this.field_6593.method_5968();
		if (lv != null) {
			boolean bl = this.field_6593.method_5985().method_6369(lv);
			boolean bl2 = this.field_6592 > 0;
			if (bl != bl2) {
				this.field_6592 = 0;
			}

			if (bl) {
				this.field_6592++;
			} else {
				this.field_6592--;
			}

			double d = this.field_6593.method_5858(lv);
			boolean bl3 = (d > (double)this.field_6591 || this.field_6592 < 5) && this.field_16529 == 0;
			if (bl3) {
				this.field_6593.method_5942().method_6335(lv, this.method_16352() ? this.field_6590 : this.field_6590 * 0.5);
			} else {
				this.field_6593.method_5942().method_6340();
			}

			this.field_6593.method_5988().method_6226(lv, 30.0F, 30.0F);
			if (this.field_16528 == class_1383.class_3744.field_16534) {
				if (!bl3) {
					this.field_6593.method_6019(class_1675.method_18812(this.field_6593, class_1802.field_8399));
					this.field_16528 = class_1383.class_3744.field_16530;
					this.field_6593.method_7110(true);
				}
			} else if (this.field_16528 == class_1383.class_3744.field_16530) {
				if (!this.field_6593.method_6115()) {
					this.field_16528 = class_1383.class_3744.field_16534;
				}

				int i = this.field_6593.method_6048();
				class_1799 lv2 = this.field_6593.method_6030();
				if (i >= class_1764.method_7775(lv2)) {
					this.field_6593.method_6075();
					this.field_16528 = class_1383.class_3744.field_16532;
					this.field_16529 = 20 + this.field_6593.method_6051().nextInt(20);
					this.field_6593.method_7110(false);
				}
			} else if (this.field_16528 == class_1383.class_3744.field_16532) {
				this.field_16529--;
				if (this.field_16529 == 0) {
					this.field_16528 = class_1383.class_3744.field_16533;
				}
			} else if (this.field_16528 == class_1383.class_3744.field_16533 && bl) {
				this.field_6593.method_7105(lv, 1.0F);
				class_1799 lv3 = this.field_6593.method_5998(class_1675.method_18812(this.field_6593, class_1802.field_8399));
				class_1764.method_7782(lv3, false);
				this.field_16528 = class_1383.class_3744.field_16534;
			}
		}
	}

	private boolean method_16352() {
		return this.field_16528 == class_1383.class_3744.field_16534;
	}

	static enum class_3744 {
		field_16534,
		field_16530,
		field_16532,
		field_16533;
	}
}
