package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class AdvancementCommand {
	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		Collection<Advancement> collection = commandContext.getSource().getMinecraftServer().getAdvancementManager().getAdvancements();
		return CommandSource.suggestIdentifiers(collection.stream().map(Advancement::getId), suggestionsBuilder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("advancement")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("grant")
						.then(
							CommandManager.argument("targets", EntityArgumentType.players())
								.then(
									CommandManager.literal("only")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.identifier())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.field_13457,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13464)
														)
												)
												.then(
													CommandManager.argument("criterion", StringArgumentType.greedyString())
														.suggests(
															(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
																	IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder
																)
														)
														.executes(
															commandContext -> executeCriterion(
																	commandContext.getSource(),
																	EntityArgumentType.getPlayers(commandContext, "targets"),
																	AdvancementCommand.Operation.field_13457,
																	IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"),
																	StringArgumentType.getString(commandContext, "criterion")
																)
														)
												)
										)
								)
								.then(
									CommandManager.literal("from")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.identifier())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.field_13457,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13458)
														)
												)
										)
								)
								.then(
									CommandManager.literal("until")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.identifier())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.field_13457,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13465)
														)
												)
										)
								)
								.then(
									CommandManager.literal("through")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.identifier())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.field_13457,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13462)
														)
												)
										)
								)
								.then(
									CommandManager.literal("everything")
										.executes(
											commandContext -> executeAdvancement(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													AdvancementCommand.Operation.field_13457,
													commandContext.getSource().getMinecraftServer().getAdvancementManager().getAdvancements()
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("revoke")
						.then(
							CommandManager.argument("targets", EntityArgumentType.players())
								.then(
									CommandManager.literal("only")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.identifier())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.field_13456,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13464)
														)
												)
												.then(
													CommandManager.argument("criterion", StringArgumentType.greedyString())
														.suggests(
															(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
																	IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder
																)
														)
														.executes(
															commandContext -> executeCriterion(
																	commandContext.getSource(),
																	EntityArgumentType.getPlayers(commandContext, "targets"),
																	AdvancementCommand.Operation.field_13456,
																	IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"),
																	StringArgumentType.getString(commandContext, "criterion")
																)
														)
												)
										)
								)
								.then(
									CommandManager.literal("from")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.identifier())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.field_13456,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13458)
														)
												)
										)
								)
								.then(
									CommandManager.literal("until")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.identifier())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.field_13456,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13465)
														)
												)
										)
								)
								.then(
									CommandManager.literal("through")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.identifier())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.field_13456,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13462)
														)
												)
										)
								)
								.then(
									CommandManager.literal("everything")
										.executes(
											commandContext -> executeAdvancement(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													AdvancementCommand.Operation.field_13456,
													commandContext.getSource().getMinecraftServer().getAdvancementManager().getAdvancements()
												)
										)
								)
						)
				)
		);
	}

	private static int executeAdvancement(
		ServerCommandSource serverCommandSource,
		Collection<ServerPlayerEntity> collection,
		AdvancementCommand.Operation operation,
		Collection<Advancement> collection2
	) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += operation.processAll(serverPlayerEntity, collection2);
		}

		if (i == 0) {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					throw new CommandException(
						new TranslatableText(
							operation.getCommandPrefix() + ".one.to.one.failure",
							((Advancement)collection2.iterator().next()).toHoverableText(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableText(
							operation.getCommandPrefix() + ".one.to.many.failure", ((Advancement)collection2.iterator().next()).toHoverableText(), collection.size()
						)
					);
				}
			} else if (collection.size() == 1) {
				throw new CommandException(
					new TranslatableText(
						operation.getCommandPrefix() + ".many.to.one.failure", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
					)
				);
			} else {
				throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".many.to.many.failure", collection2.size(), collection.size()));
			}
		} else {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						new TranslatableText(
							operation.getCommandPrefix() + ".one.to.one.success",
							((Advancement)collection2.iterator().next()).toHoverableText(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						),
						true
					);
				} else {
					serverCommandSource.sendFeedback(
						new TranslatableText(
							operation.getCommandPrefix() + ".one.to.many.success", ((Advancement)collection2.iterator().next()).toHoverableText(), collection.size()
						),
						true
					);
				}
			} else if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableText(
						operation.getCommandPrefix() + ".many.to.one.success", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
					),
					true
				);
			} else {
				serverCommandSource.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".many.to.many.success", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}

	private static int executeCriterion(
		ServerCommandSource serverCommandSource,
		Collection<ServerPlayerEntity> collection,
		AdvancementCommand.Operation operation,
		Advancement advancement,
		String string
	) {
		int i = 0;
		if (!advancement.getCriteria().containsKey(string)) {
			throw new CommandException(new TranslatableText("commands.advancement.criterionNotFound", advancement.toHoverableText(), string));
		} else {
			for (ServerPlayerEntity serverPlayerEntity : collection) {
				if (operation.processEachCriterion(serverPlayerEntity, advancement, string)) {
					i++;
				}
			}

			if (i == 0) {
				if (collection.size() == 1) {
					throw new CommandException(
						new TranslatableText(
							operation.getCommandPrefix() + ".criterion.to.one.failure",
							string,
							advancement.toHoverableText(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableText(operation.getCommandPrefix() + ".criterion.to.many.failure", string, advancement.toHoverableText(), collection.size())
					);
				}
			} else {
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						new TranslatableText(
							operation.getCommandPrefix() + ".criterion.to.one.success",
							string,
							advancement.toHoverableText(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						),
						true
					);
				} else {
					serverCommandSource.sendFeedback(
						new TranslatableText(operation.getCommandPrefix() + ".criterion.to.many.success", string, advancement.toHoverableText(), collection.size()), true
					);
				}

				return i;
			}
		}
	}

	private static List<Advancement> select(Advancement advancement, AdvancementCommand.Selection selection) {
		List<Advancement> list = Lists.<Advancement>newArrayList();
		if (selection.before) {
			for (Advancement advancement2 = advancement.getParent(); advancement2 != null; advancement2 = advancement2.getParent()) {
				list.add(advancement2);
			}
		}

		list.add(advancement);
		if (selection.after) {
			addChildrenRecursivelyToList(advancement, list);
		}

		return list;
	}

	private static void addChildrenRecursivelyToList(Advancement advancement, List<Advancement> list) {
		for (Advancement advancement2 : advancement.getChildren()) {
			list.add(advancement2);
			addChildrenRecursivelyToList(advancement2, list);
		}
	}

	static enum Operation {
		field_13457("grant") {
			@Override
			protected boolean processEach(ServerPlayerEntity serverPlayerEntity, Advancement advancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementManager().getProgress(advancement);
				if (advancementProgress.isDone()) {
					return false;
				} else {
					for (String string : advancementProgress.getUnobtainedCriteria()) {
						serverPlayerEntity.getAdvancementManager().grantCriterion(advancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String string) {
				return serverPlayerEntity.getAdvancementManager().grantCriterion(advancement, string);
			}
		},
		field_13456("revoke") {
			@Override
			protected boolean processEach(ServerPlayerEntity serverPlayerEntity, Advancement advancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementManager().getProgress(advancement);
				if (!advancementProgress.isAnyObtained()) {
					return false;
				} else {
					for (String string : advancementProgress.getObtainedCriteria()) {
						serverPlayerEntity.getAdvancementManager().revokeCriterion(advancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String string) {
				return serverPlayerEntity.getAdvancementManager().revokeCriterion(advancement, string);
			}
		};

		private final String commandPrefix;

		private Operation(String string2) {
			this.commandPrefix = "commands.advancement." + string2;
		}

		public int processAll(ServerPlayerEntity serverPlayerEntity, Iterable<Advancement> iterable) {
			int i = 0;

			for (Advancement advancement : iterable) {
				if (this.processEach(serverPlayerEntity, advancement)) {
					i++;
				}
			}

			return i;
		}

		protected abstract boolean processEach(ServerPlayerEntity serverPlayerEntity, Advancement advancement);

		protected abstract boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String string);

		protected String getCommandPrefix() {
			return this.commandPrefix;
		}
	}

	static enum Selection {
		field_13464(false, false),
		field_13462(true, true),
		field_13458(false, true),
		field_13465(true, false),
		field_13461(true, true);

		private final boolean before;
		private final boolean after;

		private Selection(boolean bl, boolean bl2) {
			this.before = bl;
			this.after = bl2;
		}
	}
}
