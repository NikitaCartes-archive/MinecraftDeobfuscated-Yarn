package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.client.network.packet.TitleS2CPacket;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class TitleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
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
									CommandManager.argument("title", TextArgumentType.create())
										.executes(
											commandContext -> executeTitle(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													TextArgumentType.getTextArgument(commandContext, "title"),
													TitleS2CPacket.Action.field_12630
												)
										)
								)
						)
						.then(
							CommandManager.literal("subtitle")
								.then(
									CommandManager.argument("title", TextArgumentType.create())
										.executes(
											commandContext -> executeTitle(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													TextArgumentType.getTextArgument(commandContext, "title"),
													TitleS2CPacket.Action.field_12632
												)
										)
								)
						)
						.then(
							CommandManager.literal("actionbar")
								.then(
									CommandManager.argument("title", TextArgumentType.create())
										.executes(
											commandContext -> executeTitle(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													TextArgumentType.getTextArgument(commandContext, "title"),
													TitleS2CPacket.Action.field_12627
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

	private static int executeClear(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.field_12633, null);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.title.cleared.single", ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.title.cleared.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int executeReset(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.field_12628, null);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.title.reset.single", ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.title.reset.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int executeTitle(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Text text, TitleS2CPacket.Action action) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(action, Texts.parse(serverCommandSource, text, serverPlayerEntity, 0)));
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText(
					"commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".single", ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
				),
				true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".multiple", collection.size()), true
			);
		}

		return collection.size();
	}

	private static int executeTimes(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, int i, int j, int k) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(i, j, k);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.title.times.single", ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.title.times.multiple", collection.size()), true);
		}

		return collection.size();
	}
}
