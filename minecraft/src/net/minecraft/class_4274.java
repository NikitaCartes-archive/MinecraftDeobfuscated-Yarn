package net.minecraft;

import java.util.List;
import java.util.Random;

public class class_4274 {
	private int field_19171;

	public int method_20261(class_3218 arg, boolean bl, boolean bl2) {
		if (bl2 && arg.method_8450().method_8355(class_1928.field_19390)) {
			this.field_19171--;
			if (this.field_19171 > 0) {
				return 0;
			} else {
				this.field_19171 = 1200;
				class_1657 lv = arg.method_18779();
				if (lv == null) {
					return 0;
				} else {
					Random random = arg.field_9229;
					int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
					int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
					class_2338 lv2 = new class_2338(lv).method_10069(i, 0, j);
					if (!arg.method_8627(
						lv2.method_10263() - 10, lv2.method_10264() - 10, lv2.method_10260() - 10, lv2.method_10263() + 10, lv2.method_10264() + 10, lv2.method_10260() + 10
					)) {
						return 0;
					} else {
						if (class_1948.method_8660(class_1317.class_1319.field_6317, arg, lv2, class_1299.field_16281)) {
							if (arg.method_19497(lv2, 2)) {
								return this.method_20263(arg, lv2);
							}

							if (class_3031.field_13520.method_14024(arg, lv2)) {
								return this.method_20260(arg, lv2);
							}
						}

						return 0;
					}
				}
			}
		} else {
			return 0;
		}
	}

	private int method_20263(class_3218 arg, class_2338 arg2) {
		int i = 48;
		if (arg.method_19494().method_20252(class_4158.field_18517.method_19164(), arg2, 48, class_4153.class_4155.field_18488) > 4L) {
			List<class_1451> list = arg.method_18467(class_1451.class, new class_238(arg2).method_1009(48.0, 8.0, 48.0));
			if (list.size() < 5) {
				return this.method_20262(arg2, arg);
			}
		}

		return 0;
	}

	private int method_20260(class_1937 arg, class_2338 arg2) {
		int i = 16;
		List<class_1451> list = arg.method_18467(class_1451.class, new class_238(arg2).method_1009(16.0, 8.0, 16.0));
		return list.size() < 1 ? this.method_20262(arg2, arg) : 0;
	}

	private int method_20262(class_2338 arg, class_1937 arg2) {
		class_1451 lv = class_1299.field_16281.method_5883(arg2);
		if (lv == null) {
			return 0;
		} else {
			lv.method_5943(arg2, arg2.method_8404(arg), class_3730.field_16459, null, null);
			lv.method_5725(arg, 0.0F, 0.0F);
			arg2.method_8649(lv);
			return 1;
		}
	}
}
