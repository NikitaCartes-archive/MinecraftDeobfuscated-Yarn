package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class TitleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("title")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.literal("clear")
								.executes(commandContext -> executeClear(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets")))
						)
						.then(
							CommandManager.literal("reset")
								.executes(commandContext -> executeReset(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets")))
						)
						.then(
							CommandManager.literal("title")
								.then(
									CommandManager.argument("title", TextArgumentType.text())
										.executes(
											commandContext -> executeTitle(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													TextArgumentType.getTextArgument(commandContext, "title"),
													TitleS2CPacket.Action.TITLE
												)
										)
								)
						)
						.then(
							CommandManager.literal("subtitle")
								.then(
									CommandManager.argument("title", TextArgumentType.text())
										.executes(
											commandContext -> executeTitle(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													TextArgumentType.getTextArgument(commandContext, "title"),
													TitleS2CPacket.Action.SUBTITLE
												)
										)
								)
						)
						.then(
							CommandManager.literal("actionbar")
								.then(
									CommandManager.argument("title", TextArgumentType.text())
										.executes(
											commandContext -> executeTitle(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													TextArgumentType.getTextArgument(commandContext, "title"),
													TitleS2CPacket.Action.ACTIONBAR
												)
										)
								)
						)
						.then(
							CommandManager.literal("times")
								.then(
									CommandManager.argument("fadeIn", IntegerArgumentType.integer(0))
										.then(
											CommandManager.argument("stay", IntegerArgumentType.integer(0))
												.then(
													CommandManager.argument("fadeOut", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> executeTimes(
																	commandContext.getSource(),
																	EntityArgumentType.getPlayers(commandContext, "targets"),
																	IntegerArgumentType.getInteger(commandContext, "fadeIn"),
																	IntegerArgumentType.getInteger(commandContext, "stay"),
																	IntegerArgumentType.getInteger(commandContext, "fadeOut")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int executeClear(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.CLEAR, null);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
		}

		if (targets.size() == 1) {
			source.sendFeedback(new TranslatableText("commands.title.cleared.single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.title.cleared.multiple", targets.size()), true);
		}

		return targets.size();
	}

	private static int executeReset(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.RESET, null);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
		}

		if (targets.size() == 1) {
			source.sendFeedback(new TranslatableText("commands.title.reset.single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.title.reset.multiple", targets.size()), true);
		}

		return targets.size();
	}

	private static int executeTitle(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text title, TitleS2CPacket.Action type) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(type, Texts.parse(source, title, serverPlayerEntity, 0)));
		}

		if (targets.size() == 1) {
			source.sendFeedback(
				new TranslatableText(
					"commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
				),
				true
			);
		} else {
			source.sendFeedback(new TranslatableText("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".multiple", targets.size()), true);
		}

		return targets.size();
	}

	private static int executeTimes(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int fadeIn, int stay, int fadeOut) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(fadeIn, stay, fadeOut);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
		}

		if (targets.size() == 1) {
			source.sendFeedback(new TranslatableText("commands.title.times.single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.title.times.multiple", targets.size()), true);
		}

		return targets.size();
	}
}
