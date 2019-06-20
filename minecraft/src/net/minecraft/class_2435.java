package net.minecraft;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class class_2435 extends class_2484 {
	protected class_2435(class_2248.class_2251 arg) {
		super(class_2484.class_2486.field_11510, arg);
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, @Nullable class_1309 arg4, class_1799 arg5) {
		super.method_9567(arg, arg2, arg3, arg4, arg5);
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_2631) {
			class_2631 lv2 = (class_2631)lv;
			GameProfile gameProfile = null;
			if (arg5.method_7985()) {
				class_2487 lv3 = arg5.method_7969();
				if (lv3.method_10573("SkullOwner", 10)) {
					gameProfile = class_2512.method_10683(lv3.method_10562("SkullOwner"));
				} else if (lv3.method_10573("SkullOwner", 8) && !StringUtils.isBlank(lv3.method_10558("SkullOwner"))) {
					gameProfile = new GameProfile(null, lv3.method_10558("SkullOwner"));
				}
			}

			lv2.method_11333(gameProfile);
		}
	}
}
