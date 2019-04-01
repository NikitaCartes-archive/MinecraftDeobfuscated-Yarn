package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_478 implements class_1712 {
	private final class_310 field_2876;

	public class_478(class_310 arg) {
		this.field_2876 = arg;
	}

	@Override
	public void method_7634(class_1703 arg, class_2371<class_1799> arg2) {
	}

	@Override
	public void method_7635(class_1703 arg, int i, class_1799 arg2) {
		this.field_2876.field_1761.method_2909(arg2, i);
	}

	@Override
	public void method_7633(class_1703 arg, int i, int j) {
	}
}
