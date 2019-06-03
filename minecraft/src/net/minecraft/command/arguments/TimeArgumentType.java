package net.minecraft.command.arguments;

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
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.TranslatableText;

public class TimeArgumentType implements ArgumentType<Integer> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0d", "0s", "0t", "0");
	private static final SimpleCommandExceptionType INVALID_UNIT_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.time.invalid_unit"));
	private static final DynamicCommandExceptionType INVALID_COUNT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.time.invalid_tick_count", object)
	);
	private static final Object2IntMap<String> units = new Object2IntOpenHashMap<>();

	public static TimeArgumentType create() {
		return new TimeArgumentType();
	}

	public Integer method_9490(StringReader stringReader) throws CommandSyntaxException {
		float f = stringReader.readFloat();
		String string = stringReader.readUnquotedString();
		int i = units.getOrDefault(string, 0);
		if (i == 0) {
			throw INVALID_UNIT_EXCEPTION.create();
		} else {
			int j = Math.round(f * (float)i);
			if (j < 0) {
				throw INVALID_COUNT_EXCEPTION.create(j);
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

		return CommandSource.suggestMatching(units.keySet(), suggestionsBuilder.createOffset(suggestionsBuilder.getStart() + stringReader.getCursor()));
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	static {
		units.put("d", 24000);
		units.put("s", 20);
		units.put("t", 1);
		units.put("", 1);
	}
}
