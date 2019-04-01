package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface class_117 extends class_46, BiFunction<class_1799, class_47, class_1799> {
	static Consumer<class_1799> method_513(BiFunction<class_1799, class_47, class_1799> biFunction, Consumer<class_1799> consumer, class_47 arg) {
		return arg2 -> consumer.accept(biFunction.apply(arg2, arg));
	}

	public interface class_118 {
		class_117 method_515();
	}

	public abstract static class class_119<T extends class_117> {
		private final class_2960 field_1045;
		private final Class<T> field_1046;

		protected class_119(class_2960 arg, Class<T> class_) {
			this.field_1045 = arg;
			this.field_1046 = class_;
		}

		public class_2960 method_518() {
			return this.field_1045;
		}

		public Class<T> method_519() {
			return this.field_1046;
		}

		public abstract void method_516(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext);

		public abstract T method_517(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);
	}
}
