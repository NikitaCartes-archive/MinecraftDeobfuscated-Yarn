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
import net.minecraft.class_3002;
import net.minecraft.class_3004;
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ResourceLocationArgumentType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

public class BossBarCommand {
	private static final DynamicCommandExceptionType CREATE_FAILED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.bossbar.create.failed", object)
	);
	private static final DynamicCommandExceptionType UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.bossbar.unknown", object)
	);
	private static final SimpleCommandExceptionType SET_PLAYERS_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.bossbar.set.players.unchanged")
	);
	private static final SimpleCommandExceptionType SET_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.bossbar.set.name.unchanged")
	);
	private static final SimpleCommandExceptionType SET_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.bossbar.set.color.unchanged")
	);
	private static final SimpleCommandExceptionType SET_STYLE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.bossbar.set.style.unchanged")
	);
	private static final SimpleCommandExceptionType SET_VALUE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.bossbar.set.value.unchanged")
	);
	private static final SimpleCommandExceptionType SETMAX_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.bossbar.set.max.unchanged")
	);
	private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.bossbar.set.visibility.unchanged.hidden")
	);
	private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.bossbar.set.visibility.unchanged.visible")
	);
	public static final SuggestionProvider<ServerCommandSource> field_13482 = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(
			commandContext.getSource().getMinecraftServer().method_3837().method_12968(), suggestionsBuilder
		);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("bossbar")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("add")
						.then(
							ServerCommandManager.argument("id", ResourceLocationArgumentType.create())
								.then(
									ServerCommandManager.argument("name", ComponentArgumentType.create())
										.executes(
											commandContext -> method_13049(
													commandContext.getSource(),
													ResourceLocationArgumentType.getIdentifierArgument(commandContext, "id"),
													ComponentArgumentType.getComponentArgument(commandContext, "name")
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("remove")
						.then(
							ServerCommandManager.argument("id", ResourceLocationArgumentType.create())
								.suggests(field_13482)
								.executes(commandContext -> method_13069(commandContext.getSource(), method_13054(commandContext)))
						)
				)
				.then(ServerCommandManager.literal("list").executes(commandContext -> method_13045(commandContext.getSource())))
				.then(
					ServerCommandManager.literal("set")
						.then(
							ServerCommandManager.argument("id", ResourceLocationArgumentType.create())
								.suggests(field_13482)
								.then(
									ServerCommandManager.literal("name")
										.then(
											ServerCommandManager.argument("name", ComponentArgumentType.create())
												.executes(
													commandContext -> method_13071(
															commandContext.getSource(), method_13054(commandContext), ComponentArgumentType.getComponentArgument(commandContext, "name")
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("color")
										.then(
											ServerCommandManager.literal("pink")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), BossBar.Color.field_5788))
										)
										.then(
											ServerCommandManager.literal("blue")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), BossBar.Color.field_5780))
										)
										.then(
											ServerCommandManager.literal("red")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), BossBar.Color.field_5784))
										)
										.then(
											ServerCommandManager.literal("green")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), BossBar.Color.field_5785))
										)
										.then(
											ServerCommandManager.literal("yellow")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), BossBar.Color.field_5782))
										)
										.then(
											ServerCommandManager.literal("purple")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), BossBar.Color.field_5783))
										)
										.then(
											ServerCommandManager.literal("white")
												.executes(commandContext -> method_13028(commandContext.getSource(), method_13054(commandContext), BossBar.Color.field_5786))
										)
								)
								.then(
									ServerCommandManager.literal("style")
										.then(
											ServerCommandManager.literal("progress")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), BossBar.Overlay.field_5795))
										)
										.then(
											ServerCommandManager.literal("notched_6")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), BossBar.Overlay.field_5796))
										)
										.then(
											ServerCommandManager.literal("notched_10")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), BossBar.Overlay.field_5791))
										)
										.then(
											ServerCommandManager.literal("notched_12")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), BossBar.Overlay.field_5793))
										)
										.then(
											ServerCommandManager.literal("notched_20")
												.executes(commandContext -> method_13050(commandContext.getSource(), method_13054(commandContext), BossBar.Overlay.field_5790))
										)
								)
								.then(
									ServerCommandManager.literal("value")
										.then(
											ServerCommandManager.argument("value", IntegerArgumentType.integer(0))
												.executes(
													commandContext -> method_13036(commandContext.getSource(), method_13054(commandContext), IntegerArgumentType.getInteger(commandContext, "value"))
												)
										)
								)
								.then(
									ServerCommandManager.literal("max")
										.then(
											ServerCommandManager.argument("max", IntegerArgumentType.integer(1))
												.executes(
													commandContext -> method_13066(commandContext.getSource(), method_13054(commandContext), IntegerArgumentType.getInteger(commandContext, "max"))
												)
										)
								)
								.then(
									ServerCommandManager.literal("visible")
										.then(
											ServerCommandManager.argument("visible", BoolArgumentType.bool())
												.executes(
													commandContext -> method_13068(commandContext.getSource(), method_13054(commandContext), BoolArgumentType.getBool(commandContext, "visible"))
												)
										)
								)
								.then(
									ServerCommandManager.literal("players")
										.executes(commandContext -> method_13031(commandContext.getSource(), method_13054(commandContext), Collections.emptyList()))
										.then(
											ServerCommandManager.argument("targets", EntityArgumentType.multiplePlayer())
												.executes(
													commandContext -> method_13031(commandContext.getSource(), method_13054(commandContext), EntityArgumentType.method_9310(commandContext, "targets"))
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("get")
						.then(
							ServerCommandManager.argument("id", ResourceLocationArgumentType.create())
								.suggests(field_13482)
								.then(ServerCommandManager.literal("value").executes(commandContext -> method_13065(commandContext.getSource(), method_13054(commandContext))))
								.then(ServerCommandManager.literal("max").executes(commandContext -> method_13056(commandContext.getSource(), method_13054(commandContext))))
								.then(ServerCommandManager.literal("visible").executes(commandContext -> method_13041(commandContext.getSource(), method_13054(commandContext))))
								.then(ServerCommandManager.literal("players").executes(commandContext -> method_13030(commandContext.getSource(), method_13054(commandContext))))
						)
				)
		);
	}

	private static int method_13065(ServerCommandSource serverCommandSource, class_3002 arg) {
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.value", arg.method_12965(), arg.method_12955()), true);
		return arg.method_12955();
	}

	private static int method_13056(ServerCommandSource serverCommandSource, class_3002 arg) {
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.max", arg.method_12965(), arg.method_12960()), true);
		return arg.method_12960();
	}

	private static int method_13041(ServerCommandSource serverCommandSource, class_3002 arg) {
		if (arg.method_14093()) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.visible.visible", arg.method_12965()), true);
			return 1;
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.visible.hidden", arg.method_12965()), true);
			return 0;
		}
	}

	private static int method_13030(ServerCommandSource serverCommandSource, class_3002 arg) {
		if (arg.method_14092().isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.get.players.none", arg.method_12965()), true);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent(
					"commands.bossbar.get.players.some", arg.method_12965(), arg.method_14092().size(), TextFormatter.join(arg.method_14092(), PlayerEntity::getDisplayName)
				),
				true
			);
		}

		return arg.method_14092().size();
	}

	private static int method_13068(ServerCommandSource serverCommandSource, class_3002 arg, boolean bl) throws CommandSyntaxException {
		if (arg.method_14093() == bl) {
			if (bl) {
				throw SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION.create();
			} else {
				throw SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION.create();
			}
		} else {
			arg.setVisible(bl);
			if (bl) {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.visible.success.visible", arg.method_12965()), true);
			} else {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.visible.success.hidden", arg.method_12965()), true);
			}

			return 0;
		}
	}

	private static int method_13036(ServerCommandSource serverCommandSource, class_3002 arg, int i) throws CommandSyntaxException {
		if (arg.method_12955() == i) {
			throw SET_VALUE_UNCHANGED_EXCEPTION.create();
		} else {
			arg.method_12954(i);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.value.success", arg.method_12965(), i), true);
			return i;
		}
	}

	private static int method_13066(ServerCommandSource serverCommandSource, class_3002 arg, int i) throws CommandSyntaxException {
		if (arg.method_12960() == i) {
			throw SETMAX_UNCHANGED_EXCEPTION.create();
		} else {
			arg.method_12956(i);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.max.success", arg.method_12965(), i), true);
			return i;
		}
	}

	private static int method_13028(ServerCommandSource serverCommandSource, class_3002 arg, BossBar.Color color) throws CommandSyntaxException {
		if (arg.getColor().equals(color)) {
			throw SET_COLOR_UNCHANGED_EXCEPTION.create();
		} else {
			arg.setColor(color);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.color.success", arg.method_12965()), true);
			return 0;
		}
	}

	private static int method_13050(ServerCommandSource serverCommandSource, class_3002 arg, BossBar.Overlay overlay) throws CommandSyntaxException {
		if (arg.getOverlay().equals(overlay)) {
			throw SET_STYLE_UNCHANGED_EXCEPTION.create();
		} else {
			arg.setOverlay(overlay);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.style.success", arg.method_12965()), true);
			return 0;
		}
	}

	private static int method_13071(ServerCommandSource serverCommandSource, class_3002 arg, TextComponent textComponent) throws CommandSyntaxException {
		TextComponent textComponent2 = TextFormatter.method_10881(serverCommandSource, textComponent, null);
		if (arg.getName().equals(textComponent2)) {
			throw SET_NAME_UNCHANGED_EXCEPTION.create();
		} else {
			arg.setName(textComponent2);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.name.success", arg.method_12965()), true);
			return 0;
		}
	}

	private static int method_13031(ServerCommandSource serverCommandSource, class_3002 arg, Collection<ServerPlayerEntity> collection) throws CommandSyntaxException {
		boolean bl = arg.method_12962(collection);
		if (!bl) {
			throw SET_PLAYERS_UNCHANGED_EXCEPTION.create();
		} else {
			if (arg.method_14092().isEmpty()) {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.set.players.success.none", arg.method_12965()), true);
			} else {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent(
						"commands.bossbar.set.players.success.some", arg.method_12965(), collection.size(), TextFormatter.join(collection, PlayerEntity::getDisplayName)
					),
					true
				);
			}

			return arg.method_14092().size();
		}
	}

	private static int method_13045(ServerCommandSource serverCommandSource) {
		Collection<class_3002> collection = serverCommandSource.getMinecraftServer().method_3837().method_12969();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.list.bars.none"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.bossbar.list.bars.some", collection.size(), TextFormatter.join(collection, class_3002::method_12965)), false
			);
		}

		return collection.size();
	}

	private static int method_13049(ServerCommandSource serverCommandSource, Identifier identifier, TextComponent textComponent) throws CommandSyntaxException {
		class_3004 lv = serverCommandSource.getMinecraftServer().method_3837();
		if (lv.method_12971(identifier) != null) {
			throw CREATE_FAILED_EXCEPTION.create(identifier.toString());
		} else {
			class_3002 lv2 = lv.method_12970(identifier, TextFormatter.method_10881(serverCommandSource, textComponent, null));
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.create.success", lv2.method_12965()), true);
			return lv.method_12969().size();
		}
	}

	private static int method_13069(ServerCommandSource serverCommandSource, class_3002 arg) {
		class_3004 lv = serverCommandSource.getMinecraftServer().method_3837();
		arg.method_14094();
		lv.method_12973(arg);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.bossbar.remove.success", arg.method_12965()), true);
		return lv.method_12969().size();
	}

	public static class_3002 method_13054(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
		Identifier identifier = ResourceLocationArgumentType.getIdentifierArgument(commandContext, "id");
		class_3002 lv = commandContext.getSource().getMinecraftServer().method_3837().method_12971(identifier);
		if (lv == null) {
			throw UNKNOWN_EXCEPTION.create(identifier.toString());
		} else {
			return lv;
		}
	}
}
