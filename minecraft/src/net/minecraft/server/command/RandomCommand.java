package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NumberRangeArgumentType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSequencesState;

public class RandomCommand {
	private static final SimpleCommandExceptionType RANGE_TOO_LARGE_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.random.error.range_too_large")
	);
	private static final SimpleCommandExceptionType RANGE_TOO_SMALL_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.random.error.range_too_small")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("random")
				.then(random("value", false))
				.then(random("roll", true))
				.then(
					CommandManager.literal("reset")
						.requires(source -> source.hasPermissionLevel(2))
						.then(
							CommandManager.literal("*")
								.executes(context -> executeReset(context.getSource()))
								.then(
									CommandManager.argument("seed", IntegerArgumentType.integer())
										.executes(context -> executeReset(context.getSource(), IntegerArgumentType.getInteger(context, "seed"), true, true))
										.then(
											CommandManager.argument("includeWorldSeed", BoolArgumentType.bool())
												.executes(
													context -> executeReset(
															context.getSource(), IntegerArgumentType.getInteger(context, "seed"), BoolArgumentType.getBool(context, "includeWorldSeed"), true
														)
												)
												.then(
													CommandManager.argument("includeSequenceId", BoolArgumentType.bool())
														.executes(
															context -> executeReset(
																	context.getSource(),
																	IntegerArgumentType.getInteger(context, "seed"),
																	BoolArgumentType.getBool(context, "includeWorldSeed"),
																	BoolArgumentType.getBool(context, "includeSequenceId")
																)
														)
												)
										)
								)
						)
						.then(
							CommandManager.argument("sequence", IdentifierArgumentType.identifier())
								.suggests(RandomCommand::suggestSequences)
								.executes(context -> executeReset(context.getSource(), IdentifierArgumentType.getIdentifier(context, "sequence")))
								.then(
									CommandManager.argument("seed", IntegerArgumentType.integer())
										.executes(
											context -> executeReset(
													context.getSource(), IdentifierArgumentType.getIdentifier(context, "sequence"), IntegerArgumentType.getInteger(context, "seed"), true, true
												)
										)
										.then(
											CommandManager.argument("includeWorldSeed", BoolArgumentType.bool())
												.executes(
													context -> executeReset(
															context.getSource(),
															IdentifierArgumentType.getIdentifier(context, "sequence"),
															IntegerArgumentType.getInteger(context, "seed"),
															BoolArgumentType.getBool(context, "includeWorldSeed"),
															true
														)
												)
												.then(
													CommandManager.argument("includeSequenceId", BoolArgumentType.bool())
														.executes(
															context -> executeReset(
																	context.getSource(),
																	IdentifierArgumentType.getIdentifier(context, "sequence"),
																	IntegerArgumentType.getInteger(context, "seed"),
																	BoolArgumentType.getBool(context, "includeWorldSeed"),
																	BoolArgumentType.getBool(context, "includeSequenceId")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static LiteralArgumentBuilder<ServerCommandSource> random(String argumentName, boolean roll) {
		return CommandManager.literal(argumentName)
			.then(
				CommandManager.argument("range", NumberRangeArgumentType.intRange())
					.executes(context -> execute(context.getSource(), NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument(context, "range"), null, roll))
					.then(
						CommandManager.argument("sequence", IdentifierArgumentType.identifier())
							.suggests(RandomCommand::suggestSequences)
							.requires(source -> source.hasPermissionLevel(2))
							.executes(
								context -> execute(
										context.getSource(),
										NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument(context, "range"),
										IdentifierArgumentType.getIdentifier(context, "sequence"),
										roll
									)
							)
					)
			);
	}

	private static CompletableFuture<Suggestions> suggestSequences(CommandContext<ServerCommandSource> context, SuggestionsBuilder suggestionsBuilder) {
		List<String> list = Lists.<String>newArrayList();
		context.getSource().getWorld().getRandomSequences().forEachSequence((id, sequence) -> list.add(id.toString()));
		return CommandSource.suggestMatching(list, suggestionsBuilder);
	}

	private static int execute(ServerCommandSource source, NumberRange.IntRange range, @Nullable Identifier sequenceId, boolean roll) throws CommandSyntaxException {
		Random random;
		if (sequenceId != null) {
			random = source.getWorld().getOrCreateRandom(sequenceId);
		} else {
			random = source.getWorld().getRandom();
		}

		int i = range.getMin() == null ? Integer.MIN_VALUE : (Integer)range.getMin();
		int j = range.getMax() == null ? Integer.MAX_VALUE : (Integer)range.getMax();
		long l = (long)j - (long)i;
		if (l == 0L) {
			throw RANGE_TOO_SMALL_EXCEPTION.create();
		} else if (l >= 2147483647L) {
			throw RANGE_TOO_LARGE_EXCEPTION.create();
		} else {
			int k = MathHelper.nextBetween(random, i, j);
			if (roll) {
				source.getServer().getPlayerManager().broadcast(Text.translatable("commands.random.roll", source.getDisplayName(), k, i, j), false);
			} else {
				source.sendFeedback(() -> Text.translatable("commands.random.sample.success", k), false);
			}

			return k;
		}
	}

	private static int executeReset(ServerCommandSource source, Identifier sequenceId) throws CommandSyntaxException {
		source.getWorld().getRandomSequences().reset(sequenceId);
		source.sendFeedback(() -> Text.translatable("commands.random.reset.success", sequenceId), false);
		return 1;
	}

	private static int executeReset(ServerCommandSource source, Identifier sequenceId, int salt, boolean includeWorldSeed, boolean includeSequenceId) throws CommandSyntaxException {
		source.getWorld().getRandomSequences().reset(sequenceId, salt, includeWorldSeed, includeSequenceId);
		source.sendFeedback(() -> Text.translatable("commands.random.reset.success", sequenceId), false);
		return 1;
	}

	private static int executeReset(ServerCommandSource source) {
		int i = source.getWorld().getRandomSequences().resetAll();
		source.sendFeedback(() -> Text.translatable("commands.random.reset.all.success", i), false);
		return i;
	}

	private static int executeReset(ServerCommandSource source, int salt, boolean includeWorldSeed, boolean includeSequenceId) {
		RandomSequencesState randomSequencesState = source.getWorld().getRandomSequences();
		randomSequencesState.setDefaultParameters(salt, includeWorldSeed, includeSequenceId);
		int i = randomSequencesState.resetAll();
		source.sendFeedback(() -> Text.translatable("commands.random.reset.all.success", i), false);
		return i;
	}
}
