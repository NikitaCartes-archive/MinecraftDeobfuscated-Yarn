package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.lang3.ArrayUtils;

public abstract class class_85 extends class_79 {
	protected final int field_995;
	protected final int field_994;
	protected final class_117[] field_996;
	private final BiFunction<class_1799, class_47, class_1799> field_997;
	private final class_82 field_998 = new class_85.class_88() {
		@Override
		public void method_426(Consumer<class_1799> consumer, class_47 arg) {
			class_85.this.method_433(class_117.method_513(class_85.this.field_997, consumer, arg), arg);
		}
	};

	protected class_85(int i, int j, class_209[] args, class_117[] args2) {
		super(args);
		this.field_995 = i;
		this.field_994 = j;
		this.field_996 = args2;
		this.field_997 = class_131.method_594(args2);
	}

	@Override
	public void method_415(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		super.method_415(arg, function, set, arg2);

		for (int i = 0; i < this.field_996.length; i++) {
			this.field_996[i].method_292(arg.method_364(".functions[" + i + "]"), function, set, arg2);
		}
	}

	protected abstract void method_433(Consumer<class_1799> consumer, class_47 arg);

	@Override
	public boolean expand(class_47 arg, Consumer<class_82> consumer) {
		if (this.method_414(arg)) {
			consumer.accept(this.field_998);
			return true;
		} else {
			return false;
		}
	}

	public static class_85.class_86<?> method_434(class_85.class_89 arg) {
		return new class_85.class_87(arg);
	}

	public abstract static class class_86<T extends class_85.class_86<T>> extends class_79.class_80<T> implements class_116<T> {
		protected int field_1001 = 1;
		protected int field_1000 = 0;
		private final List<class_117> field_999 = Lists.<class_117>newArrayList();

		public T method_438(class_117.class_118 arg) {
			this.field_999.add(arg.method_515());
			return this.method_418();
		}

		protected class_117[] method_439() {
			return (class_117[])this.field_999.toArray(new class_117[0]);
		}

		public T method_437(int i) {
			this.field_1001 = i;
			return this.method_418();
		}

		public T method_436(int i) {
			this.field_1000 = i;
			return this.method_418();
		}
	}

	static class class_87 extends class_85.class_86<class_85.class_87> {
		private final class_85.class_89 field_1003;

		public class_87(class_85.class_89 arg) {
			this.field_1003 = arg;
		}

		protected class_85.class_87 method_440() {
			return this;
		}

		@Override
		public class_79 method_419() {
			return this.field_1003.build(this.field_1001, this.field_1000, this.method_420(), this.method_439());
		}
	}

	public abstract class class_88 implements class_82 {
		protected class_88() {
		}

		@Override
		public int method_427(float f) {
			return Math.max(class_3532.method_15375((float)class_85.this.field_995 + (float)class_85.this.field_994 * f), 0);
		}
	}

	@FunctionalInterface
	public interface class_89 {
		class_85 build(int i, int j, class_209[] args, class_117[] args2);
	}

	public abstract static class class_90<T extends class_85> extends class_79.class_81<T> {
		public class_90(class_2960 arg, Class<T> class_) {
			super(arg, class_);
		}

		public void method_442(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext) {
			if (arg.field_995 != 1) {
				jsonObject.addProperty("weight", arg.field_995);
			}

			if (arg.field_994 != 0) {
				jsonObject.addProperty("quality", arg.field_994);
			}

			if (!ArrayUtils.isEmpty((Object[])arg.field_996)) {
				jsonObject.add("functions", jsonSerializationContext.serialize(arg.field_996));
			}
		}

		public final T method_441(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			int i = class_3518.method_15282(jsonObject, "weight", 1);
			int j = class_3518.method_15282(jsonObject, "quality", 0);
			class_117[] lvs = class_3518.method_15283(jsonObject, "functions", new class_117[0], jsonDeserializationContext, class_117[].class);
			return this.method_443(jsonObject, jsonDeserializationContext, i, j, args, lvs);
		}

		protected abstract T method_443(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_209[] args, class_117[] args2
		);
	}
}
