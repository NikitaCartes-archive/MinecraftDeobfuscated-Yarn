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

public class class_2243 implements ArgumentType<String> {
	private static final Collection<String> field_9964 = Arrays.asList("foo", "123");
	private static final DynamicCommandExceptionType field_9963 = new DynamicCommandExceptionType(object -> new class_2588("team.notFound", object));

	public static class_2243 method_9482() {
		return new class_2243();
	}

	public static class_268 method_9480(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		String string2 = commandContext.getArgument(string, String.class);
		class_269 lv = commandContext.getSource().method_9211().method_3845();
		class_268 lv2 = lv.method_1153(string2);
		if (lv2 == null) {
			throw field_9963.create(string2);
		} else {
			return lv2;
		}
	}

	public String method_9483(StringReader stringReader) throws CommandSyntaxException {
		return stringReader.readUnquotedString();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return commandContext.getSource() instanceof class_2172
			? class_2172.method_9265(((class_2172)commandContext.getSource()).method_9267(), suggestionsBuilder)
			: Suggestions.empty();
	}

	@Override
	public Collection<String> getExamples() {
		return field_9964;
	}
}
