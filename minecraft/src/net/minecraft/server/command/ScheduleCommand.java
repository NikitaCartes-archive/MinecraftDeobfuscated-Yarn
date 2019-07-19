package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;

public class ScheduleCommand {
	private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.schedule.same_tick"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("schedule")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("function")
						.then(
							CommandManager.argument("function", FunctionArgumentType.function())
								.suggests(FunctionCommand.SUGGESTION_PROVIDER)
								.then(
									CommandManager.argument("time", TimeArgumentType.time())
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													FunctionArgumentType.getFunctionOrTag(commandContext, "function"),
													IntegerArgumentType.getInteger(commandContext, "time")
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, Either<CommandFunction, Tag<CommandFunction>> function, int time) throws CommandSyntaxException {
		if (time == 0) {
			throw SAME_TICK_EXCEPTION.create();
		} else {
			long l = source.getWorld().getTime() + (long)time;
			function.ifLeft(commandFunction -> {
				Identifier identifier = commandFunction.getId();
				source.getWorld().getLevelProperties().getScheduledEvents().replaceEvent(identifier.toString(), l, new FunctionTimerCallback(identifier));
				source.sendFeedback(new TranslatableText("commands.schedule.created.function", identifier, time, l), true);
			}).ifRight(tag -> {
				Identifier identifier = tag.getId();
				source.getWorld().getLevelProperties().getScheduledEvents().replaceEvent("#" + identifier.toString(), l, new FunctionTagTimerCallback(identifier));
				source.sendFeedback(new TranslatableText("commands.schedule.created.tag", identifier, time, l), true);
			});
			return (int)Math.floorMod(l, 2147483647L);
		}
	}
}
