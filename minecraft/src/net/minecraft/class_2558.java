package net.minecraft;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class class_2558 {
	private final class_2558.class_2559 field_11741;
	private final String field_11740;

	public class_2558(class_2558.class_2559 arg, String string) {
		this.field_11741 = arg;
		this.field_11740 = string;
	}

	public class_2558.class_2559 method_10845() {
		return this.field_11741;
	}

	public String method_10844() {
		return this.field_11740;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_2558 lv = (class_2558)object;
			if (this.field_11741 != lv.field_11741) {
				return false;
			} else {
				return this.field_11740 != null ? this.field_11740.equals(lv.field_11740) : lv.field_11740 == null;
			}
		} else {
			return false;
		}
	}

	public String toString() {
		return "ClickEvent{action=" + this.field_11741 + ", value='" + this.field_11740 + '\'' + '}';
	}

	public int hashCode() {
		int i = this.field_11741.hashCode();
		return 31 * i + (this.field_11740 != null ? this.field_11740.hashCode() : 0);
	}

	public static enum class_2559 {
		field_11749("open_url", true),
		field_11746("open_file", false),
		field_11750("run_command", true),
		field_11745("suggest_command", true),
		field_11748("change_page", true);

		private static final Map<String, class_2558.class_2559> field_11743 = (Map<String, class_2558.class_2559>)Arrays.stream(values())
			.collect(Collectors.toMap(class_2558.class_2559::method_10846, arg -> arg));
		private final boolean field_11744;
		private final String field_11742;

		private class_2559(String string2, boolean bl) {
			this.field_11742 = string2;
			this.field_11744 = bl;
		}

		public boolean method_10847() {
			return this.field_11744;
		}

		public String method_10846() {
			return this.field_11742;
		}

		public static class_2558.class_2559 method_10848(String string) {
			return (class_2558.class_2559)field_11743.get(string);
		}
	}
}
