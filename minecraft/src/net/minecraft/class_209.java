package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Predicate;

@FunctionalInterface
public interface class_209 extends class_46, Predicate<class_47> {
	@FunctionalInterface
	public interface class_210 {
		class_209 build();

		default class_209.class_210 method_16780() {
			return class_207.method_889(this);
		}

		default class_186.class_187 method_893(class_209.class_210 arg) {
			return class_186.method_826(this, arg);
		}
	}

	public abstract static class class_211<T extends class_209> {
		private final class_2960 field_1284;
		private final Class<T> field_1285;

		protected class_211(class_2960 arg, Class<T> class_) {
			this.field_1284 = arg;
			this.field_1285 = class_;
		}

		public class_2960 method_894() {
			return this.field_1284;
		}

		public Class<T> method_897() {
			return this.field_1285;
		}

		public abstract void method_895(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext);

		public abstract T method_896(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);
	}
}
