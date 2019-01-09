package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;

public abstract class class_120 implements class_117 {
	protected final class_209[] field_1047;
	private final Predicate<class_47> field_1048;

	protected class_120(class_209[] args) {
		this.field_1047 = args;
		this.field_1048 = class_217.method_924(args);
	}

	public final class_1799 method_521(class_1799 arg, class_47 arg2) {
		return this.field_1048.test(arg2) ? this.method_522(arg, arg2) : arg;
	}

	protected abstract class_1799 method_522(class_1799 arg, class_47 arg2);

	@Override
	public void method_292(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		class_117.super.method_292(arg, function, set, arg2);

		for (int i = 0; i < this.field_1047.length; i++) {
			this.field_1047[i].method_292(arg.method_364(".conditions[" + i + "]"), function, set, arg2);
		}
	}

	protected static class_120.class_121<?> method_520(Function<class_209[], class_117> function) {
		return new class_120.class_122(function);
	}

	public abstract static class class_121<T extends class_120.class_121<T>> implements class_117.class_118, class_192<T> {
		private final List<class_209> field_1049 = Lists.<class_209>newArrayList();

		public T method_524(class_209.class_210 arg) {
			this.field_1049.add(arg.build());
			return this.method_523();
		}

		public final T method_525() {
			return this.method_523();
		}

		protected abstract T method_523();

		protected class_209[] method_526() {
			return (class_209[])this.field_1049.toArray(new class_209[0]);
		}
	}

	static final class class_122 extends class_120.class_121<class_120.class_122> {
		private final Function<class_209[], class_117> field_1050;

		public class_122(Function<class_209[], class_117> function) {
			this.field_1050 = function;
		}

		protected class_120.class_122 method_527() {
			return this;
		}

		@Override
		public class_117 method_515() {
			return (class_117)this.field_1050.apply(this.method_526());
		}
	}

	public abstract static class class_123<T extends class_120> extends class_117.class_119<T> {
		public class_123(class_2960 arg, Class<T> class_) {
			super(arg, class_);
		}

		public void method_529(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext) {
			if (!ArrayUtils.isEmpty((Object[])arg.field_1047)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(arg.field_1047));
			}
		}

		public final T method_528(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_209[] lvs = class_3518.method_15283(jsonObject, "conditions", new class_209[0], jsonDeserializationContext, class_209[].class);
			return this.method_530(jsonObject, jsonDeserializationContext, lvs);
		}

		public abstract T method_530(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args);
	}
}
