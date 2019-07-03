package net.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4450 {
	static List<String> method_21575(String string) {
		return Arrays.asList(string.split("\\n"));
	}

	public static List<class_4450.class_4451> method_21578(String string, class_4450.class_4452... args) {
		return method_21577(string, Arrays.asList(args));
	}

	private static List<class_4450.class_4451> method_21577(String string, List<class_4450.class_4452> list) {
		List<String> list2 = method_21575(string);
		return method_21579(list2, list);
	}

	private static List<class_4450.class_4451> method_21579(List<String> list, List<class_4450.class_4452> list2) {
		int i = 0;
		ArrayList<class_4450.class_4451> arrayList = new ArrayList();

		for (String string : list) {
			List<class_4450.class_4452> list3 = new ArrayList();

			for (String string2 : method_21576(string, "%link")) {
				if (string2.equals("%link")) {
					list3.add(list2.get(i++));
				} else {
					list3.add(class_4450.class_4452.method_21581(string2));
				}
			}

			arrayList.add(new class_4450.class_4451(list3));
		}

		return arrayList;
	}

	public static List<String> method_21576(String string, String string2) {
		if (string2.isEmpty()) {
			throw new IllegalArgumentException("Delimiter cannot be the empty string");
		} else {
			List<String> list = new ArrayList();
			int i = 0;

			int j;
			while ((j = string.indexOf(string2, i)) != -1) {
				if (j > i) {
					list.add(string.substring(i, j));
				}

				list.add(string2);
				i = j + string2.length();
			}

			if (i < string.length()) {
				list.add(string.substring(i));
			}

			return list;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4451 {
		public final List<class_4450.class_4452> field_20266;

		class_4451(List<class_4450.class_4452> list) {
			this.field_20266 = list;
		}

		public String toString() {
			return "Line{segments=" + this.field_20266 + '}';
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				class_4450.class_4451 lv = (class_4450.class_4451)object;
				return Objects.equals(this.field_20266, lv.field_20266);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_20266});
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4452 {
		final String field_20267;
		final String field_20268;
		final String field_20269;

		private class_4452(String string) {
			this.field_20267 = string;
			this.field_20268 = null;
			this.field_20269 = null;
		}

		private class_4452(String string, String string2, String string3) {
			this.field_20267 = string;
			this.field_20268 = string2;
			this.field_20269 = string3;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				class_4450.class_4452 lv = (class_4450.class_4452)object;
				return Objects.equals(this.field_20267, lv.field_20267)
					&& Objects.equals(this.field_20268, lv.field_20268)
					&& Objects.equals(this.field_20269, lv.field_20269);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_20267, this.field_20268, this.field_20269});
		}

		public String toString() {
			return "Segment{fullText='" + this.field_20267 + '\'' + ", linkTitle='" + this.field_20268 + '\'' + ", linkUrl='" + this.field_20269 + '\'' + '}';
		}

		public String method_21580() {
			return this.method_21583() ? this.field_20268 : this.field_20267;
		}

		public boolean method_21583() {
			return this.field_20268 != null;
		}

		public String method_21584() {
			if (!this.method_21583()) {
				throw new IllegalStateException("Not a link: " + this);
			} else {
				return this.field_20269;
			}
		}

		public static class_4450.class_4452 method_21582(String string, String string2) {
			return new class_4450.class_4452(null, string, string2);
		}

		static class_4450.class_4452 method_21581(String string) {
			return new class_4450.class_4452(string);
		}
	}
}
