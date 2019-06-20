package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class class_13 {
	private final class_5 field_62 = new class_5();
	private final Set<class_9> field_59 = Sets.<class_9>newHashSet();
	private final class_9[] field_60 = new class_9[32];
	private final int field_18708;
	private class_8 field_61;

	public class_13(class_8 arg, int i) {
		this.field_61 = arg;
		this.field_18708 = i;
	}

	@Nullable
	public class_11 method_52(class_1941 arg, class_1308 arg2, double d, double e, double f, float g) {
		this.field_62.method_5();
		this.field_61.method_12(arg, arg2);
		class_9 lv = this.field_61.method_21();
		class_9 lv2 = this.field_61.method_16(d, e, f);
		class_11 lv3 = this.method_54(lv, lv2, g);
		this.field_61.method_19();
		return lv3;
	}

	@Nullable
	private class_11 method_54(class_9 arg, class_9 arg2, float f) {
		arg.field_36 = 0.0F;
		arg.field_34 = arg.method_31(arg2);
		arg.field_47 = arg.field_34;
		this.field_62.method_5();
		this.field_59.clear();
		this.field_62.method_2(arg);
		class_9 lv = arg;
		int i = 0;

		while (!this.field_62.method_8()) {
			if (++i >= this.field_18708) {
				break;
			}

			class_9 lv2 = this.field_62.method_6();
			lv2.field_42 = true;
			if (lv2.equals(arg2)) {
				lv = arg2;
				break;
			}

			if (lv2.method_31(arg2) < lv.method_31(arg2)) {
				lv = lv2;
			}

			if (!(lv2.method_31(arg2) >= f)) {
				int j = this.field_61.method_18(this.field_60, lv2);

				for (int k = 0; k < j; k++) {
					class_9 lv3 = this.field_60[k];
					float g = lv2.method_31(lv3);
					lv3.field_46 = lv2.field_46 + g;
					float h = lv2.field_36 + g + lv3.field_43;
					if (lv3.field_46 < f && (!lv3.method_27() || h < lv3.field_36)) {
						lv3.field_35 = lv2;
						lv3.field_36 = h;
						lv3.field_34 = lv3.method_31(arg2) * 1.5F + lv3.field_43;
						if (lv3.method_27()) {
							this.field_62.method_3(lv3, lv3.field_36 + lv3.field_34);
						} else {
							lv3.field_47 = lv3.field_36 + lv3.field_34;
							this.field_62.method_2(lv3);
						}
					}
				}
			}
		}

		return lv.equals(arg) ? null : this.method_55(lv);
	}

	private class_11 method_55(class_9 arg) {
		List<class_9> list = Lists.<class_9>newArrayList();
		class_9 lv = arg;
		list.add(0, arg);

		while (lv.field_35 != null) {
			lv = lv.field_35;
			list.add(0, lv);
		}

		return new class_11(list);
	}
}
