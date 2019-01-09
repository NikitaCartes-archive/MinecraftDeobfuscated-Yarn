package net.minecraft;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1612 {
	private static final Logger field_7354 = LogManager.getLogger();
	public static final class_1320 field_7359 = new class_1329(null, "generic.maxHealth", 20.0, 0.0, 1024.0).method_6222("Max Health").method_6212(true);
	public static final class_1320 field_7365 = new class_1329(null, "generic.followRange", 32.0, 0.0, 2048.0).method_6222("Follow Range");
	public static final class_1320 field_7360 = new class_1329(null, "generic.knockbackResistance", 0.0, 0.0, 1.0).method_6222("Knockback Resistance");
	public static final class_1320 field_7357 = new class_1329(null, "generic.movementSpeed", 0.7F, 0.0, 1024.0).method_6222("Movement Speed").method_6212(true);
	public static final class_1320 field_7355 = new class_1329(null, "generic.flyingSpeed", 0.4F, 0.0, 1024.0).method_6222("Flying Speed").method_6212(true);
	public static final class_1320 field_7363 = new class_1329(null, "generic.attackDamage", 2.0, 0.0, 2048.0);
	public static final class_1320 field_7361 = new class_1329(null, "generic.attackKnockback", 0.0, 0.0, 5.0);
	public static final class_1320 field_7356 = new class_1329(null, "generic.attackSpeed", 4.0, 0.0, 1024.0).method_6212(true);
	public static final class_1320 field_7358 = new class_1329(null, "generic.armor", 0.0, 0.0, 30.0).method_6212(true);
	public static final class_1320 field_7364 = new class_1329(null, "generic.armorToughness", 0.0, 0.0, 20.0).method_6212(true);
	public static final class_1320 field_7362 = new class_1329(null, "generic.luck", 0.0, -1024.0, 1024.0).method_6212(true);

	public static class_2499 method_7134(class_1325 arg) {
		class_2499 lv = new class_2499();

		for (class_1324 lv2 : arg.method_6204()) {
			lv.method_10606(method_7130(lv2));
		}

		return lv;
	}

	private static class_2487 method_7130(class_1324 arg) {
		class_2487 lv = new class_2487();
		class_1320 lv2 = arg.method_6198();
		lv.method_10582("Name", lv2.method_6167());
		lv.method_10549("Base", arg.method_6201());
		Collection<class_1322> collection = arg.method_6195();
		if (collection != null && !collection.isEmpty()) {
			class_2499 lv3 = new class_2499();

			for (class_1322 lv4 : collection) {
				if (lv4.method_6188()) {
					lv3.method_10606(method_7135(lv4));
				}
			}

			lv.method_10566("Modifiers", lv3);
		}

		return lv;
	}

	public static class_2487 method_7135(class_1322 arg) {
		class_2487 lv = new class_2487();
		lv.method_10582("Name", arg.method_6185());
		lv.method_10549("Amount", arg.method_6186());
		lv.method_10569("Operation", arg.method_6182().method_6191());
		lv.method_10560("UUID", arg.method_6189());
		return lv;
	}

	public static void method_7131(class_1325 arg, class_2499 arg2) {
		for (int i = 0; i < arg2.size(); i++) {
			class_2487 lv = arg2.method_10602(i);
			class_1324 lv2 = arg.method_6207(lv.method_10558("Name"));
			if (lv2 == null) {
				field_7354.warn("Ignoring unknown attribute '{}'", lv.method_10558("Name"));
			} else {
				method_7132(lv2, lv);
			}
		}
	}

	private static void method_7132(class_1324 arg, class_2487 arg2) {
		arg.method_6192(arg2.method_10574("Base"));
		if (arg2.method_10573("Modifiers", 9)) {
			class_2499 lv = arg2.method_10554("Modifiers", 10);

			for (int i = 0; i < lv.size(); i++) {
				class_1322 lv2 = method_7133(lv.method_10602(i));
				if (lv2 != null) {
					class_1322 lv3 = arg.method_6199(lv2.method_6189());
					if (lv3 != null) {
						arg.method_6202(lv3);
					}

					arg.method_6197(lv2);
				}
			}
		}
	}

	@Nullable
	public static class_1322 method_7133(class_2487 arg) {
		UUID uUID = arg.method_10584("UUID");

		try {
			class_1322.class_1323 lv = class_1322.class_1323.method_6190(arg.method_10550("Operation"));
			return new class_1322(uUID, arg.method_10558("Name"), arg.method_10574("Amount"), lv);
		} catch (Exception var3) {
			field_7354.warn("Unable to create attribute: {}", var3.getMessage());
			return null;
		}
	}
}
