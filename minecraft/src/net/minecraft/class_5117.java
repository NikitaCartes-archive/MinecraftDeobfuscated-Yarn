package net.minecraft;

import com.google.common.collect.Multimap;
import java.util.UUID;

enum class_5117 {
	field_23645,
	field_23646,
	field_23647,
	field_23648,
	field_23649,
	field_23650;

	protected static final UUID field_23651 = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID field_23652 = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	protected static final UUID field_23653 = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");

	public void method_26741(class_1832 arg, Multimap<String, class_1322> multimap) {
		float f = this.method_26742(arg);
		float g = this.method_26740(arg);
		float h = this.method_26743(arg);
		multimap.put(class_1612.field_7363.method_6167(), new class_1322(field_23651, "Weapon modifier", (double)g, class_1322.class_1323.field_6328));
		multimap.put(class_1612.field_7356.method_6167(), new class_1322(field_23652, "Weapon modifier", (double)f, class_1322.class_1323.field_6328));
		if (h != 0.0F) {
			multimap.put(class_1612.field_23644.method_6167(), new class_1322(field_23653, "Weapon modifier", (double)h, class_1322.class_1323.field_6328));
		}
	}

	public float method_26740(class_1832 arg) {
		switch (this) {
			case field_23645:
				return arg.method_8028() + 3.0F;
			case field_23646:
				return arg.method_8028() + 4.0F;
			case field_23647:
				return arg.method_8028() + 1.0F;
			case field_23648:
				if (arg != class_1834.field_8923 && arg != class_1834.field_8930) {
					return 0.0F;
				}

				return 1.0F;
			case field_23649:
				return arg.method_8028();
			case field_23650:
				return 5.0F;
			default:
				return 0.0F;
		}
	}

	public float method_26742(class_1832 arg) {
		switch (this) {
			case field_23645:
				return 0.5F;
			case field_23646:
			case field_23649:
			case field_23650:
				return -0.5F;
			case field_23647:
				return 0.0F;
			case field_23648:
				if (arg == class_1834.field_8922) {
					return -0.5F;
				} else if (arg == class_1834.field_8923) {
					return 0.5F;
				} else if (arg == class_1834.field_8930) {
					return 1.0F;
				} else {
					if (arg == class_1834.field_8929) {
						return 1.0F;
					}

					return 0.0F;
				}
			default:
				return 0.0F;
		}
	}

	public float method_26743(class_1832 arg) {
		switch (this) {
			case field_23645:
				return 0.5F;
			case field_23646:
			case field_23647:
			case field_23649:
				return 0.0F;
			case field_23648:
			case field_23650:
				return 1.0F;
			default:
				return 0.0F;
		}
	}
}
