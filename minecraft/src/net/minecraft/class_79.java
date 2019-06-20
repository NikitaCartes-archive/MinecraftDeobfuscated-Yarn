package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class class_79 implements class_64 {
	protected final class_209[] field_988;
	private final Predicate<class_47> field_989;

	protected class_79(class_209[] args) {
		this.field_988 = args;
		this.field_989 = class_217.method_924(args);
	}

	public void method_415(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		for (int i = 0; i < this.field_988.length; i++) {
			this.field_988[i].method_292(arg.method_364(".condition[" + i + "]"), function, set, arg2);
		}
	}

	protected final boolean method_414(class_47 arg) {
		return this.field_989.test(arg);
	}

	public abstract static class class_80<T extends class_79.class_80<T>> implements class_192<T> {
		private final List<class_209> field_990 = Lists.<class_209>newArrayList();

		protected abstract T method_418();

		public T method_421(class_209.class_210 arg) {
			this.field_990.add(arg.build());
			return this.method_418();
		}

		public final T method_416() {
			return this.method_418();
		}

		protected class_209[] method_420() {
			return (class_209[])this.field_990.toArray(new class_209[0]);
		}

		public class_65.class_66 method_417(class_79.class_80<?> arg) {
			return new class_65.class_66(this, arg);
		}

		public abstract class_79 method_419();
	}

	public abstract static class class_81<T extends class_79> {
		private final class_2960 field_991;
		private final Class<T> field_992;

		protected class_81(class_2960 arg, Class<T> class_) {
			this.field_991 = arg;
			this.field_992 = class_;
		}

		public class_2960 method_423() {
			return this.field_991;
		}

		public Class<T> method_425() {
			return this.field_992;
		}

		public abstract void method_422(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext);

		public abstract T method_424(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args);
	}
}
