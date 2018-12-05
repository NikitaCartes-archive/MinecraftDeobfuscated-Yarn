package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.client.network.packet.TitleClientPacket;
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;

public class TitleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("title")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
						.then(
							ServerCommandManager.literal("clear")
								.executes(commandContext -> method_13805(commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets")))
						)
						.then(
							ServerCommandManager.literal("reset")
								.executes(commandContext -> method_13799(commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets")))
						)
						.then(
							ServerCommandManager.literal("title")
								.then(
									ServerCommandManager.argument("title", ComponentArgumentType.create())
										.executes(
											commandContext -> method_13802(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													ComponentArgumentType.getComponentArgument(commandContext, "title"),
													TitleClientPacket.Action.field_12630
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("subtitle")
								.then(
									ServerCommandManager.argument("title", ComponentArgumentType.create())
										.executes(
											commandContext -> method_13802(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													ComponentArgumentType.getComponentArgument(commandContext, "title"),
													TitleClientPacket.Action.field_12632
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("actionbar")
								.then(
									ServerCommandManager.argument("title", ComponentArgumentType.create())
										.executes(
											commandContext -> method_13802(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													ComponentArgumentType.getComponentArgument(commandContext, "title"),
													TitleClientPacket.Action.field_12627
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("times")
								.then(
									ServerCommandManager.argument("fadeIn", IntegerArgumentType.integer(0))
										.then(
											ServerCommandManager.argument("stay", IntegerArgumentType.integer(0))
												.then(
													ServerCommandManager.argument("fadeOut", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_13806(
																	commandContext.getSource(),
																	EntityArgumentType.method_9312(commandContext, "targets"),
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

	private static int method_13805(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection) {
		TitleClientPacket titleClientPacket = new TitleClientPacket(TitleClientPacket.Action.HIDE, null);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(titleClientPacket);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.title.cleared.single", ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.title.cleared.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13799(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection) {
		TitleClientPacket titleClientPacket = new TitleClientPacket(TitleClientPacket.Action.RESET, null);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(titleClientPacket);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.title.reset.single", ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.title.reset.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13802(
		ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, TextComponent textComponent, TitleClientPacket.Action action
	) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler
				.sendPacket(new TitleClientPacket(action, TextFormatter.method_10881(serverCommandSource, textComponent, serverPlayerEntity)));
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent(
					"commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".single", ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
				),
				true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".multiple", collection.size()), true
			);
		}

		return collection.size();
	}

	private static int method_13806(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, int i, int j, int k) {
		TitleClientPacket titleClientPacket = new TitleClientPacket(i, j, k);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(titleClientPacket);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.title.times.single", ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.title.times.multiple", collection.size()), true);
		}

		return collection.size();
	}
}
