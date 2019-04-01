package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1152 implements class_1155 {
	private static final Set<class_2248> field_5632 = Sets.<class_2248>newHashSet(
		class_2246.field_10431,
		class_2246.field_10037,
		class_2246.field_10511,
		class_2246.field_10306,
		class_2246.field_10533,
		class_2246.field_10010,
		class_2246.field_10126,
		class_2246.field_10155,
		class_2246.field_10307,
		class_2246.field_10303,
		class_2246.field_9999,
		class_2246.field_10178,
		class_2246.field_10503,
		class_2246.field_9988,
		class_2246.field_10539,
		class_2246.field_10335,
		class_2246.field_10098,
		class_2246.field_10035
	);
	private static final class_2561 field_5631 = new class_2588("tutorial.find_tree.title");
	private static final class_2561 field_5628 = new class_2588("tutorial.find_tree.description");
	private final class_1156 field_5630;
	private class_372 field_5633;
	private int field_5629;

	public class_1152(class_1156 arg) {
		this.field_5630 = arg;
	}

	@Override
	public void method_4899() {
		this.field_5629++;
		if (this.field_5630.method_4905() != class_1934.field_9215) {
			this.field_5630.method_4910(class_1157.field_5653);
		} else {
			if (this.field_5629 == 1) {
				class_746 lv = this.field_5630.method_4914().field_1724;
				if (lv != null) {
					for (class_2248 lv2 : field_5632) {
						if (lv.field_7514.method_7379(new class_1799(lv2))) {
							this.field_5630.method_4910(class_1157.field_5655);
							return;
						}
					}

					if (method_4896(lv)) {
						this.field_5630.method_4910(class_1157.field_5655);
						return;
					}
				}
			}

			if (this.field_5629 >= 6000 && this.field_5633 == null) {
				this.field_5633 = new class_372(class_372.class_373.field_2235, field_5631, field_5628, false);
				this.field_5630.method_4914().method_1566().method_1999(this.field_5633);
			}
		}
	}

	@Override
	public void method_4902() {
		if (this.field_5633 != null) {
			this.field_5633.method_1993();
			this.field_5633 = null;
		}
	}

	@Override
	public void method_4898(class_638 arg, class_239 arg2) {
		if (arg2.method_17783() == class_239.class_240.field_1332) {
			class_2680 lv = arg.method_8320(((class_3965)arg2).method_17777());
			if (field_5632.contains(lv.method_11614())) {
				this.field_5630.method_4910(class_1157.field_5649);
			}
		}
	}

	@Override
	public void method_4897(class_1799 arg) {
		for (class_2248 lv : field_5632) {
			if (arg.method_7909() == lv.method_8389()) {
				this.field_5630.method_4910(class_1157.field_5655);
				return;
			}
		}
	}

	public static boolean method_4896(class_746 arg) {
		for (class_2248 lv : field_5632) {
			if (arg.method_3143().method_15025(class_3468.field_15427.method_14956(lv)) > 0) {
				return true;
			}
		}

		return false;
	}
}
