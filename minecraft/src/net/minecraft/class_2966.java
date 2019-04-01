package net.minecraft;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2966 {
	public static final PrintStream field_13358 = System.out;
	private static boolean field_13357;
	private static final Logger field_13359 = LogManager.getLogger();

	public static void method_12851() {
		if (!field_13357) {
			field_13357 = true;
			if (class_2378.field_11144.method_10274()) {
				throw new IllegalStateException("Unable to load registries");
			} else {
				class_2358.method_10199();
				class_3962.method_17758();
				if (class_1299.method_5890(class_1299.field_6097) == null) {
					throw new IllegalStateException("Failed loading EntityTypes");
				} else {
					class_1845.method_8076();
					class_2306.method_9960();
					class_2357.method_18346();
					class_2316.method_10015();
					method_12852();
				}
			}
		}
	}

	private static <T> void method_12848(class_2378<T> arg, Function<T, String> function, Set<String> set) {
		class_2477 lv = class_2477.method_10517();
		arg.iterator().forEachRemaining(object -> {
			String string = (String)function.apply(object);
			if (!lv.method_10516(string)) {
				set.add(string);
			}
		});
	}

	public static Set<String> method_17597() {
		Set<String> set = new TreeSet();
		method_12848(class_2378.field_11145, class_1299::method_5882, set);
		method_12848(class_2378.field_11159, class_1291::method_5567, set);
		method_12848(class_2378.field_11142, class_1792::method_7876, set);
		method_12848(class_2378.field_11160, class_1887::method_8184, set);
		method_12848(class_2378.field_11153, class_1959::method_8689, set);
		method_12848(class_2378.field_11146, class_2248::method_9539, set);
		method_12848(class_2378.field_11158, arg -> "stat." + arg.toString().replace(':', '.'), set);
		return set;
	}

	public static void method_17598() {
		if (!field_13357) {
			throw new IllegalArgumentException("Not bootstrapped");
		} else if (!class_155.field_1125) {
			method_17597().forEach(string -> field_13359.error("Missing translations: " + string));
		}
	}

	private static void method_12852() {
		if (field_13359.isDebugEnabled()) {
			System.setErr(new class_2980("STDERR", System.err));
			System.setOut(new class_2980("STDOUT", field_13358));
		} else {
			System.setErr(new class_2983("STDERR", System.err));
			System.setOut(new class_2983("STDOUT", field_13358));
		}
	}

	public static void method_12847(String string) {
		field_13358.println(string);
	}
}
