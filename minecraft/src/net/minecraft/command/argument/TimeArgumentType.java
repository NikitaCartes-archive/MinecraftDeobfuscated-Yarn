package net.minecraft.command.argument;

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
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;

public class TimeArgumentType implements ArgumentType<Integer> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0d", "0s", "0t", "0");
	private static final SimpleCommandExceptionType INVALID_UNIT_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.time.invalid_unit"));
	private static final DynamicCommandExceptionType INVALID_COUNT_EXCEPTION = new DynamicCommandExceptionType(
		time -> new TranslatableText("argument.time.invalid_tick_count", time)
	);
	private static final Object2IntMap<String> UNITS = new Object2IntOpenHashMap<>();

	public static TimeArgumentType time() {
		return new TimeArgumentType();
	}

	public Integer parse(StringReader stringReader) throws CommandSyntaxException {
		float f = stringReader.readFloat();
		String string = stringReader.readUnquotedString();
		int i = UNITS.getOrDefault(string, 0);
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
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getRemaining());

		try {
			stringReader.readFloat();
		} catch (CommandSyntaxException var5) {
			return builder.buildFuture();
		}

		return CommandSource.suggestMatching(UNITS.keySet(), builder.createOffset(builder.getStart() + stringReader.getCursor()));
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	static {
		UNITS.put("d", 24000);
		UNITS.put("s", 20);
		UNITS.put("t", 1);
		UNITS.put("", 1);
	}
}
