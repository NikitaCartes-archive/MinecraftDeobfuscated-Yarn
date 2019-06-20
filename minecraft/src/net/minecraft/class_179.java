package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

public interface class_179<T extends class_184> {
	class_2960 method_794();

	void method_792(class_2985 arg, class_179.class_180<T> arg2);

	void method_793(class_2985 arg, class_179.class_180<T> arg2);

	void method_791(class_2985 arg);

	T method_795(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);

	public static class class_180<T extends class_184> {
		private final T field_1223;
		private final class_161 field_1222;
		private final String field_1221;

		public class_180(T arg, class_161 arg2, String string) {
			this.field_1223 = arg;
			this.field_1222 = arg2;
			this.field_1221 = string;
		}

		public T method_797() {
			return this.field_1223;
		}

		public void method_796(class_2985 arg) {
			arg.method_12878(this.field_1222, this.field_1221);
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				class_179.class_180<?> lv = (class_179.class_180<?>)object;
				if (!this.field_1223.equals(lv.field_1223)) {
					return false;
				} else {
					return !this.field_1222.equals(lv.field_1222) ? false : this.field_1221.equals(lv.field_1221);
				}
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.field_1223.hashCode();
			i = 31 * i + this.field_1222.hashCode();
			return 31 * i + this.field_1221.hashCode();
		}
	}
}
