package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1772 extends class_1792 {
	public class_1772(class_1792.class_1793 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(class_1799 arg) {
		return true;
	}

	@Override
	public boolean method_7870(class_1799 arg) {
		return false;
	}

	public static class_2499 method_7806(class_1799 arg) {
		class_2487 lv = arg.method_7969();
		return lv != null ? lv.method_10554("StoredEnchantments", 10) : new class_2499();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		super.method_7851(arg, arg2, list, arg3);
		class_1799.method_17870(list, method_7806(arg));
	}

	public static void method_7807(class_1799 arg, class_1889 arg2) {
		class_2499 lv = method_7806(arg);
		boolean bl = true;
		class_2960 lv2 = class_2378.field_11160.method_10221(arg2.field_9093);

		for (int i = 0; i < lv.size(); i++) {
			class_2487 lv3 = lv.method_10602(i);
			class_2960 lv4 = class_2960.method_12829(lv3.method_10558("id"));
			if (lv4 != null && lv4.equals(lv2)) {
				if (lv3.method_10550("lvl") < arg2.field_9094) {
					lv3.method_10575("lvl", (short)arg2.field_9094);
				}

				bl = false;
				break;
			}
		}

		if (bl) {
			class_2487 lv5 = new class_2487();
			lv5.method_10582("id", String.valueOf(lv2));
			lv5.method_10575("lvl", (short)arg2.field_9094);
			lv.add(lv5);
		}

		arg.method_7948().method_10566("StoredEnchantments", lv);
	}

	public static class_1799 method_7808(class_1889 arg) {
		class_1799 lv = new class_1799(class_1802.field_8598);
		method_7807(lv, arg);
		return lv;
	}

	@Override
	public void method_7850(class_1761 arg, class_2371<class_1799> arg2) {
		if (arg == class_1761.field_7915) {
			for (class_1887 lv : class_2378.field_11160) {
				if (lv.field_9083 != null) {
					for (int i = lv.method_8187(); i <= lv.method_8183(); i++) {
						arg2.add(method_7808(new class_1889(lv, i)));
					}
				}
			}
		} else if (arg.method_7744().length != 0) {
			for (class_1887 lvx : class_2378.field_11160) {
				if (arg.method_7740(lvx.field_9083)) {
					arg2.add(method_7808(new class_1889(lvx, lvx.method_8183())));
				}
			}
		}
	}
}
