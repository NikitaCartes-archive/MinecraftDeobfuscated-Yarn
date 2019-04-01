package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_341 {
	public static String method_1849(String string, boolean bl) {
		return !bl && !class_310.method_1551().field_1690.field_1900 ? class_124.method_539(string) : string;
	}

	public static List<class_2561> method_1850(class_2561 arg, int i, class_327 arg2, boolean bl, boolean bl2) {
		int j = 0;
		class_2561 lv = new class_2585("");
		List<class_2561> list = Lists.<class_2561>newArrayList();
		List<class_2561> list2 = Lists.<class_2561>newArrayList(arg);

		for (int k = 0; k < list2.size(); k++) {
			class_2561 lv2 = (class_2561)list2.get(k);
			String string = lv2.method_10851();
			boolean bl3 = false;
			if (string.contains("\n")) {
				int l = string.indexOf(10);
				String string2 = string.substring(l + 1);
				string = string.substring(0, l + 1);
				class_2561 lv3 = new class_2585(string2).method_10862(lv2.method_10866().method_10976());
				list2.add(k + 1, lv3);
				bl3 = true;
			}

			String string3 = method_1849(lv2.method_10866().method_10953() + string, bl2);
			String string2 = string3.endsWith("\n") ? string3.substring(0, string3.length() - 1) : string3;
			int m = arg2.method_1727(string2);
			class_2561 lv4 = new class_2585(string2).method_10862(lv2.method_10866().method_10976());
			if (j + m > i) {
				String string4 = arg2.method_1711(string3, i - j, false);
				String string5 = string4.length() < string3.length() ? string3.substring(string4.length()) : null;
				if (string5 != null && !string5.isEmpty()) {
					int n = string5.charAt(0) != ' ' ? string4.lastIndexOf(32) : string4.length();
					if (n >= 0 && arg2.method_1727(string3.substring(0, n)) > 0) {
						string4 = string3.substring(0, n);
						if (bl) {
							n++;
						}

						string5 = string3.substring(n);
					} else if (j > 0 && !string3.contains(" ")) {
						string4 = "";
						string5 = string3;
					}

					class_2561 lv5 = new class_2585(string5).method_10862(lv2.method_10866().method_10976());
					list2.add(k + 1, lv5);
				}

				m = arg2.method_1727(string4);
				lv4 = new class_2585(string4);
				lv4.method_10862(lv2.method_10866().method_10976());
				bl3 = true;
			}

			if (j + m <= i) {
				j += m;
				lv.method_10852(lv4);
			} else {
				bl3 = true;
			}

			if (bl3) {
				list.add(lv);
				j = 0;
				lv = new class_2585("");
			}
		}

		list.add(lv);
		return list;
	}
}
