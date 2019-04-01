package net.minecraft;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class class_173 {
	private static final BiMap<class_2960, class_176> field_1178 = HashBiMap.create();
	public static final class_176 field_1175 = method_759("empty", arg -> {
	});
	public static final class_176 field_1179 = method_759("chest", arg -> arg.method_781(class_181.field_1232).method_780(class_181.field_1226));
	public static final class_176 field_1176 = method_759("fishing", arg -> arg.method_781(class_181.field_1232).method_781(class_181.field_1229));
	public static final class_176 field_1173 = method_759(
		"entity",
		arg -> arg.method_781(class_181.field_1226)
				.method_781(class_181.field_1232)
				.method_781(class_181.field_1231)
				.method_780(class_181.field_1230)
				.method_780(class_181.field_1227)
				.method_780(class_181.field_1233)
	);
	public static final class_176 field_16235 = method_759("gift", arg -> arg.method_781(class_181.field_1232).method_781(class_181.field_1226));
	public static final class_176 field_1174 = method_759("advancement_reward", arg -> arg.method_781(class_181.field_1226).method_781(class_181.field_1232));
	public static final class_176 field_1177 = method_759(
		"generic",
		arg -> arg.method_781(class_181.field_1226)
				.method_781(class_181.field_1233)
				.method_781(class_181.field_1231)
				.method_781(class_181.field_1230)
				.method_781(class_181.field_1227)
				.method_781(class_181.field_1232)
				.method_781(class_181.field_1224)
				.method_781(class_181.field_1228)
				.method_781(class_181.field_1229)
				.method_781(class_181.field_1225)
	);
	public static final class_176 field_1172 = method_759(
		"block",
		arg -> arg.method_781(class_181.field_1224)
				.method_781(class_181.field_1232)
				.method_781(class_181.field_1229)
				.method_780(class_181.field_1226)
				.method_780(class_181.field_1228)
				.method_780(class_181.field_1225)
	);

	private static class_176 method_759(String string, Consumer<class_176.class_177> consumer) {
		class_176.class_177 lv = new class_176.class_177();
		consumer.accept(lv);
		class_176 lv2 = lv.method_782();
		class_2960 lv3 = new class_2960(string);
		class_176 lv4 = field_1178.put(lv3, lv2);
		if (lv4 != null) {
			throw new IllegalStateException("Loot table parameter set " + lv3 + " is already registered");
		} else {
			return lv2;
		}
	}

	@Nullable
	public static class_176 method_757(class_2960 arg) {
		return (class_176)field_1178.get(arg);
	}

	@Nullable
	public static class_2960 method_762(class_176 arg) {
		return (class_2960)field_1178.inverse().get(arg);
	}
}
