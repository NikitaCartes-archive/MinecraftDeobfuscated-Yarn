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

public class class_2214 implements ArgumentType<String> {
	private static final Collection<String> field_9919 = Arrays.asList("foo", "*", "012");
	private static final DynamicCommandExceptionType field_9922 = new DynamicCommandExceptionType(object -> new class_2588("arguments.objective.notFound", object));
	private static final DynamicCommandExceptionType field_9921 = new DynamicCommandExceptionType(object -> new class_2588("arguments.objective.readonly", object));
	public static final DynamicCommandExceptionType field_9920 = new DynamicCommandExceptionType(
		object -> new class_2588("commands.scoreboard.objectives.add.longName", object)
	);

	public static class_2214 method_9391() {
		return new class_2214();
	}

	public static class_266 method_9395(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		String string2 = commandContext.getArgument(string, String.class);
		class_269 lv = commandContext.getSource().method_9211().method_3845();
		class_266 lv2 = lv.method_1170(string2);
		if (lv2 == null) {
			throw field_9922.create(string2);
		} else {
			return lv2;
		}
	}

	public static class_266 method_9393(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		class_266 lv = method_9395(commandContext, string);
		if (lv.method_1116().method_1226()) {
			throw field_9921.create(lv.method_1113());
		} else {
			return lv;
		}
	}

	public String method_9396(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (string.length() > 16) {
			throw field_9920.create(16);
		} else {
			return string;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (commandContext.getSource() instanceof class_2168) {
			return class_2172.method_9265(((class_2168)commandContext.getSource()).method_9211().method_3845().method_1163(), suggestionsBuilder);
		} else if (commandContext.getSource() instanceof class_2172) {
			class_2172 lv = (class_2172)commandContext.getSource();
			return lv.method_9261(commandContext, suggestionsBuilder);
		} else {
			return Suggestions.empty();
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_9919;
	}
}
