package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class class_69 extends class_79 {
	protected final class_79[] field_982;
	private final class_64 field_983;

	protected class_69(class_79[] args, class_209[] args2) {
		super(args2);
		this.field_982 = args;
		this.field_983 = this.method_394(args);
	}

	@Override
	public void method_415(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		super.method_415(arg, function, set, arg2);
		if (this.field_982.length == 0) {
			arg.method_360("Empty children list");
		}

		for (int i = 0; i < this.field_982.length; i++) {
			this.field_982[i].method_415(arg.method_364(".entry[" + i + "]"), function, set, arg2);
		}
	}

	protected abstract class_64 method_394(class_64[] args);

	@Override
	public final boolean expand(class_47 arg, Consumer<class_82> consumer) {
		return !this.method_414(arg) ? false : this.field_983.expand(arg, consumer);
	}

	public static <T extends class_69> class_69.class_71<T> method_395(class_2960 arg, Class<T> class_, class_69.class_70<T> arg2) {
		return new class_69.class_71<T>(arg, class_) {
			@Override
			protected T method_398(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_79[] args, class_209[] args2) {
				return arg2.create(args, args2);
			}
		};
	}

	@FunctionalInterface
	public interface class_70<T extends class_69> {
		T create(class_79[] args, class_209[] args2);
	}

	public abstract static class class_71<T extends class_69> extends class_79.class_81<T> {
		public class_71(class_2960 arg, Class<T> class_) {
			super(arg, class_);
		}

		public void method_397(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("children", jsonSerializationContext.serialize(arg.field_982));
		}

		public final T method_396(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_79[] lvs = class_3518.method_15272(jsonObject, "children", jsonDeserializationContext, class_79[].class);
			return this.method_398(jsonObject, jsonDeserializationContext, lvs, args);
		}

		protected abstract T method_398(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_79[] args, class_209[] args2);
	}
}
