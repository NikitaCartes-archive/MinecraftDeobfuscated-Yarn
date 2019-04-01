package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public class class_2158 {
	private final class_2158.class_2161[] field_9805;
	private final class_2960 field_9806;

	public class_2158(class_2960 arg, class_2158.class_2161[] args) {
		this.field_9806 = arg;
		this.field_9805 = args;
	}

	public class_2960 method_9194() {
		return this.field_9806;
	}

	public class_2158.class_2161[] method_9193() {
		return this.field_9805;
	}

	public static class_2158 method_9195(class_2960 arg, class_2991 arg2, List<String> list) {
		List<class_2158.class_2161> list2 = Lists.<class_2158.class_2161>newArrayListWithCapacity(list.size());

		for (int i = 0; i < list.size(); i++) {
			int j = i + 1;
			String string = ((String)list.get(i)).trim();
			StringReader stringReader = new StringReader(string);
			if (stringReader.canRead() && stringReader.peek() != '#') {
				if (stringReader.peek() == '/') {
					stringReader.skip();
					if (stringReader.peek() == '/') {
						throw new IllegalArgumentException("Unknown or invalid command '" + string + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
					}

					String string2 = stringReader.readUnquotedString();
					throw new IllegalArgumentException(
						"Unknown or invalid command '" + string + "' on line " + j + " (did you mean '" + string2 + "'? Do not use a preceding forwards slash.)"
					);
				}

				try {
					ParseResults<class_2168> parseResults = arg2.method_12907().method_3734().method_9235().parse(stringReader, arg2.method_12899());
					if (parseResults.getReader().canRead()) {
						if (parseResults.getExceptions().size() == 1) {
							throw (CommandSyntaxException)parseResults.getExceptions().values().iterator().next();
						}

						if (parseResults.getContext().getRange().isEmpty()) {
							throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader());
						}

						throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults.getReader());
					}

					list2.add(new class_2158.class_2160(parseResults));
				} catch (CommandSyntaxException var9) {
					throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + var9.getMessage());
				}
			}
		}

		return new class_2158(arg, (class_2158.class_2161[])list2.toArray(new class_2158.class_2161[0]));
	}

	public static class class_2159 {
		public static final class_2158.class_2159 field_9809 = new class_2158.class_2159((class_2960)null);
		@Nullable
		private final class_2960 field_9807;
		private boolean field_9810;
		private Optional<class_2158> field_9808 = Optional.empty();

		public class_2159(@Nullable class_2960 arg) {
			this.field_9807 = arg;
		}

		public class_2159(class_2158 arg) {
			this.field_9810 = true;
			this.field_9807 = null;
			this.field_9808 = Optional.of(arg);
		}

		public Optional<class_2158> method_9196(class_2991 arg) {
			if (!this.field_9810) {
				if (this.field_9807 != null) {
					this.field_9808 = arg.method_12905(this.field_9807);
				}

				this.field_9810 = true;
			}

			return this.field_9808;
		}

		@Nullable
		public class_2960 method_9197() {
			return (class_2960)this.field_9808.map(arg -> arg.field_9806).orElse(this.field_9807);
		}
	}

	public static class class_2160 implements class_2158.class_2161 {
		private final ParseResults<class_2168> field_9811;

		public class_2160(ParseResults<class_2168> parseResults) {
			this.field_9811 = parseResults;
		}

		@Override
		public void method_9198(class_2991 arg, class_2168 arg2, ArrayDeque<class_2991.class_2992> arrayDeque, int i) throws CommandSyntaxException {
			arg.method_12900().execute(new ParseResults<>(this.field_9811.getContext().withSource(arg2), this.field_9811.getReader(), this.field_9811.getExceptions()));
		}

		public String toString() {
			return this.field_9811.getReader().getString();
		}
	}

	public interface class_2161 {
		void method_9198(class_2991 arg, class_2168 arg2, ArrayDeque<class_2991.class_2992> arrayDeque, int i) throws CommandSyntaxException;
	}

	public static class class_2162 implements class_2158.class_2161 {
		private final class_2158.class_2159 field_9812;

		public class_2162(class_2158 arg) {
			this.field_9812 = new class_2158.class_2159(arg);
		}

		@Override
		public void method_9198(class_2991 arg, class_2168 arg2, ArrayDeque<class_2991.class_2992> arrayDeque, int i) {
			this.field_9812.method_9196(arg).ifPresent(arg3 -> {
				class_2158.class_2161[] lvs = arg3.method_9193();
				int j = i - arrayDeque.size();
				int k = Math.min(lvs.length, j);

				for (int l = k - 1; l >= 0; l--) {
					arrayDeque.addFirst(new class_2991.class_2992(arg, arg2, lvs[l]));
				}
			});
		}

		public String toString() {
			return "function " + this.field_9812.method_9197();
		}
	}
}
