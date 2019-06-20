package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2293 implements ArgumentType<class_2293.class_2295> {
	private static final Collection<String> field_10812 = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
	private static final DynamicCommandExceptionType field_10811 = new DynamicCommandExceptionType(object -> new class_2588("arguments.item.tag.unknown", object));

	public static class_2293 method_9801() {
		return new class_2293();
	}

	public class_2293.class_2295 method_9800(StringReader stringReader) throws CommandSyntaxException {
		class_2291 lv = new class_2291(stringReader, true).method_9789();
		if (lv.method_9786() != null) {
			class_2293.class_2294 lv2 = new class_2293.class_2294(lv.method_9786(), lv.method_9797());
			return commandContext -> lv2;
		} else {
			class_2960 lv3 = lv.method_9790();
			return commandContext -> {
				class_3494<class_1792> lvx = commandContext.getSource().method_9211().method_3801().method_15201().method_15193(lv3);
				if (lvx == null) {
					throw field_10811.create(lv3.toString());
				} else {
					return new class_2293.class_2296(lvx, lv.method_9797());
				}
			};
		}
	}

	public static Predicate<class_1799> method_9804(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2293.class_2295>getArgument(string, class_2293.class_2295.class).create(commandContext);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_2291 lv = new class_2291(stringReader, true);

		try {
			lv.method_9789();
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_9793(suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_10812;
	}

	static class class_2294 implements Predicate<class_1799> {
		private final class_1792 field_10813;
		@Nullable
		private final class_2487 field_10814;

		public class_2294(class_1792 arg, @Nullable class_2487 arg2) {
			this.field_10813 = arg;
			this.field_10814 = arg2;
		}

		public boolean method_9806(class_1799 arg) {
			return arg.method_7909() == this.field_10813 && class_2512.method_10687(this.field_10814, arg.method_7969(), true);
		}
	}

	public interface class_2295 {
		Predicate<class_1799> create(CommandContext<class_2168> commandContext) throws CommandSyntaxException;
	}

	static class class_2296 implements Predicate<class_1799> {
		private final class_3494<class_1792> field_10815;
		@Nullable
		private final class_2487 field_10816;

		public class_2296(class_3494<class_1792> arg, @Nullable class_2487 arg2) {
			this.field_10815 = arg;
			this.field_10816 = arg2;
		}

		public boolean method_9807(class_1799 arg) {
			return this.field_10815.method_15141(arg.method_7909()) && class_2512.method_10687(this.field_10816, arg.method_7969(), true);
		}
	}
}
