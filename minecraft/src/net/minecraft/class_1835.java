package net.minecraft;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1835 extends class_1792 {
	public class_1835(class_1792.class_1793 arg) {
		super(arg);
		this.method_7863(new class_2960("throwing"), (argx, arg2, arg3) -> arg3 != null && arg3.method_6115() && arg3.method_6030() == argx ? 1.0F : 0.0F);
	}

	@Override
	public boolean method_7885(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4) {
		return !arg4.method_7337();
	}

	@Override
	public class_1839 method_7853(class_1799 arg) {
		return class_1839.field_8951;
	}

	@Override
	public int method_7881(class_1799 arg) {
		return 72000;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(class_1799 arg) {
		return false;
	}

	@Override
	public void method_7840(class_1799 arg, class_1937 arg2, class_1309 arg3, int i) {
		if (arg3 instanceof class_1657) {
			class_1657 lv = (class_1657)arg3;
			int j = this.method_7881(arg) - i;
			if (j >= 10) {
				int k = class_1890.method_8202(arg);
				if (k <= 0 || lv.method_5721()) {
					if (!arg2.field_9236) {
						arg.method_7956(1, lv);
						if (k == 0) {
							class_1685 lv2 = new class_1685(arg2, lv, arg);
							lv2.method_7437(lv, lv.field_5965, lv.field_6031, 0.0F, 2.5F + (float)k * 0.5F, 1.0F);
							if (lv.field_7503.field_7477) {
								lv2.field_7572 = class_1665.class_1666.field_7594;
							}

							arg2.method_8649(lv2);
							arg2.method_8449(null, lv2, class_3417.field_15001, class_3419.field_15248, 1.0F, 1.0F);
							if (!lv.field_7503.field_7477) {
								lv.field_7514.method_7378(arg);
							}
						}
					}

					lv.method_7259(class_3468.field_15372.method_14956(this));
					if (k > 0) {
						float f = lv.field_6031;
						float g = lv.field_5965;
						float h = -class_3532.method_15374(f * (float) (Math.PI / 180.0)) * class_3532.method_15362(g * (float) (Math.PI / 180.0));
						float l = -class_3532.method_15374(g * (float) (Math.PI / 180.0));
						float m = class_3532.method_15362(f * (float) (Math.PI / 180.0)) * class_3532.method_15362(g * (float) (Math.PI / 180.0));
						float n = class_3532.method_15355(h * h + l * l + m * m);
						float o = 3.0F * ((1.0F + (float)k) / 4.0F);
						h *= o / n;
						l *= o / n;
						m *= o / n;
						lv.method_5762((double)h, (double)l, (double)m);
						lv.method_6018(20);
						if (lv.field_5952) {
							float p = 1.1999999F;
							lv.method_5784(class_1313.field_6308, 0.0, 1.1999999F, 0.0);
						}

						class_3414 lv3;
						if (k >= 3) {
							lv3 = class_3417.field_14717;
						} else if (k == 2) {
							lv3 = class_3417.field_14806;
						} else {
							lv3 = class_3417.field_14606;
						}

						arg2.method_8449(null, lv, lv3, class_3419.field_15248, 1.0F, 1.0F);
					}
				}
			}
		}
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		if (lv.method_7919() >= lv.method_7936()) {
			return new class_1271<>(class_1269.field_5814, lv);
		} else if (class_1890.method_8202(lv) > 0 && !arg2.method_5721()) {
			return new class_1271<>(class_1269.field_5814, lv);
		} else {
			arg2.method_6019(arg3);
			return new class_1271<>(class_1269.field_5812, lv);
		}
	}

	@Override
	public boolean method_7873(class_1799 arg, class_1309 arg2, class_1309 arg3) {
		arg.method_7956(1, arg3);
		return true;
	}

	@Override
	public boolean method_7879(class_1799 arg, class_1937 arg2, class_2680 arg3, class_2338 arg4, class_1309 arg5) {
		if ((double)arg3.method_11579(arg2, arg4) != 0.0) {
			arg.method_7956(2, arg5);
		}

		return true;
	}

	@Override
	public Multimap<String, class_1322> method_7844(class_1304 arg) {
		Multimap<String, class_1322> multimap = super.method_7844(arg);
		if (arg == class_1304.field_6173) {
			multimap.put(class_1612.field_7363.method_6167(), new class_1322(field_8006, "Tool modifier", 8.0, class_1322.class_1323.field_6328));
			multimap.put(class_1612.field_7356.method_6167(), new class_1322(field_8001, "Tool modifier", -2.9F, class_1322.class_1323.field_6328));
		}

		return multimap;
	}

	@Override
	public int method_7837() {
		return 1;
	}
}
