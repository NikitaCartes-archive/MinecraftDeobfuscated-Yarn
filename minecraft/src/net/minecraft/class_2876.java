package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;

public enum class_2876 {
	field_13097 {
		@Override
		public void method_12507(class_3218 arg, class_2881 arg2, List<class_1511> list, int i, class_2338 arg3) {
			class_2338 lv = new class_2338(0, 128, 0);

			for (class_1511 lv2 : list) {
				lv2.method_6837(lv);
			}

			arg2.method_12521(field_13095);
		}
	},
	field_13095 {
		@Override
		public void method_12507(class_3218 arg, class_2881 arg2, List<class_1511> list, int i, class_2338 arg3) {
			if (i < 100) {
				if (i == 0 || i == 50 || i == 51 || i == 52 || i >= 95) {
					arg.method_8535(3001, new class_2338(0, 128, 0), 0);
				}
			} else {
				arg2.method_12521(field_13094);
			}
		}
	},
	field_13094 {
		@Override
		public void method_12507(class_3218 arg, class_2881 arg2, List<class_1511> list, int i, class_2338 arg3) {
			int j = 40;
			boolean bl = i % 40 == 0;
			boolean bl2 = i % 40 == 39;
			if (bl || bl2) {
				List<class_3310.class_3181> list2 = class_3310.method_14506(arg);
				int k = i / 40;
				if (k < list2.size()) {
					class_3310.class_3181 lv = (class_3310.class_3181)list2.get(k);
					if (bl) {
						for (class_1511 lv2 : list) {
							lv2.method_6837(new class_2338(lv.method_13966(), lv.method_13964() + 1, lv.method_13967()));
						}
					} else {
						int l = 10;

						for (class_2338 lv3 : class_2338.method_10097(
							new class_2338(lv.method_13966() - 10, lv.method_13964() - 10, lv.method_13967() - 10),
							new class_2338(lv.method_13966() + 10, lv.method_13964() + 10, lv.method_13967() + 10)
						)) {
							arg.method_8650(lv3, false);
						}

						arg.method_8437(
							null,
							(double)((float)lv.method_13966() + 0.5F),
							(double)lv.method_13964(),
							(double)((float)lv.method_13967() + 0.5F),
							5.0F,
							class_1927.class_4179.field_18687
						);
						class_3666 lv4 = new class_3666(true, ImmutableList.of(lv), new class_2338(0, 128, 0));
						class_3031.field_13522
							.method_13151(
								arg, (class_2794<? extends class_2888>)arg.method_14178().method_12129(), new Random(), new class_2338(lv.method_13966(), 45, lv.method_13967()), lv4
							);
					}
				} else if (bl) {
					arg2.method_12521(field_13098);
				}
			}
		}
	},
	field_13098 {
		@Override
		public void method_12507(class_3218 arg, class_2881 arg2, List<class_1511> list, int i, class_2338 arg3) {
			if (i >= 100) {
				arg2.method_12521(field_13099);
				arg2.method_12524();

				for (class_1511 lv : list) {
					lv.method_6837(null);
					arg.method_8437(lv, lv.field_5987, lv.field_6010, lv.field_6035, 6.0F, class_1927.class_4179.field_18685);
					lv.method_5650();
				}
			} else if (i >= 80) {
				arg.method_8535(3001, new class_2338(0, 128, 0), 0);
			} else if (i == 0) {
				for (class_1511 lv : list) {
					lv.method_6837(new class_2338(0, 128, 0));
				}
			} else if (i < 5) {
				arg.method_8535(3001, new class_2338(0, 128, 0), 0);
			}
		}
	},
	field_13099 {
		@Override
		public void method_12507(class_3218 arg, class_2881 arg2, List<class_1511> list, int i, class_2338 arg3) {
		}
	};

	private class_2876() {
	}

	public abstract void method_12507(class_3218 arg, class_2881 arg2, List<class_1511> list, int i, class_2338 arg3);
}
