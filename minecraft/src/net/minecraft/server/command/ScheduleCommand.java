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
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;
import net.minecraft.world.timer.Timer;

public class ScheduleCommand {
	private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.schedule.same_tick"));
	private static final DynamicCommandExceptionType CLEARED_FAILURE_EXCEPTION = new DynamicCommandExceptionType(
		eventName -> Text.translatable("commands.schedule.cleared.failure", eventName)
	);
	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(
			context.getSource().getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents().getEventNames(), builder
		);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("schedule")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.literal("function")
						.then(
							CommandManager.argument("function", CommandFunctionArgumentType.commandFunction())
								.suggests(FunctionCommand.SUGGESTION_PROVIDER)
								.then(
									CommandManager.argument("time", TimeArgumentType.time())
										.executes(
											context -> execute(
													context.getSource(), CommandFunctionArgumentType.getFunctionOrTag(context, "function"), IntegerArgumentType.getInteger(context, "time"), true
												)
										)
										.then(
											CommandManager.literal("append")
												.executes(
													context -> execute(
															context.getSource(), CommandFunctionArgumentType.getFunctionOrTag(context, "function"), IntegerArgumentType.getInteger(context, "time"), false
														)
												)
										)
										.then(
											CommandManager.literal("replace")
												.executes(
													context -> execute(
															context.getSource(), CommandFunctionArgumentType.getFunctionOrTag(context, "function"), IntegerArgumentType.getInteger(context, "time"), true
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
								.executes(context -> clearEvent(context.getSource(), StringArgumentType.getString(context, "function")))
						)
				)
		);
	}

	private static int execute(
		ServerCommandSource source, Pair<Identifier, Either<CommandFunction, Collection<CommandFunction>>> function, int time, boolean replace
	) throws CommandSyntaxException {
		if (time == 0) {
			throw SAME_TICK_EXCEPTION.create();
		} else {
			long l = source.getWorld().getTime() + (long)time;
			Identifier identifier = function.getFirst();
			Timer<MinecraftServer> timer = source.getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents();
			function.getSecond().ifLeft(function2 -> {
				String string = identifier.toString();
				if (replace) {
					timer.remove(string);
				}

				timer.setEvent(string, l, new FunctionTimerCallback(identifier));
				source.sendFeedback(() -> Text.translatable("commands.schedule.created.function", identifier, time, l), true);
			}).ifRight(functions -> {
				String string = "#" + identifier;
				if (replace) {
					timer.remove(string);
				}

				timer.setEvent(string, l, new FunctionTagTimerCallback(identifier));
				source.sendFeedback(() -> Text.translatable("commands.schedule.created.tag", identifier, time, l), true);
			});
			return Math.floorMod(l, Integer.MAX_VALUE);
		}
	}

	private static int clearEvent(ServerCommandSource source, String eventName) throws CommandSyntaxException {
		int i = source.getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents().remove(eventName);
		if (i == 0) {
			throw CLEARED_FAILURE_EXCEPTION.create(eventName);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.schedule.cleared.success", i, eventName), true);
			return i;
		}
	}
}
