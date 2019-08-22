package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class BossBarCommand {
	private static final DynamicCommandExceptionType CREATE_FAILED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.bossbar.create.failed", object)
	);
	private static final DynamicCommandExceptionType UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.bossbar.unknown", object)
	);
	private static final SimpleCommandExceptionType SET_PLAYERS_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.players.unchanged")
	);
	private static final SimpleCommandExceptionType SET_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.name.unchanged")
	);
	private static final SimpleCommandExceptionType SET_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.color.unchanged")
	);
	private static final SimpleCommandExceptionType SET_STYLE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.style.unchanged")
	);
	private static final SimpleCommandExceptionType SET_VALUE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.value.unchanged")
	);
	private static final SimpleCommandExceptionType SETMAX_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.max.unchanged")
	);
	private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.visibility.unchanged.hidden")
	);
	private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.visibility.unchanged.visible")
	);
	public static final SuggestionProvider<ServerCommandSource> suggestionProvider = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(
			commandContext.getSource().getMinecraftServer().getBossBarManager().getIds(), suggestionsBuilder
		);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("bossbar")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("add")
						.then(
							CommandManager.argument("id", IdentifierArgumentType.identifier())
								.then(
									CommandManager.argument("name", TextArgumentType.text())
										.executes(
											commandContext -> addBossBar(
													commandContext.getSource(), IdentifierArgumentType.getIdentifier(commandContext, "id"), TextArgumentType.getTextArgument(commandContext, "name")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("remove")
						.then(
							CommandManager.argument("id", IdentifierArgumentType.identifier())
								.suggests(suggestionProvider)
								.executes(commandContext -> removeBossBar(commandContext.getSource(), createBossBar(commandContext)))
						)
				)
				.then(CommandManager.literal("list").executes(commandContext -> listBossBars(commandContext.getSource())))
				.then(
					CommandManager.literal("set")
						.then(
							CommandManager.argument("id", IdentifierArgumentType.identifier())
								.suggests(suggestionProvider)
								.then(
									CommandManager.literal("name")
										.then(
											CommandManager.argument("name", TextArgumentType.text())
												.executes(
													commandContext -> setName(commandContext.getSource(), createBossBar(commandContext), TextArgumentType.getTextArgument(commandContext, "name"))
												)
										)
								)
								.then(
									CommandManager.literal("color")
										.then(
											CommandManager.literal("pink").executes(commandContext -> setColor(commandContext.getSource(), createBossBar(commandContext), BossBar.Color.PINK))
										)
										.then(
											CommandManager.literal("blue").executes(commandContext -> setColor(commandContext.getSource(), createBossBar(commandContext), BossBar.Color.BLUE))
										)
										.then(
											CommandManager.literal("red").executes(commandContext -> setColor(commandContext.getSource(), createBossBar(commandContext), BossBar.Color.RED))
										)
										.then(
											CommandManager.literal("green").executes(commandContext -> setColor(commandContext.getSource(), createBossBar(commandContext), BossBar.Color.GREEN))
										)
										.then(
											CommandManager.literal("yellow")
												.executes(commandContext -> setColor(commandContext.getSource(), createBossBar(commandContext), BossBar.Color.YELLOW))
										)
										.then(
											CommandManager.literal("purple")
												.executes(commandContext -> setColor(commandContext.getSource(), createBossBar(commandContext), BossBar.Color.PURPLE))
										)
										.then(
											CommandManager.literal("white").executes(commandContext -> setColor(commandContext.getSource(), createBossBar(commandContext), BossBar.Color.WHITE))
										)
								)
								.then(
									CommandManager.literal("style")
										.then(
											CommandManager.literal("progress")
												.executes(commandContext -> setStyle(commandContext.getSource(), createBossBar(commandContext), BossBar.Style.PROGRESS))
										)
										.then(
											CommandManager.literal("notched_6")
												.executes(commandContext -> setStyle(commandContext.getSource(), createBossBar(commandContext), BossBar.Style.NOTCHED_6))
										)
										.then(
											CommandManager.literal("notched_10")
												.executes(commandContext -> setStyle(commandContext.getSource(), createBossBar(commandContext), BossBar.Style.NOTCHED_10))
										)
										.then(
											CommandManager.literal("notched_12")
												.executes(commandContext -> setStyle(commandContext.getSource(), createBossBar(commandContext), BossBar.Style.NOTCHED_12))
										)
										.then(
											CommandManager.literal("notched_20")
												.executes(commandContext -> setStyle(commandContext.getSource(), createBossBar(commandContext), BossBar.Style.NOTCHED_20))
										)
								)
								.then(
									CommandManager.literal("value")
										.then(
											CommandManager.argument("value", IntegerArgumentType.integer(0))
												.executes(
													commandContext -> setValue(commandContext.getSource(), createBossBar(commandContext), IntegerArgumentType.getInteger(commandContext, "value"))
												)
										)
								)
								.then(
									CommandManager.literal("max")
										.then(
											CommandManager.argument("max", IntegerArgumentType.integer(1))
												.executes(
													commandContext -> setMaxValue(commandContext.getSource(), createBossBar(commandContext), IntegerArgumentType.getInteger(commandContext, "max"))
												)
										)
								)
								.then(
									CommandManager.literal("visible")
										.then(
											CommandManager.argument("visible", BoolArgumentType.bool())
												.executes(
													commandContext -> setVisible(commandContext.getSource(), createBossBar(commandContext), BoolArgumentType.getBool(commandContext, "visible"))
												)
										)
								)
								.then(
									CommandManager.literal("players")
										.executes(commandContext -> setPlayers(commandContext.getSource(), createBossBar(commandContext), Collections.emptyList()))
										.then(
											CommandManager.argument("targets", EntityArgumentType.players())
												.executes(
													commandContext -> setPlayers(
															commandContext.getSource(), createBossBar(commandContext), EntityArgumentType.getOptionalPlayers(commandContext, "targets")
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("get")
						.then(
							CommandManager.argument("id", IdentifierArgumentType.identifier())
								.suggests(suggestionProvider)
								.then(CommandManager.literal("value").executes(commandContext -> getValue(commandContext.getSource(), createBossBar(commandContext))))
								.then(CommandManager.literal("max").executes(commandContext -> getMaxValue(commandContext.getSource(), createBossBar(commandContext))))
								.then(CommandManager.literal("visible").executes(commandContext -> isVisible(commandContext.getSource(), createBossBar(commandContext))))
								.then(CommandManager.literal("players").executes(commandContext -> getPlayers(commandContext.getSource(), createBossBar(commandContext))))
						)
				)
		);
	}

	private static int getValue(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
		serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.get.value", commandBossBar.toHoverableText(), commandBossBar.getValue()), true);
		return commandBossBar.getValue();
	}

	private static int getMaxValue(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
		serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.get.max", commandBossBar.toHoverableText(), commandBossBar.getMaxValue()), true);
		return commandBossBar.getMaxValue();
	}

	private static int isVisible(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
		if (commandBossBar.isVisible()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.get.visible.visible", commandBossBar.toHoverableText()), true);
			return 1;
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.get.visible.hidden", commandBossBar.toHoverableText()), true);
			return 0;
		}
	}

	private static int getPlayers(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
		if (commandBossBar.getPlayers().isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.get.players.none", commandBossBar.toHoverableText()), true);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText(
					"commands.bossbar.get.players.some",
					commandBossBar.toHoverableText(),
					commandBossBar.getPlayers().size(),
					Texts.join(commandBossBar.getPlayers(), PlayerEntity::getDisplayName)
				),
				true
			);
		}

		return commandBossBar.getPlayers().size();
	}

	private static int setVisible(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, boolean bl) throws CommandSyntaxException {
		if (commandBossBar.isVisible() == bl) {
			if (bl) {
				throw SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION.create();
			} else {
				throw SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION.create();
			}
		} else {
			commandBossBar.setVisible(bl);
			if (bl) {
				serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.set.visible.success.visible", commandBossBar.toHoverableText()), true);
			} else {
				serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.set.visible.success.hidden", commandBossBar.toHoverableText()), true);
			}

			return 0;
		}
	}

	private static int setValue(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, int i) throws CommandSyntaxException {
		if (commandBossBar.getValue() == i) {
			throw SET_VALUE_UNCHANGED_EXCEPTION.create();
		} else {
			commandBossBar.setValue(i);
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.set.value.success", commandBossBar.toHoverableText(), i), true);
			return i;
		}
	}

	private static int setMaxValue(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, int i) throws CommandSyntaxException {
		if (commandBossBar.getMaxValue() == i) {
			throw SETMAX_UNCHANGED_EXCEPTION.create();
		} else {
			commandBossBar.setMaxValue(i);
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.set.max.success", commandBossBar.toHoverableText(), i), true);
			return i;
		}
	}

	private static int setColor(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, BossBar.Color color) throws CommandSyntaxException {
		if (commandBossBar.getColor().equals(color)) {
			throw SET_COLOR_UNCHANGED_EXCEPTION.create();
		} else {
			commandBossBar.setColor(color);
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.set.color.success", commandBossBar.toHoverableText()), true);
			return 0;
		}
	}

	private static int setStyle(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, BossBar.Style style) throws CommandSyntaxException {
		if (commandBossBar.getOverlay().equals(style)) {
			throw SET_STYLE_UNCHANGED_EXCEPTION.create();
		} else {
			commandBossBar.setOverlay(style);
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.set.style.success", commandBossBar.toHoverableText()), true);
			return 0;
		}
	}

	private static int setName(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, Text text) throws CommandSyntaxException {
		Text text2 = Texts.parse(serverCommandSource, text, null, 0);
		if (commandBossBar.getName().equals(text2)) {
			throw SET_NAME_UNCHANGED_EXCEPTION.create();
		} else {
			commandBossBar.setName(text2);
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.set.name.success", commandBossBar.toHoverableText()), true);
			return 0;
		}
	}

	private static int setPlayers(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, Collection<ServerPlayerEntity> collection) throws CommandSyntaxException {
		boolean bl = commandBossBar.addPlayers(collection);
		if (!bl) {
			throw SET_PLAYERS_UNCHANGED_EXCEPTION.create();
		} else {
			if (commandBossBar.getPlayers().isEmpty()) {
				serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.set.players.success.none", commandBossBar.toHoverableText()), true);
			} else {
				serverCommandSource.sendFeedback(
					new TranslatableText(
						"commands.bossbar.set.players.success.some", commandBossBar.toHoverableText(), collection.size(), Texts.join(collection, PlayerEntity::getDisplayName)
					),
					true
				);
			}

			return commandBossBar.getPlayers().size();
		}
	}

	private static int listBossBars(ServerCommandSource serverCommandSource) {
		Collection<CommandBossBar> collection = serverCommandSource.getMinecraftServer().getBossBarManager().getAll();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.list.bars.none"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.bossbar.list.bars.some", collection.size(), Texts.join(collection, CommandBossBar::toHoverableText)), false
			);
		}

		return collection.size();
	}

	private static int addBossBar(ServerCommandSource serverCommandSource, Identifier identifier, Text text) throws CommandSyntaxException {
		BossBarManager bossBarManager = serverCommandSource.getMinecraftServer().getBossBarManager();
		if (bossBarManager.get(identifier) != null) {
			throw CREATE_FAILED_EXCEPTION.create(identifier.toString());
		} else {
			CommandBossBar commandBossBar = bossBarManager.add(identifier, Texts.parse(serverCommandSource, text, null, 0));
			serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.create.success", commandBossBar.toHoverableText()), true);
			return bossBarManager.getAll().size();
		}
	}

	private static int removeBossBar(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar) {
		BossBarManager bossBarManager = serverCommandSource.getMinecraftServer().getBossBarManager();
		commandBossBar.clearPlayers();
		bossBarManager.remove(commandBossBar);
		serverCommandSource.sendFeedback(new TranslatableText("commands.bossbar.remove.success", commandBossBar.toHoverableText()), true);
		return bossBarManager.getAll().size();
	}

	public static CommandBossBar createBossBar(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
		Identifier identifier = IdentifierArgumentType.getIdentifier(commandContext, "id");
		CommandBossBar commandBossBar = commandContext.getSource().getMinecraftServer().getBossBarManager().get(identifier);
		if (commandBossBar == null) {
			throw UNKNOWN_EXCEPTION.create(identifier.toString());
		} else {
			return commandBossBar;
		}
	}
}
