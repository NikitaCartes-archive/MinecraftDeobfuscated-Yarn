package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class ObjectiveCriteriaArgumentType implements ArgumentType<ScoreboardCriterion> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
	public static final DynamicCommandExceptionType INVALID_CRITERIA_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.criteria.invalid", object)
	);

	private ObjectiveCriteriaArgumentType() {
	}

	public static ObjectiveCriteriaArgumentType objectiveCriteria() {
		return new ObjectiveCriteriaArgumentType();
	}

	public static ScoreboardCriterion getCriteria(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, ScoreboardCriterion.class);
	}

	public ScoreboardCriterion parse(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());
		return (ScoreboardCriterion)ScoreboardCriterion.getOrCreateStatCriterion(string).orElseThrow(() -> {
			stringReader.setCursor(i);
			return INVALID_CRITERIA_EXCEPTION.create(string);
		});
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		List<String> list = Lists.<String>newArrayList(ScoreboardCriterion.CRITERIA.keySet());

		for (StatType<?> statType : Registry.STAT_TYPE) {
			for (Object object : statType.getRegistry()) {
				String string = this.getStatName(statType, object);
				list.add(string);
			}
		}

		return CommandSource.suggestMatching(list, builder);
	}

	public <T> String getStatName(StatType<T> stat, Object value) {
		return Stat.getName(stat, (T)value);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
