package net.minecraft.server.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Set;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;

public class TagCommand {
	private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.tag.add.failed"));
	private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.tag.remove.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("tag")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities())
						.then(
							ServerCommandManager.literal("add")
								.then(
									ServerCommandManager.argument("name", StringArgumentType.word())
										.executes(
											commandContext -> method_13702(
													commandContext.getSource(), EntityArgumentType.method_9317(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("remove")
								.then(
									ServerCommandManager.argument("name", StringArgumentType.word())
										.suggests(
											(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
													method_13706(EntityArgumentType.method_9317(commandContext, "targets")), suggestionsBuilder
												)
										)
										.executes(
											commandContext -> method_13699(
													commandContext.getSource(), EntityArgumentType.method_9317(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")
												)
										)
								)
						)
						.then(
							ServerCommandManager.literal("list")
								.executes(commandContext -> method_13700(commandContext.getSource(), EntityArgumentType.method_9317(commandContext, "targets")))
						)
				)
		);
	}

	private static Collection<String> method_13706(Collection<? extends Entity> collection) {
		Set<String> set = Sets.<String>newHashSet();

		for (Entity entity : collection) {
			set.addAll(entity.getScoreboardTags());
		}

		return set;
	}

	private static int method_13702(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity.addScoreboardTag(string)) {
				i++;
			}
		}

		if (i == 0) {
			throw ADD_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.method_9226(
					new TranslatableTextComponent("commands.tag.add.success.single", string, ((Entity)collection.iterator().next()).method_5476()), true
				);
			} else {
				serverCommandSource.method_9226(new TranslatableTextComponent("commands.tag.add.success.multiple", string, collection.size()), true);
			}

			return i;
		}
	}

	private static int method_13699(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity.removeScoreboardTag(string)) {
				i++;
			}
		}

		if (i == 0) {
			throw REMOVE_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.method_9226(
					new TranslatableTextComponent("commands.tag.remove.success.single", string, ((Entity)collection.iterator().next()).method_5476()), true
				);
			} else {
				serverCommandSource.method_9226(new TranslatableTextComponent("commands.tag.remove.success.multiple", string, collection.size()), true);
			}

			return i;
		}
	}

	private static int method_13700(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection) {
		Set<String> set = Sets.<String>newHashSet();

		for (Entity entity : collection) {
			set.addAll(entity.getScoreboardTags());
		}

		if (collection.size() == 1) {
			Entity entity2 = (Entity)collection.iterator().next();
			if (set.isEmpty()) {
				serverCommandSource.method_9226(new TranslatableTextComponent("commands.tag.list.single.empty", entity2.method_5476()), false);
			} else {
				serverCommandSource.method_9226(
					new TranslatableTextComponent("commands.tag.list.single.success", entity2.method_5476(), set.size(), TextFormatter.sortedJoin(set)), false
				);
			}
		} else if (set.isEmpty()) {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.tag.list.multiple.empty", collection.size()), false);
		} else {
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.tag.list.multiple.success", collection.size(), set.size(), TextFormatter.sortedJoin(set)), false
			);
		}

		return set.size();
	}
}
