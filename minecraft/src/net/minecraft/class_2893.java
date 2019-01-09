package net.minecraft;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class class_2893 {
	public static enum class_2894 {
		field_13169("air"),
		field_13166("liquid");

		private static final Map<String, class_2893.class_2894> field_13168 = (Map<String, class_2893.class_2894>)Arrays.stream(values())
			.collect(Collectors.toMap(class_2893.class_2894::method_12581, arg -> arg));
		private final String field_13167;

		private class_2894(String string2) {
			this.field_13167 = string2;
		}

		public String method_12581() {
			return this.field_13167;
		}
	}

	public static enum class_2895 {
		field_13174("raw_generation"),
		field_13171("local_modifications"),
		field_13172("underground_structures"),
		field_13173("surface_structures"),
		field_13176("underground_ores"),
		field_13177("underground_decoration"),
		field_13178("vegetal_decoration"),
		field_13179("top_layer_modification");

		private static final Map<String, class_2893.class_2895> field_13175 = (Map<String, class_2893.class_2895>)Arrays.stream(values())
			.collect(Collectors.toMap(class_2893.class_2895::method_12582, arg -> arg));
		private final String field_13180;

		private class_2895(String string2) {
			this.field_13180 = string2;
		}

		public String method_12582() {
			return this.field_13180;
		}
	}
}
