package net.minecraft;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4453 {
	private static final Map<Long, String> field_20270 = new HashMap();

	public static String method_21585(long l) {
		return (String)field_20270.get(l);
	}

	public static void method_21587(long l) {
		field_20270.remove(l);
	}

	public static void method_21586(long l, String string) {
		field_20270.put(l, string);
	}
}
