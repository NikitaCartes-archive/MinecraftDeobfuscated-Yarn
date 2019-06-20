package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public class class_2196 implements ArgumentType<class_2196.class_2197> {
	private static final Collection<String> field_9876 = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

	public static class_2196 method_9340() {
		return new class_2196();
	}

	public static class_2561 method_9339(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2196.class_2197>getArgument(string, class_2196.class_2197.class)
			.method_9341(commandContext.getSource(), commandContext.getSource().method_9259(2));
	}

	public class_2196.class_2197 method_9338(StringReader stringReader) throws CommandSyntaxException {
		return class_2196.class_2197.method_9342(stringReader, true);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9876;
	}

	public static class class_2197 {
		private final String field_9877;
		private final class_2196.class_2198[] field_9878;

		public class_2197(String string, class_2196.class_2198[] args) {
			this.field_9877 = string;
			this.field_9878 = args;
		}

		public class_2561 method_9341(class_2168 arg, boolean bl) throws CommandSyntaxException {
			if (this.field_9878.length != 0 && bl) {
				class_2561 lv = new class_2585(this.field_9877.substring(0, this.field_9878[0].method_9343()));
				int i = this.field_9878[0].method_9343();

				for (class_2196.class_2198 lv2 : this.field_9878) {
					class_2561 lv3 = lv2.method_9345(arg);
					if (i < lv2.method_9343()) {
						lv.method_10864(this.field_9877.substring(i, lv2.method_9343()));
					}

					if (lv3 != null) {
						lv.method_10852(lv3);
					}

					i = lv2.method_9344();
				}

				if (i < this.field_9877.length()) {
					lv.method_10864(this.field_9877.substring(i, this.field_9877.length()));
				}

				return lv;
			} else {
				return new class_2585(this.field_9877);
			}
		}

		public static class_2196.class_2197 method_9342(StringReader stringReader, boolean bl) throws CommandSyntaxException {
			String string = stringReader.getString().substring(stringReader.getCursor(), stringReader.getTotalLength());
			if (!bl) {
				stringReader.setCursor(stringReader.getTotalLength());
				return new class_2196.class_2197(string, new class_2196.class_2198[0]);
			} else {
				List<class_2196.class_2198> list = Lists.<class_2196.class_2198>newArrayList();
				int i = stringReader.getCursor();

				while (true) {
					int j;
					class_2300 lv2;
					while (true) {
						if (!stringReader.canRead()) {
							return new class_2196.class_2197(string, (class_2196.class_2198[])list.toArray(new class_2196.class_2198[list.size()]));
						}

						if (stringReader.peek() == '@') {
							j = stringReader.getCursor();

							try {
								class_2303 lv = new class_2303(stringReader);
								lv2 = lv.method_9882();
								break;
							} catch (CommandSyntaxException var8) {
								if (var8.getType() != class_2303.field_10844 && var8.getType() != class_2303.field_10853) {
									throw var8;
								}

								stringReader.setCursor(j + 1);
							}
						} else {
							stringReader.skip();
						}
					}

					list.add(new class_2196.class_2198(j - i, stringReader.getCursor() - i, lv2));
				}
			}
		}
	}

	public static class class_2198 {
		private final int field_9880;
		private final int field_9879;
		private final class_2300 field_9881;

		public class_2198(int i, int j, class_2300 arg) {
			this.field_9880 = i;
			this.field_9879 = j;
			this.field_9881 = arg;
		}

		public int method_9343() {
			return this.field_9880;
		}

		public int method_9344() {
			return this.field_9879;
		}

		@Nullable
		public class_2561 method_9345(class_2168 arg) throws CommandSyntaxException {
			return class_2300.method_9822(this.field_9881.method_9816(arg));
		}
	}
}
