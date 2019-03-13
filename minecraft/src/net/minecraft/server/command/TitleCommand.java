package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.client.network.packet.TitleS2CPacket;
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
					ServerCommandManager.argument("targets", EntityArgumentType.multiplePlayer())
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
													ComponentArgumentType.method_9280(commandContext, "title"),
													TitleS2CPacket.Action.field_12630
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
													ComponentArgumentType.method_9280(commandContext, "title"),
													TitleS2CPacket.Action.field_12632
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
													ComponentArgumentType.method_9280(commandContext, "title"),
													TitleS2CPacket.Action.field_12627
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
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.HIDE, null);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.field_13987.sendPacket(titleS2CPacket);
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.title.cleared.single", ((ServerPlayerEntity)collection.iterator().next()).method_5476()), true
			);
		} else {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.title.cleared.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13799(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.RESET, null);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.field_13987.sendPacket(titleS2CPacket);
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.title.reset.single", ((ServerPlayerEntity)collection.iterator().next()).method_5476()), true
			);
		} else {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.title.reset.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_13802(
		ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, TextComponent textComponent, TitleS2CPacket.Action action
	) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.field_13987.sendPacket(new TitleS2CPacket(action, TextFormatter.method_10881(serverCommandSource, textComponent, serverPlayerEntity)));
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent(
					"commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".single", ((ServerPlayerEntity)collection.iterator().next()).method_5476()
				),
				true
			);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".multiple", collection.size()), true
			);
		}

		return collection.size();
	}

	private static int method_13806(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, int i, int j, int k) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(i, j, k);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.field_13987.sendPacket(titleS2CPacket);
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.title.times.single", ((ServerPlayerEntity)collection.iterator().next()).method_5476()), true
			);
		} else {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.title.times.multiple", collection.size()), true);
		}

		return collection.size();
	}
}
