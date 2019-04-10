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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;

public class ScheduleCommand {
	private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.schedule.same_tick")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("schedule")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("function")
						.then(
							CommandManager.argument("function", FunctionArgumentType.create())
								.suggests(FunctionCommand.SUGGESTION_PROVIDER)
								.then(
									CommandManager.argument("time", TimeArgumentType.create())
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

	private static int execute(ServerCommandSource serverCommandSource, Either<CommandFunction, Tag<CommandFunction>> either, int i) throws CommandSyntaxException {
		if (i == 0) {
			throw SAME_TICK_EXCEPTION.create();
		} else {
			long l = serverCommandSource.getWorld().getTime() + (long)i;
			either.ifLeft(commandFunction -> {
					Identifier identifier = commandFunction.getId();
					serverCommandSource.getWorld().getLevelProperties().getScheduledEvents().replaceEvent(identifier.toString(), l, new FunctionTimerCallback(identifier));
					serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.schedule.created.function", identifier, i, l), true);
				})
				.ifRight(
					tag -> {
						Identifier identifier = tag.getId();
						serverCommandSource.getWorld()
							.getLevelProperties()
							.getScheduledEvents()
							.replaceEvent("#" + identifier.toString(), l, new FunctionTagTimerCallback(identifier));
						serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.schedule.created.tag", identifier, i, l), true);
					}
				);
			return (int)Math.floorMod(l, 2147483647L);
		}
	}
}
