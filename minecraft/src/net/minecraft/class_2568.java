package net.minecraft;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class class_2568 {
	private final class_2568.class_2569 field_11756;
	private final class_2561 field_11755;

	public class_2568(class_2568.class_2569 arg, class_2561 arg2) {
		this.field_11756 = arg;
		this.field_11755 = arg2;
	}

	public class_2568.class_2569 method_10892() {
		return this.field_11756;
	}

	public class_2561 method_10891() {
		return this.field_11755;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_2568 lv = (class_2568)object;
			if (this.field_11756 != lv.field_11756) {
				return false;
			} else {
				return this.field_11755 != null ? this.field_11755.equals(lv.field_11755) : lv.field_11755 == null;
			}
		} else {
			return false;
		}
	}

	public String toString() {
		return "HoverEvent{action=" + this.field_11756 + ", value='" + this.field_11755 + '\'' + '}';
	}

	public int hashCode() {
		int i = this.field_11756.hashCode();
		return 31 * i + (this.field_11755 != null ? this.field_11755.hashCode() : 0);
	}

	public static enum class_2569 {
		field_11762("show_text", true),
		field_11757("show_item", true),
		field_11761("show_entity", true);

		private static final Map<String, class_2568.class_2569> field_11758 = (Map<String, class_2568.class_2569>)Arrays.stream(values())
			.collect(Collectors.toMap(class_2568.class_2569::method_10893, arg -> arg));
		private final boolean field_11759;
		private final String field_11763;

		private class_2569(String string2, boolean bl) {
			this.field_11763 = string2;
			this.field_11759 = bl;
		}

		public boolean method_10895() {
			return this.field_11759;
		}

		public String method_10893() {
			return this.field_11763;
		}

		public static class_2568.class_2569 method_10896(String string) {
			return (class_2568.class_2569)field_11758.get(string);
		}
	}
}
