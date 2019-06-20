package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1819 extends class_1792 {
	public class_1819(class_1792.class_1793 arg) {
		super(arg);
		this.method_7863(new class_2960("blocking"), (argx, arg2, arg3) -> {
			if (arg3 != null && arg3.method_6039()) {
				if (arg3.method_6030() == argx) {
					return 1.0F;
				} else {
					return !arg3.method_6115() && arg3.method_5998(class_1268.field_5810) == argx ? 1.0F : 0.0F;
				}
			} else {
				return 0.0F;
			}
		});
		class_2315.method_10009(this, class_1738.field_7879);
	}

	@Override
	public String method_7866(class_1799 arg) {
		return arg.method_7941("BlockEntityTag") != null ? this.method_7876() + '.' + method_8013(arg).method_7792() : super.method_7866(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		class_1746.method_7705(arg, list);
	}

	@Override
	public class_1839 method_7853(class_1799 arg) {
		return class_1839.field_8949;
	}

	@Override
	public int method_7881(class_1799 arg) {
		return 72000;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		arg2.method_6019(arg3);
		return new class_1271<>(class_1269.field_5812, lv);
	}

	@Override
	public boolean method_7878(class_1799 arg, class_1799 arg2) {
		return class_3489.field_15537.method_15141(arg2.method_7909()) || super.method_7878(arg, arg2);
	}

	public static class_1767 method_8013(class_1799 arg) {
		return class_1767.method_7791(arg.method_7911("BlockEntityTag").method_10550("Base"));
	}
}
