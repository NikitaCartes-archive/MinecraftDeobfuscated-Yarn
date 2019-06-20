package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_2478 extends class_2237 implements class_3737 {
	public static final class_2746 field_11491 = class_2741.field_12508;
	protected static final class_265 field_11492 = class_2248.method_9541(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	protected class_2478(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_11491)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11492;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(class_2680 arg) {
		return true;
	}

	@Override
	public boolean method_9538() {
		return true;
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2625();
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (arg2.field_9236) {
			return true;
		} else {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2625) {
				class_2625 lv2 = (class_2625)lv;
				class_1799 lv3 = arg4.method_5998(arg5);
				if (lv3.method_7909() instanceof class_1769 && arg4.field_7503.field_7476) {
					boolean bl = lv2.method_16127(((class_1769)lv3.method_7909()).method_7802());
					if (bl && !arg4.method_7337()) {
						lv3.method_7934(1);
					}
				}

				return lv2.method_11301(arg4);
			} else {
				return false;
			}
		}
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_11491) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}
}
