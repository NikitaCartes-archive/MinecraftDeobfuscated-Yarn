package net.minecraft;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1670 extends class_1668 {
	public class_1670(class_1299<? extends class_1670> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Environment(EnvType.CLIENT)
	public class_1670(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(class_1299.field_6129, d, e, f, g, h, i, arg);
	}

	public class_1670(class_1937 arg, class_1309 arg2, double d, double e, double f) {
		super(class_1299.field_6129, arg2, d, e, f, arg);
	}

	@Override
	protected void method_7469(class_239 arg) {
		if (arg.method_17783() != class_239.class_240.field_1331 || !((class_3966)arg).method_17782().method_5779(this.field_7604)) {
			if (!this.field_6002.field_9236) {
				List<class_1309> list = this.field_6002.method_18467(class_1309.class, this.method_5829().method_1009(4.0, 2.0, 4.0));
				class_1295 lv = new class_1295(this.field_6002, this.field_5987, this.field_6010, this.field_6035);
				lv.method_5607(this.field_7604);
				lv.method_5608(class_2398.field_11216);
				lv.method_5603(3.0F);
				lv.method_5604(600);
				lv.method_5596((7.0F - lv.method_5599()) / (float)lv.method_5605());
				lv.method_5610(new class_1293(class_1294.field_5921, 1, 1));
				if (!list.isEmpty()) {
					for (class_1309 lv2 : list) {
						double d = this.method_5858(lv2);
						if (d < 16.0) {
							lv.method_5814(lv2.field_5987, lv2.field_6010, lv2.field_6035);
							break;
						}
					}
				}

				this.field_6002.method_8535(2006, new class_2338(this.field_5987, this.field_6010, this.field_6035), 0);
				this.field_6002.method_8649(lv);
				this.method_5650();
			}
		}
	}

	@Override
	public boolean method_5863() {
		return false;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		return false;
	}

	@Override
	protected class_2394 method_7467() {
		return class_2398.field_11216;
	}

	@Override
	protected boolean method_7468() {
		return false;
	}
}
