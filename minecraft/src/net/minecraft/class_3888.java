package net.minecraft;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3888 {
	public static final class_3890 field_17158 = new class_3890();
	private final class_3888.class_3889 field_17159;

	public class_3888(class_3888.class_3889 arg) {
		this.field_17159 = arg;
	}

	public class_3888.class_3889 method_17167() {
		return this.field_17159;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_3889 {
		field_17160("none"),
		field_17161("partial"),
		field_17162("full");

		private static final Map<String, class_3888.class_3889> field_17163 = (Map<String, class_3888.class_3889>)Arrays.stream(values())
			.collect(Collectors.toMap(class_3888.class_3889::method_17168, arg -> arg));
		private final String field_17164;

		private class_3889(String string2) {
			this.field_17164 = string2;
		}

		public String method_17168() {
			return this.field_17164;
		}

		public static class_3888.class_3889 method_17170(String string) {
			return (class_3888.class_3889)field_17163.getOrDefault(string, field_17160);
		}
	}
}
