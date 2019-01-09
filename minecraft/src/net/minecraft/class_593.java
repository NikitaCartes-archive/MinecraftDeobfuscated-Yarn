package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_593 {
	public class_618[] field_3502;
	public final int field_3500;
	private boolean field_3501;

	public class_593(class_618[] args) {
		this.field_3502 = args;
		this.field_3500 = args.length;
	}

	public class_593(class_618[] args, int i, int j, int k, int l, float f, float g) {
		this(args);
		float h = 0.0F / f;
		float m = 0.0F / g;
		args[0] = args[0].method_2837((float)k / f - h, (float)j / g + m);
		args[1] = args[1].method_2837((float)i / f + h, (float)j / g + m);
		args[2] = args[2].method_2837((float)i / f + h, (float)l / g - m);
		args[3] = args[3].method_2837((float)k / f - h, (float)l / g - m);
	}

	public void method_2826() {
		class_618[] lvs = new class_618[this.field_3502.length];

		for (int i = 0; i < this.field_3502.length; i++) {
			lvs[i] = this.field_3502[this.field_3502.length - i - 1];
		}

		this.field_3502 = lvs;
	}

	public void method_2825(class_287 arg, float f) {
		class_243 lv = this.field_3502[1].field_3605.method_1035(this.field_3502[0].field_3605);
		class_243 lv2 = this.field_3502[1].field_3605.method_1035(this.field_3502[2].field_3605);
		class_243 lv3 = lv2.method_1036(lv).method_1029();
		float g = (float)lv3.field_1352;
		float h = (float)lv3.field_1351;
		float i = (float)lv3.field_1350;
		if (this.field_3501) {
			g = -g;
			h = -h;
			i = -i;
		}

		arg.method_1328(7, class_290.field_1580);

		for (int j = 0; j < 4; j++) {
			class_618 lv4 = this.field_3502[j];
			arg.method_1315(lv4.field_3605.field_1352 * (double)f, lv4.field_3605.field_1351 * (double)f, lv4.field_3605.field_1350 * (double)f)
				.method_1312((double)lv4.field_3604, (double)lv4.field_3603)
				.method_1318(g, h, i)
				.method_1344();
		}

		class_289.method_1348().method_1350();
	}
}
