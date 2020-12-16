package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;
import net.minecraft.world.timer.Timer;

public class ScheduleCommand {
	private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.schedule.same_tick"));
	private static final DynamicCommandExceptionType CLEARED_FAILURE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.schedule.cleared.failure", object)
	);
	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
			commandContext.getSource().getMinecraftServer().getSaveProperties().getMainWorldProperties().getScheduledEvents().method_22592(), suggestionsBuilder
		);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("schedule")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("function")
						.then(
							CommandManager.argument("function", CommandFunctionArgumentType.commandFunction())
								.suggests(FunctionCommand.SUGGESTION_PROVIDER)
								.then(
									CommandManager.argument("time", TimeArgumentType.time())
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													CommandFunctionArgumentType.getFunctionOrTag(commandContext, "function"),
													IntegerArgumentType.getInteger(commandContext, "time"),
													true
												)
										)
										.then(
											CommandManager.literal("append")
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															CommandFunctionArgumentType.getFunctionOrTag(commandContext, "function"),
															IntegerArgumentType.getInteger(commandContext, "time"),
															false
														)
												)
										)
										.then(
											CommandManager.literal("replace")
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															CommandFunctionArgumentType.getFunctionOrTag(commandContext, "function"),
															IntegerArgumentType.getInteger(commandContext, "time"),
															true
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("clear")
						.then(
							CommandManager.argument("function", StringArgumentType.greedyString())
								.suggests(SUGGESTION_PROVIDER)
								.executes(commandContext -> method_22833(commandContext.getSource(), StringArgumentType.getString(commandContext, "function")))
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> pair, int i, boolean bl) throws CommandSyntaxException {
		if (i == 0) {
			throw SAME_TICK_EXCEPTION.create();
		} else {
			long l = source.getWorld().getTime() + (long)i;
			Identifier identifier = pair.getFirst();
			Timer<MinecraftServer> timer = source.getMinecraftServer().getSaveProperties().getMainWorldProperties().getScheduledEvents();
			pair.getSecond().ifLeft(commandFunction -> {
				String string = identifier.toString();
				if (bl) {
					timer.method_22593(string);
				}

				timer.setEvent(string, l, new FunctionTimerCallback(identifier));
				source.sendFeedback(new TranslatableText("commands.schedule.created.function", identifier, i, l), true);
			}).ifRight(tag -> {
				String string = "#" + identifier;
				if (bl) {
					timer.method_22593(string);
				}

				timer.setEvent(string, l, new FunctionTagTimerCallback(identifier));
				source.sendFeedback(new TranslatableText("commands.schedule.created.tag", identifier, i, l), true);
			});
			return (int)Math.floorMod(l, 2147483647L);
		}
	}

	private static int method_22833(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
		int i = serverCommandSource.getMinecraftServer().getSaveProperties().getMainWorldProperties().getScheduledEvents().method_22593(string);
		if (i == 0) {
			throw CLEARED_FAILURE_EXCEPTION.create(string);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.schedule.cleared.success", i, string), true);
			return i;
		}
	}
}
