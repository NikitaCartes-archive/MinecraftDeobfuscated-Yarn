package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class class_2203 implements ArgumentType<class_2203.class_2209> {
	private static final Collection<String> field_9898 = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
	public static final SimpleCommandExceptionType field_9900 = new SimpleCommandExceptionType(new class_2588("arguments.nbtpath.node.invalid"));
	public static final DynamicCommandExceptionType field_9899 = new DynamicCommandExceptionType(
		object -> new class_2588("arguments.nbtpath.nothing_found", object)
	);

	public static class_2203 method_9360() {
		return new class_2203();
	}

	public static class_2203.class_2209 method_9358(CommandContext<class_2168> commandContext, String string) {
		return commandContext.getArgument(string, class_2203.class_2209.class);
	}

	public class_2203.class_2209 method_9362(StringReader stringReader) throws CommandSyntaxException {
		List<class_2203.class_2210> list = Lists.<class_2203.class_2210>newArrayList();
		int i = stringReader.getCursor();
		Object2IntMap<class_2203.class_2210> object2IntMap = new Object2IntOpenHashMap<>();
		boolean bl = true;

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			class_2203.class_2210 lv = method_9361(stringReader, bl);
			list.add(lv);
			object2IntMap.put(lv, stringReader.getCursor() - i);
			bl = false;
			if (stringReader.canRead()) {
				char c = stringReader.peek();
				if (c != ' ' && c != '[' && c != '{') {
					stringReader.expect('.');
				}
			}
		}

		return new class_2203.class_2209(
			stringReader.getString().substring(i, stringReader.getCursor()), (class_2203.class_2210[])list.toArray(new class_2203.class_2210[0]), object2IntMap
		);
	}

	private static class_2203.class_2210 method_9361(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		switch (stringReader.peek()) {
			case '"': {
				String string = stringReader.readString();
				return method_9352(stringReader, string);
			}
			case '[':
				stringReader.skip();
				int i = stringReader.peek();
				if (i == 123) {
					class_2487 lv2 = new class_2522(stringReader).method_10727();
					stringReader.expect(']');
					return new class_2203.class_2207(lv2);
				} else {
					if (i == 93) {
						stringReader.skip();
						return class_2203.class_2204.field_9901;
					}

					int j = stringReader.readInt();
					stringReader.expect(']');
					return new class_2203.class_2206(j);
				}
			case '{':
				if (!bl) {
					throw field_9900.createWithContext(stringReader);
				}

				class_2487 lv = new class_2522(stringReader).method_10727();
				return new class_2203.class_3707(lv);
			default: {
				String string = method_9357(stringReader);
				return method_9352(stringReader, string);
			}
		}
	}

	private static class_2203.class_2210 method_9352(StringReader stringReader, String string) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '{') {
			class_2487 lv = new class_2522(stringReader).method_10727();
			return new class_2203.class_2208(string, lv);
		} else {
			return new class_2203.class_2205(string);
		}
	}

	private static String method_9357(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && method_9355(stringReader.peek())) {
			stringReader.skip();
		}

		if (stringReader.getCursor() == i) {
			throw field_9900.createWithContext(stringReader);
		} else {
			return stringReader.getString().substring(i, stringReader.getCursor());
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_9898;
	}

	private static boolean method_9355(char c) {
		return c != ' ' && c != '"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
	}

	private static Predicate<class_2520> method_9359(class_2487 arg) {
		return arg2 -> class_2512.method_10687(arg, arg2, true);
	}

	static class class_2204 implements class_2203.class_2210 {
		public static final class_2203.class_2204 field_9901 = new class_2203.class_2204();

		private class_2204() {
		}

		@Override
		public void method_9378(class_2520 arg, List<class_2520> list) {
			if (arg instanceof class_2483) {
				list.addAll((class_2483)arg);
			}
		}

		@Override
		public void method_9380(class_2520 arg, Supplier<class_2520> supplier, List<class_2520> list) {
			if (arg instanceof class_2483) {
				class_2483<?> lv = (class_2483<?>)arg;
				if (lv.isEmpty()) {
					class_2520 lv2 = (class_2520)supplier.get();
					lv.method_10533(0, lv2);
					list.add(lv2);
				} else {
					list.addAll(lv);
				}
			}
		}

		@Override
		public class_2520 method_9382() {
			return new class_2499();
		}

		@Override
		public int method_9376(class_2520 arg, Supplier<class_2520> supplier) {
			if (!(arg instanceof class_2483)) {
				return 0;
			} else {
				class_2483<?> lv = (class_2483<?>)arg;
				int i = Math.max(1, lv.size());
				lv.clear();

				for (int j = 0; j < i; j++) {
					lv.method_10533(j, (class_2520)supplier.get());
				}

				return i;
			}
		}

		@Override
		public int method_9383(class_2520 arg) {
			if (arg instanceof class_2483) {
				class_2483<?> lv = (class_2483<?>)arg;
				int i = lv.size();
				if (i > 0) {
					lv.clear();
					return i;
				}
			}

			return 0;
		}
	}

	static class class_2205 implements class_2203.class_2210 {
		private final String field_9902;

		public class_2205(String string) {
			this.field_9902 = string;
		}

		@Override
		public void method_9378(class_2520 arg, List<class_2520> list) {
			if (arg instanceof class_2487) {
				class_2520 lv = ((class_2487)arg).method_10580(this.field_9902);
				if (lv != null) {
					list.add(lv);
				}
			}
		}

		@Override
		public void method_9380(class_2520 arg, Supplier<class_2520> supplier, List<class_2520> list) {
			if (arg instanceof class_2487) {
				class_2487 lv = (class_2487)arg;
				class_2520 lv2;
				if (lv.method_10545(this.field_9902)) {
					lv2 = lv.method_10580(this.field_9902);
				} else {
					lv2 = (class_2520)supplier.get();
					lv.method_10566(this.field_9902, lv2);
				}

				list.add(lv2);
			}
		}

		@Override
		public class_2520 method_9382() {
			return new class_2487();
		}

		@Override
		public int method_9376(class_2520 arg, Supplier<class_2520> supplier) {
			if (arg instanceof class_2487) {
				class_2487 lv = (class_2487)arg;
				lv.method_10566(this.field_9902, (class_2520)supplier.get());
				return 1;
			} else {
				return 0;
			}
		}

		@Override
		public int method_9383(class_2520 arg) {
			if (arg instanceof class_2487) {
				class_2487 lv = (class_2487)arg;
				if (lv.method_10545(this.field_9902)) {
					lv.method_10551(this.field_9902);
					return 1;
				}
			}

			return 0;
		}
	}

	static class class_2206 implements class_2203.class_2210 {
		private final int field_9903;

		public class_2206(int i) {
			this.field_9903 = i;
		}

		@Override
		public void method_9378(class_2520 arg, List<class_2520> list) {
			if (arg instanceof class_2483) {
				class_2483<?> lv = (class_2483<?>)arg;
				int i = lv.size();
				int j = this.field_9903 < 0 ? i + this.field_9903 : this.field_9903;
				if (0 <= j && j < i) {
					list.add(lv.method_10536(j));
				}
			}
		}

		@Override
		public void method_9380(class_2520 arg, Supplier<class_2520> supplier, List<class_2520> list) {
			this.method_9378(arg, list);
		}

		@Override
		public class_2520 method_9382() {
			return new class_2499();
		}

		@Override
		public int method_9376(class_2520 arg, Supplier<class_2520> supplier) {
			if (arg instanceof class_2483) {
				class_2483<?> lv = (class_2483<?>)arg;
				int i = lv.size();
				int j = this.field_9903 < 0 ? i + this.field_9903 : this.field_9903;
				if (0 <= j && j < i) {
					lv.method_10535(j, (class_2520)supplier.get());
					return 1;
				}
			}

			return 0;
		}

		@Override
		public int method_9383(class_2520 arg) {
			if (arg instanceof class_2483) {
				class_2483<?> lv = (class_2483<?>)arg;
				int i = lv.size();
				int j = this.field_9903 < 0 ? i + this.field_9903 : this.field_9903;
				if (0 <= j && j < i) {
					lv.method_10532(j);
					return 1;
				}
			}

			return 0;
		}
	}

	static class class_2207 implements class_2203.class_2210 {
		private final class_2487 field_9904;
		private final Predicate<class_2520> field_9905;

		public class_2207(class_2487 arg) {
			this.field_9904 = arg;
			this.field_9905 = class_2203.method_9359(arg);
		}

		@Override
		public void method_9378(class_2520 arg, List<class_2520> list) {
			if (arg instanceof class_2499) {
				class_2499 lv = (class_2499)arg;
				lv.stream().filter(this.field_9905).forEach(list::add);
			}
		}

		@Override
		public void method_9380(class_2520 arg, Supplier<class_2520> supplier, List<class_2520> list) {
			MutableBoolean mutableBoolean = new MutableBoolean();
			if (arg instanceof class_2499) {
				class_2499 lv = (class_2499)arg;
				lv.stream().filter(this.field_9905).forEach(argx -> {
					list.add(argx);
					mutableBoolean.setTrue();
				});
				if (mutableBoolean.isFalse()) {
					class_2487 lv2 = this.field_9904.method_10553();
					lv.method_10606(lv2);
					list.add(lv2);
				}
			}
		}

		@Override
		public class_2520 method_9382() {
			return new class_2499();
		}

		private int method_9364(class_2520 arg, BiConsumer<class_2499, Integer> biConsumer) {
			int i = 0;
			if (arg instanceof class_2499) {
				class_2499 lv = (class_2499)arg;

				for (int j = 0; j < lv.size(); j++) {
					if (this.field_9905.test(lv.method_10534(j))) {
						biConsumer.accept(lv, j);
						i++;
					}
				}
			}

			return i;
		}

		@Override
		public int method_9376(class_2520 arg, Supplier<class_2520> supplier) {
			return this.method_9364(arg, (argx, integer) -> argx.method_10535(integer, (class_2520)supplier.get()));
		}

		@Override
		public int method_9383(class_2520 arg) {
			return this.method_9364(arg, class_2499::method_10532);
		}
	}

	static class class_2208 implements class_2203.class_2210 {
		private final String field_9906;
		private final class_2487 field_9907;
		private final Predicate<class_2520> field_9908;

		public class_2208(String string, class_2487 arg) {
			this.field_9906 = string;
			this.field_9907 = arg;
			this.field_9908 = class_2203.method_9359(arg);
		}

		@Override
		public void method_9378(class_2520 arg, List<class_2520> list) {
			if (arg instanceof class_2487) {
				class_2520 lv = ((class_2487)arg).method_10580(this.field_9906);
				if (this.field_9908.test(lv)) {
					list.add(lv);
				}
			}
		}

		@Override
		public void method_9380(class_2520 arg, Supplier<class_2520> supplier, List<class_2520> list) {
			if (arg instanceof class_2487) {
				class_2487 lv = (class_2487)arg;
				class_2520 lv2 = lv.method_10580(this.field_9906);
				if (lv2 == null) {
					class_2520 var6 = this.field_9907.method_10553();
					lv.method_10566(this.field_9906, var6);
					list.add(var6);
				} else if (this.field_9908.test(lv2)) {
					list.add(lv2);
				}
			}
		}

		@Override
		public class_2520 method_9382() {
			return new class_2487();
		}

		@Override
		public int method_9376(class_2520 arg, Supplier<class_2520> supplier) {
			if (arg instanceof class_2487) {
				class_2487 lv = (class_2487)arg;
				class_2520 lv2 = lv.method_10580(this.field_9906);
				if (this.field_9908.test(lv2)) {
					lv.method_10566(this.field_9906, (class_2520)supplier.get());
					return 1;
				}
			}

			return 0;
		}

		@Override
		public int method_9383(class_2520 arg) {
			if (arg instanceof class_2487) {
				class_2487 lv = (class_2487)arg;
				class_2520 lv2 = lv.method_10580(this.field_9906);
				if (this.field_9908.test(lv2)) {
					lv.method_10551(this.field_9906);
					return 1;
				}
			}

			return 0;
		}
	}

	public static class class_2209 {
		private final String field_9909;
		private final Object2IntMap<class_2203.class_2210> field_9910;
		private final class_2203.class_2210[] field_9911;

		public class_2209(String string, class_2203.class_2210[] args, Object2IntMap<class_2203.class_2210> object2IntMap) {
			this.field_9909 = string;
			this.field_9911 = args;
			this.field_9910 = object2IntMap;
		}

		public List<class_2520> method_9366(class_2520 arg) throws CommandSyntaxException {
			List<class_2520> list = Collections.singletonList(arg);

			for (class_2203.class_2210 lv : this.field_9911) {
				list = lv.method_9381(list);
				if (list.isEmpty()) {
					throw this.method_9375(lv);
				}
			}

			return list;
		}

		public int method_9374(class_2520 arg) {
			List<class_2520> list = Collections.singletonList(arg);

			for (class_2203.class_2210 lv : this.field_9911) {
				list = lv.method_9381(list);
				if (list.isEmpty()) {
					return 0;
				}
			}

			return list.size();
		}

		private List<class_2520> method_9369(class_2520 arg) throws CommandSyntaxException {
			List<class_2520> list = Collections.singletonList(arg);

			for (int i = 0; i < this.field_9911.length - 1; i++) {
				class_2203.class_2210 lv = this.field_9911[i];
				int j = i + 1;
				list = lv.method_9377(list, this.field_9911[j]::method_9382);
				if (list.isEmpty()) {
					throw this.method_9375(lv);
				}
			}

			return list;
		}

		public List<class_2520> method_9367(class_2520 arg, Supplier<class_2520> supplier) throws CommandSyntaxException {
			List<class_2520> list = this.method_9369(arg);
			class_2203.class_2210 lv = this.field_9911[this.field_9911.length - 1];
			return lv.method_9377(list, supplier);
		}

		private static int method_9371(List<class_2520> list, Function<class_2520, Integer> function) {
			return (Integer)list.stream().map(function).reduce(0, (integer, integer2) -> integer + integer2);
		}

		public int method_9368(class_2520 arg, Supplier<class_2520> supplier) throws CommandSyntaxException {
			List<class_2520> list = this.method_9369(arg);
			class_2203.class_2210 lv = this.field_9911[this.field_9911.length - 1];
			int i = method_9371(list, arg2 -> lv.method_9376(arg2, supplier));
			if (i == 0) {
				throw class_2203.field_9899.create(this.field_9909);
			} else {
				return i;
			}
		}

		public int method_9372(class_2520 arg) {
			List<class_2520> list = Collections.singletonList(arg);

			for (int i = 0; i < this.field_9911.length - 1; i++) {
				list = this.field_9911[i].method_9381(list);
			}

			class_2203.class_2210 lv = this.field_9911[this.field_9911.length - 1];
			return method_9371(list, lv::method_9383);
		}

		private CommandSyntaxException method_9375(class_2203.class_2210 arg) {
			int i = this.field_9910.getInt(arg);
			return class_2203.field_9899.create(this.field_9909.substring(0, i));
		}

		public String toString() {
			return this.field_9909;
		}
	}

	interface class_2210 {
		void method_9378(class_2520 arg, List<class_2520> list);

		void method_9380(class_2520 arg, Supplier<class_2520> supplier, List<class_2520> list);

		class_2520 method_9382();

		int method_9376(class_2520 arg, Supplier<class_2520> supplier);

		int method_9383(class_2520 arg);

		default List<class_2520> method_9381(List<class_2520> list) {
			return this.method_9384(list, this::method_9378);
		}

		default List<class_2520> method_9377(List<class_2520> list, Supplier<class_2520> supplier) {
			return this.method_9384(list, (arg, listx) -> this.method_9380(arg, supplier, listx));
		}

		default List<class_2520> method_9384(List<class_2520> list, BiConsumer<class_2520, List<class_2520>> biConsumer) {
			List<class_2520> list2 = Lists.<class_2520>newArrayList();

			for (class_2520 lv : list) {
				biConsumer.accept(lv, list2);
			}

			return list2;
		}
	}

	static class class_3707 implements class_2203.class_2210 {
		private final Predicate<class_2520> field_16319;

		public class_3707(class_2487 arg) {
			this.field_16319 = class_2203.method_9359(arg);
		}

		@Override
		public void method_9378(class_2520 arg, List<class_2520> list) {
			if (arg instanceof class_2487 && this.field_16319.test(arg)) {
				list.add(arg);
			}
		}

		@Override
		public void method_9380(class_2520 arg, Supplier<class_2520> supplier, List<class_2520> list) {
			this.method_9378(arg, list);
		}

		@Override
		public class_2520 method_9382() {
			return new class_2487();
		}

		@Override
		public int method_9376(class_2520 arg, Supplier<class_2520> supplier) {
			return 0;
		}

		@Override
		public int method_9383(class_2520 arg) {
			return 0;
		}
	}
}
