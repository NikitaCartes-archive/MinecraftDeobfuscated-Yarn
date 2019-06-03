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
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class TagCommand {
	private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.tag.add.failed"));
	private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.tag.remove.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("tag")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.entities())
						.then(
							CommandManager.literal("add")
								.then(
									CommandManager.argument("name", StringArgumentType.word())
										.executes(
											commandContext -> executeAdd(
													commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")
												)
										)
								)
						)
						.then(
							CommandManager.literal("remove")
								.then(
									CommandManager.argument("name", StringArgumentType.word())
										.suggests(
											(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
													getTags(EntityArgumentType.getEntities(commandContext, "targets")), suggestionsBuilder
												)
										)
										.executes(
											commandContext -> executeRemove(
													commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")
												)
										)
								)
						)
						.then(
							CommandManager.literal("list")
								.executes(commandContext -> executeList(commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets")))
						)
				)
		);
	}

	private static Collection<String> getTags(Collection<? extends Entity> collection) {
		Set<String> set = Sets.<String>newHashSet();

		for (Entity entity : collection) {
			set.addAll(entity.getScoreboardTags());
		}

		return set;
	}

	private static int executeAdd(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
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
				serverCommandSource.method_9226(new TranslatableText("commands.tag.add.success.single", string, ((Entity)collection.iterator().next()).method_5476()), true);
			} else {
				serverCommandSource.method_9226(new TranslatableText("commands.tag.add.success.multiple", string, collection.size()), true);
			}

			return i;
		}
	}

	private static int executeRemove(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
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
					new TranslatableText("commands.tag.remove.success.single", string, ((Entity)collection.iterator().next()).method_5476()), true
				);
			} else {
				serverCommandSource.method_9226(new TranslatableText("commands.tag.remove.success.multiple", string, collection.size()), true);
			}

			return i;
		}
	}

	private static int executeList(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection) {
		Set<String> set = Sets.<String>newHashSet();

		for (Entity entity : collection) {
			set.addAll(entity.getScoreboardTags());
		}

		if (collection.size() == 1) {
			Entity entity2 = (Entity)collection.iterator().next();
			if (set.isEmpty()) {
				serverCommandSource.method_9226(new TranslatableText("commands.tag.list.single.empty", entity2.method_5476()), false);
			} else {
				serverCommandSource.method_9226(new TranslatableText("commands.tag.list.single.success", entity2.method_5476(), set.size(), Texts.joinOrdered(set)), false);
			}
		} else if (set.isEmpty()) {
			serverCommandSource.method_9226(new TranslatableText("commands.tag.list.multiple.empty", collection.size()), false);
		} else {
			serverCommandSource.method_9226(new TranslatableText("commands.tag.list.multiple.success", collection.size(), set.size(), Texts.joinOrdered(set)), false);
		}

		return set.size();
	}
}
