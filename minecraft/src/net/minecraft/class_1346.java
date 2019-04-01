package net.minecraft;

import java.util.List;

public class class_1346 extends class_1352 {
	private int field_6428;
	private final class_1314 field_6426;
	private class_1309 field_6427;
	private class_1340 field_6425;

	public class_1346(class_1314 arg) {
		this.field_6426 = arg;
	}

	@Override
	public boolean method_6264() {
		List<class_1690> list = this.field_6426.field_6002.method_18467(class_1690.class, this.field_6426.method_5829().method_1014(5.0));
		boolean bl = false;

		for (class_1690 lv : list) {
			if (lv.method_5642() != null
				&& (class_3532.method_15379(((class_1309)lv.method_5642()).field_6212) > 0.0F || class_3532.method_15379(((class_1309)lv.method_5642()).field_6250) > 0.0F)
				)
			 {
				bl = true;
				break;
			}
		}

		return this.field_6427 != null && (class_3532.method_15379(this.field_6427.field_6212) > 0.0F || class_3532.method_15379(this.field_6427.field_6250) > 0.0F)
			|| bl;
	}

	@Override
	public boolean method_6267() {
		return true;
	}

	@Override
	public boolean method_6266() {
		return this.field_6427 != null
			&& this.field_6427.method_5765()
			&& (class_3532.method_15379(this.field_6427.field_6212) > 0.0F || class_3532.method_15379(this.field_6427.field_6250) > 0.0F);
	}

	@Override
	public void method_6269() {
		for (class_1690 lv : this.field_6426.field_6002.method_18467(class_1690.class, this.field_6426.method_5829().method_1014(5.0))) {
			if (lv.method_5642() != null && lv.method_5642() instanceof class_1309) {
				this.field_6427 = (class_1309)lv.method_5642();
				break;
			}
		}

		this.field_6428 = 0;
		this.field_6425 = class_1340.field_6401;
	}

	@Override
	public void method_6270() {
		this.field_6427 = null;
	}

	@Override
	public void method_6268() {
		boolean bl = class_3532.method_15379(this.field_6427.field_6212) > 0.0F || class_3532.method_15379(this.field_6427.field_6250) > 0.0F;
		float f = this.field_6425 == class_1340.field_6400 ? (bl ? 0.17999999F : 0.0F) : 0.135F;
		this.field_6426.method_5724(f, new class_243((double)this.field_6426.field_6212, (double)this.field_6426.field_6227, (double)this.field_6426.field_6250));
		this.field_6426.method_5784(class_1313.field_6308, this.field_6426.method_18798());
		if (--this.field_6428 <= 0) {
			this.field_6428 = 10;
			if (this.field_6425 == class_1340.field_6401) {
				class_2338 lv = new class_2338(this.field_6427).method_10093(this.field_6427.method_5735().method_10153());
				lv = lv.method_10069(0, -1, 0);
				this.field_6426.method_5942().method_6337((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260(), 1.0);
				if (this.field_6426.method_5739(this.field_6427) < 4.0F) {
					this.field_6428 = 0;
					this.field_6425 = class_1340.field_6400;
				}
			} else if (this.field_6425 == class_1340.field_6400) {
				class_2350 lv2 = this.field_6427.method_5755();
				class_2338 lv3 = new class_2338(this.field_6427).method_10079(lv2, 10);
				this.field_6426.method_5942().method_6337((double)lv3.method_10263(), (double)(lv3.method_10264() - 1), (double)lv3.method_10260(), 1.0);
				if (this.field_6426.method_5739(this.field_6427) > 12.0F) {
					this.field_6428 = 0;
					this.field_6425 = class_1340.field_6401;
				}
			}
		}
	}
}
