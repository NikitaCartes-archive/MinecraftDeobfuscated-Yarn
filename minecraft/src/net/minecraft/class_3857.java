package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_3856.class
	)})
public abstract class class_3857 extends class_1682 implements class_3856 {
	private static final class_2940<class_1799> field_17082 = class_2945.method_12791(class_3857.class, class_2943.field_13322);

	public class_3857(class_1299<? extends class_3857> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_3857(class_1299<? extends class_3857> arg, double d, double e, double f, class_1937 arg2) {
		super(arg, d, e, f, arg2);
	}

	public class_3857(class_1299<? extends class_3857> arg, class_1309 arg2, class_1937 arg3) {
		super(arg, arg2, arg3);
	}

	public void method_16940(class_1799 arg) {
		if (arg.method_7909() != this.method_16942() || arg.method_7985()) {
			this.method_5841().method_12778(field_17082, class_156.method_654(arg.method_7972(), argx -> argx.method_7939(1)));
		}
	}

	protected abstract class_1792 method_16942();

	protected class_1799 method_16943() {
		return this.method_5841().method_12789(field_17082);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_7495() {
		class_1799 lv = this.method_16943();
		return lv.method_7960() ? new class_1799(this.method_16942()) : lv;
	}

	@Override
	protected void method_5693() {
		this.method_5841().method_12784(field_17082, class_1799.field_8037);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		class_1799 lv = this.method_16943();
		if (!lv.method_7960()) {
			arg.method_10566("Item", lv.method_7953(new class_2487()));
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		class_1799 lv = class_1799.method_7915(arg.method_10562("Item"));
		this.method_16940(lv);
	}
}
