package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1833 extends class_1744 {
	public class_1833(class_1792.class_1793 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_7854() {
		return class_1844.method_8061(super.method_7854(), class_1847.field_8982);
	}

	@Override
	public class_1665 method_7702(class_1937 arg, class_1799 arg2, class_1309 arg3) {
		class_1667 lv = new class_1667(arg, arg3);
		lv.method_7459(arg2);
		return lv;
	}

	@Override
	public void method_7850(class_1761 arg, class_2371<class_1799> arg2) {
		if (this.method_7877(arg)) {
			for (class_1842 lv : class_2378.field_11143) {
				if (!lv.method_8049().isEmpty()) {
					arg2.add(class_1844.method_8061(new class_1799(this), lv));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		class_1844.method_8065(arg, list, 0.125F);
	}

	@Override
	public String method_7866(class_1799 arg) {
		return class_1844.method_8063(arg).method_8051(this.method_7876() + ".effect.");
	}
}
