package net.minecraft;

public class class_1380<T extends class_1588 & class_1603> extends class_1352 {
	private final T field_6576;
	private final double field_6569;
	private int field_6575;
	private final float field_6570;
	private int field_6574 = -1;
	private int field_6572;
	private boolean field_6573;
	private boolean field_6571;
	private int field_6568 = -1;

	public class_1380(T arg, double d, int i, float f) {
		this.field_6576 = arg;
		this.field_6569 = d;
		this.field_6575 = i;
		this.field_6570 = f * f;
		this.method_6265(3);
	}

	public void method_6305(int i) {
		this.field_6575 = i;
	}

	@Override
	public boolean method_6264() {
		return this.field_6576.method_5968() == null ? false : this.method_6306();
	}

	protected boolean method_6306() {
		return !this.field_6576.method_6047().method_7960() && this.field_6576.method_6047().method_7909() == class_1802.field_8102;
	}

	@Override
	public boolean method_6266() {
		return (this.method_6264() || !this.field_6576.method_5942().method_6357()) && this.method_6306();
	}

	@Override
	public void method_6269() {
		super.method_6269();
		this.field_6576.method_7106(true);
	}

	@Override
	public void method_6270() {
		super.method_6270();
		this.field_6576.method_7106(false);
		this.field_6572 = 0;
		this.field_6574 = -1;
		this.field_6576.method_6021();
	}

	@Override
	public void method_6268() {
		class_1309 lv = this.field_6576.method_5968();
		if (lv != null) {
			double d = this.field_6576.method_5649(lv.field_5987, lv.method_5829().field_1322, lv.field_6035);
			boolean bl = this.field_6576.method_5985().method_6369(lv);
			boolean bl2 = this.field_6572 > 0;
			if (bl != bl2) {
				this.field_6572 = 0;
			}

			if (bl) {
				this.field_6572++;
			} else {
				this.field_6572--;
			}

			if (!(d > (double)this.field_6570) && this.field_6572 >= 20) {
				this.field_6576.method_5942().method_6340();
				this.field_6568++;
			} else {
				this.field_6576.method_5942().method_6335(lv, this.field_6569);
				this.field_6568 = -1;
			}

			if (this.field_6568 >= 20) {
				if ((double)this.field_6576.method_6051().nextFloat() < 0.3) {
					this.field_6573 = !this.field_6573;
				}

				if ((double)this.field_6576.method_6051().nextFloat() < 0.3) {
					this.field_6571 = !this.field_6571;
				}

				this.field_6568 = 0;
			}

			if (this.field_6568 > -1) {
				if (d > (double)(this.field_6570 * 0.75F)) {
					this.field_6571 = false;
				} else if (d < (double)(this.field_6570 * 0.25F)) {
					this.field_6571 = true;
				}

				this.field_6576.method_5962().method_6243(this.field_6571 ? -0.5F : 0.5F, this.field_6573 ? 0.5F : -0.5F);
				this.field_6576.method_5951(lv, 30.0F, 30.0F);
			} else {
				this.field_6576.method_5988().method_6226(lv, 30.0F, 30.0F);
			}

			if (this.field_6576.method_6115()) {
				if (!bl && this.field_6572 < -60) {
					this.field_6576.method_6021();
				} else if (bl) {
					int i = this.field_6576.method_6048();
					if (i >= 20) {
						this.field_6576.method_6021();
						this.field_6576.method_7105(lv, class_1753.method_7722(i));
						this.field_6574 = this.field_6575;
					}
				}
			} else if (--this.field_6574 <= 0 && this.field_6572 >= -60) {
				this.field_6576.method_6019(class_1268.field_5808);
			}
		}
	}
}
