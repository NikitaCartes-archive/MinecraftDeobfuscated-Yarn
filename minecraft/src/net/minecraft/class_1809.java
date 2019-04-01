package net.minecraft;

import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.StringUtils;

public class class_1809 extends class_1827 {
	public class_1809(class_2248 arg, class_2248 arg2, class_1792.class_1793 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	public class_2561 method_7864(class_1799 arg) {
		if (arg.method_7909() == class_1802.field_8575 && arg.method_7985()) {
			String string = null;
			class_2487 lv = arg.method_7969();
			if (lv.method_10573("SkullOwner", 8)) {
				string = lv.method_10558("SkullOwner");
			} else if (lv.method_10573("SkullOwner", 10)) {
				class_2487 lv2 = lv.method_10562("SkullOwner");
				if (lv2.method_10573("Name", 8)) {
					string = lv2.method_10558("Name");
				}
			}

			if (string != null) {
				return new class_2588(this.method_7876() + ".named", string);
			}
		}

		return super.method_7864(arg);
	}

	@Override
	public boolean method_7860(class_2487 arg) {
		super.method_7860(arg);
		if (arg.method_10573("SkullOwner", 8) && !StringUtils.isBlank(arg.method_10558("SkullOwner"))) {
			GameProfile gameProfile = new GameProfile(null, arg.method_10558("SkullOwner"));
			gameProfile = class_2631.method_11335(gameProfile);
			arg.method_10566("SkullOwner", class_2512.method_10684(new class_2487(), gameProfile));
			return true;
		} else {
			return false;
		}
	}
}
