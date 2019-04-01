package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3685 {
	@Deprecated
	public static int[] method_16049(class_3300 arg, class_2960 arg2) throws IOException {
		class_3298 lv = arg.method_14486(arg2);
		Throwable var3 = null;

		int[] var6;
		try (class_1011 lv2 = class_1011.method_4309(lv.method_14482())) {
			var6 = lv2.method_4322();
		} catch (Throwable var31) {
			var3 = var31;
			throw var31;
		} finally {
			if (lv != null) {
				if (var3 != null) {
					try {
						lv.close();
					} catch (Throwable var27) {
						var3.addSuppressed(var27);
					}
				} else {
					lv.close();
				}
			}
		}

		return var6;
	}
}
