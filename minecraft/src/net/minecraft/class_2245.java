package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class class_2245 implements ArgumentType<Integer> {
	private static final Collection<String> field_9969 = Arrays.asList("0d", "0s", "0t", "0");
	private static final SimpleCommandExceptionType field_9970 = new SimpleCommandExceptionType(new class_2588("argument.time.invalid_unit"));
	private static final DynamicCommandExceptionType field_9971 = new DynamicCommandExceptionType(
		object -> new class_2588("argument.time.invalid_tick_count", object)
	);
	private static final Object2IntMap<String> field_9972 = new Object2IntOpenHashMap<>();

	public static class_2245 method_9489() {
		return new class_2245();
	}

	public Integer method_9490(StringReader stringReader) throws CommandSyntaxException {
		float f = stringReader.readFloat();
		String string = stringReader.readUnquotedString();
		int i = field_9972.getOrDefault(string, 0);
		if (i == 0) {
			throw field_9970.create();
		} else {
			int j = Math.round(f * (float)i);
			if (j < 0) {
				throw field_9971.create(j);
			} else {
				return j;
			}
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getRemaining());

		try {
			stringReader.readFloat();
		} catch (CommandSyntaxException var5) {
			return suggestionsBuilder.buildFuture();
		}

		return class_2172.method_9265(field_9972.keySet(), suggestionsBuilder.createOffset(suggestionsBuilder.getStart() + stringReader.getCursor()));
	}

	@Override
	public Collection<String> getExamples() {
		return field_9969;
	}

	static {
		field_9972.put("d", 24000);
		field_9972.put("s", 20);
		field_9972.put("t", 1);
		field_9972.put("", 1);
	}
}
