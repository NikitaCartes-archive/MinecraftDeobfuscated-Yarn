package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1812 extends class_1792 {
	public class_1812(class_1792.class_1793 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_7854() {
		return class_1844.method_8061(super.method_7854(), class_1847.field_8991);
	}

	@Override
	public class_1799 method_7861(class_1799 arg, class_1937 arg2, class_1309 arg3) {
		class_1657 lv = arg3 instanceof class_1657 ? (class_1657)arg3 : null;
		if (lv == null || !lv.field_7503.field_7477) {
			arg.method_7934(1);
		}

		if (lv instanceof class_3222) {
			class_174.field_1198.method_8821((class_3222)lv, arg);
		}

		if (!arg2.field_9236) {
			for (class_1293 lv2 : class_1844.method_8067(arg)) {
				if (lv2.method_5579().method_5561()) {
					lv2.method_5579().method_5564(lv, lv, arg3, lv2.method_5578(), 1.0);
				} else {
					arg3.method_6092(new class_1293(lv2));
				}
			}
		}

		if (lv != null) {
			lv.method_7259(class_3468.field_15372.method_14956(this));
		}

		if (lv == null || !lv.field_7503.field_7477) {
			if (arg.method_7960()) {
				return new class_1799(class_1802.field_8469);
			}

			if (lv != null) {
				lv.field_7514.method_7394(new class_1799(class_1802.field_8469));
			}
		}

		return arg;
	}

	@Override
	public int method_7881(class_1799 arg) {
		return 32;
	}

	@Override
	public class_1839 method_7853(class_1799 arg) {
		return class_1839.field_8946;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		arg2.method_6019(arg3);
		return new class_1271<>(class_1269.field_5812, arg2.method_5998(arg3));
	}

	@Override
	public String method_7866(class_1799 arg) {
		return class_1844.method_8063(arg).method_8051(this.method_7876() + ".effect.");
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		class_1844.method_8065(arg, list, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(class_1799 arg) {
		return super.method_7886(arg) || !class_1844.method_8067(arg).isEmpty();
	}

	@Override
	public void method_7850(class_1761 arg, class_2371<class_1799> arg2) {
		if (this.method_7877(arg)) {
			for (class_1842 lv : class_2378.field_11143) {
				if (lv != class_1847.field_8984) {
					arg2.add(class_1844.method_8061(new class_1799(this), lv));
				}
			}
		}
	}
}
