package net.minecraft;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;

public class class_4018 extends class_1352 {
	private final class_1314 field_17944;
	private final int field_17945;
	@Nullable
	private class_2338 field_17947;

	public class_4018(class_1314 arg, int i) {
		this.field_17944 = arg;
		this.field_17945 = i;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
	}

	@Override
	public boolean method_6264() {
		if (this.field_17944.method_5782()) {
			return false;
		} else if (this.field_17944.field_6002.method_8530()) {
			return false;
		} else if (this.field_17944.method_6051().nextInt(this.field_17945) != 0) {
			return false;
		} else {
			class_3218 lv = (class_3218)this.field_17944.field_6002;
			class_2338 lv2 = new class_2338(this.field_17944);
			if (!lv.method_19497(lv2, 4)) {
				return false;
			} else {
				class_243 lv3 = class_1414.method_19108(this.field_17944, 15, 7, arg2 -> (double)(-lv.method_19498(class_4076.method_18682(arg2))));
				this.field_17947 = lv3 == null ? null : new class_2338(lv3);
				return this.field_17947 != null;
			}
		}
	}

	@Override
	public boolean method_6266() {
		return this.field_17947 != null && !this.field_17944.method_5942().method_6357() && this.field_17944.method_5942().method_6355().equals(this.field_17947);
	}

	@Override
	public void method_6268() {
		if (this.field_17947 != null) {
			class_1408 lv = this.field_17944.method_5942();
			if (lv.method_6357() && !this.field_17947.method_19769(this.field_17944.method_19538(), 10.0)) {
				class_243 lv2 = new class_243(this.field_17947);
				class_243 lv3 = new class_243(this.field_17944.field_5987, this.field_17944.field_6010, this.field_17944.field_6035);
				class_243 lv4 = lv3.method_1020(lv2);
				lv2 = lv4.method_1021(0.4).method_1019(lv2);
				class_243 lv5 = lv2.method_1020(lv3).method_1029().method_1021(10.0).method_1019(lv3);
				class_2338 lv6 = new class_2338((int)lv5.field_1352, (int)lv5.field_1351, (int)lv5.field_1350);
				lv6 = this.field_17944.field_6002.method_8598(class_2902.class_2903.field_13203, lv6);
				if (!lv.method_6337((double)lv6.method_10263(), (double)lv6.method_10264(), (double)lv6.method_10260(), 1.0)) {
					this.method_18252();
				}
			}
		}
	}

	private void method_18252() {
		Random random = this.field_17944.method_6051();
		class_2338 lv = this.field_17944
			.field_6002
			.method_8598(class_2902.class_2903.field_13203, new class_2338(this.field_17944).method_10069(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.field_17944.method_5942().method_6337((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260(), 1.0);
	}
}
