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

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
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
															AdvancementCommand.Operation.GRANT,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.ONLY)
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
																	AdvancementCommand.Operation.GRANT,
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
															AdvancementCommand.Operation.GRANT,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.FROM)
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
															AdvancementCommand.Operation.GRANT,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.UNTIL)
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
															AdvancementCommand.Operation.GRANT,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.THROUGH)
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
													AdvancementCommand.Operation.GRANT,
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
															AdvancementCommand.Operation.REVOKE,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.ONLY)
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
																	AdvancementCommand.Operation.REVOKE,
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
															AdvancementCommand.Operation.REVOKE,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.FROM)
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
															AdvancementCommand.Operation.REVOKE,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.UNTIL)
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
															AdvancementCommand.Operation.REVOKE,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.THROUGH)
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
													AdvancementCommand.Operation.REVOKE,
													commandContext.getSource().getMinecraftServer().getAdvancementManager().getAdvancements()
												)
										)
								)
						)
				)
		);
	}

	private static int executeAdvancement(
		ServerCommandSource source, Collection<ServerPlayerEntity> targets, AdvancementCommand.Operation operation, Collection<Advancement> selection
	) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			i += operation.processAll(serverPlayerEntity, selection);
		}

		if (i == 0) {
			if (selection.size() == 1) {
				if (targets.size() == 1) {
					throw new CommandException(
						new TranslatableText(
							operation.getCommandPrefix() + ".one.to.one.failure",
							((Advancement)selection.iterator().next()).toHoverableText(),
							((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableText(operation.getCommandPrefix() + ".one.to.many.failure", ((Advancement)selection.iterator().next()).toHoverableText(), targets.size())
					);
				}
			} else if (targets.size() == 1) {
				throw new CommandException(
					new TranslatableText(
						operation.getCommandPrefix() + ".many.to.one.failure", selection.size(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
					)
				);
			} else {
				throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".many.to.many.failure", selection.size(), targets.size()));
			}
		} else {
			if (selection.size() == 1) {
				if (targets.size() == 1) {
					source.sendFeedback(
						new TranslatableText(
							operation.getCommandPrefix() + ".one.to.one.success",
							((Advancement)selection.iterator().next()).toHoverableText(),
							((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
						),
						true
					);
				} else {
					source.sendFeedback(
						new TranslatableText(operation.getCommandPrefix() + ".one.to.many.success", ((Advancement)selection.iterator().next()).toHoverableText(), targets.size()),
						true
					);
				}
			} else if (targets.size() == 1) {
				source.sendFeedback(
					new TranslatableText(
						operation.getCommandPrefix() + ".many.to.one.success", selection.size(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
					),
					true
				);
			} else {
				source.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".many.to.many.success", selection.size(), targets.size()), true);
			}

			return i;
		}
	}

	private static int executeCriterion(
		ServerCommandSource source, Collection<ServerPlayerEntity> targets, AdvancementCommand.Operation operation, Advancement advancement, String criterion
	) {
		int i = 0;
		if (!advancement.getCriteria().containsKey(criterion)) {
			throw new CommandException(new TranslatableText("commands.advancement.criterionNotFound", advancement.toHoverableText(), criterion));
		} else {
			for (ServerPlayerEntity serverPlayerEntity : targets) {
				if (operation.processEachCriterion(serverPlayerEntity, advancement, criterion)) {
					i++;
				}
			}

			if (i == 0) {
				if (targets.size() == 1) {
					throw new CommandException(
						new TranslatableText(
							operation.getCommandPrefix() + ".criterion.to.one.failure",
							criterion,
							advancement.toHoverableText(),
							((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableText(operation.getCommandPrefix() + ".criterion.to.many.failure", criterion, advancement.toHoverableText(), targets.size())
					);
				}
			} else {
				if (targets.size() == 1) {
					source.sendFeedback(
						new TranslatableText(
							operation.getCommandPrefix() + ".criterion.to.one.success",
							criterion,
							advancement.toHoverableText(),
							((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
						),
						true
					);
				} else {
					source.sendFeedback(
						new TranslatableText(operation.getCommandPrefix() + ".criterion.to.many.success", criterion, advancement.toHoverableText(), targets.size()), true
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

	private static void addChildrenRecursivelyToList(Advancement parent, List<Advancement> childList) {
		for (Advancement advancement : parent.getChildren()) {
			childList.add(advancement);
			addChildrenRecursivelyToList(advancement, childList);
		}
	}

	static enum Operation {
		GRANT("grant") {
			@Override
			protected boolean processEach(ServerPlayerEntity serverPlayerEntity, Advancement advancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementTracker().getProgress(advancement);
				if (advancementProgress.isDone()) {
					return false;
				} else {
					for (String string : advancementProgress.getUnobtainedCriteria()) {
						serverPlayerEntity.getAdvancementTracker().grantCriterion(advancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String criterion) {
				return serverPlayerEntity.getAdvancementTracker().grantCriterion(advancement, criterion);
			}
		},
		REVOKE("revoke") {
			@Override
			protected boolean processEach(ServerPlayerEntity serverPlayerEntity, Advancement advancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementTracker().getProgress(advancement);
				if (!advancementProgress.isAnyObtained()) {
					return false;
				} else {
					for (String string : advancementProgress.getObtainedCriteria()) {
						serverPlayerEntity.getAdvancementTracker().revokeCriterion(advancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String criterion) {
				return serverPlayerEntity.getAdvancementTracker().revokeCriterion(advancement, criterion);
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

		protected abstract boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String criterion);

		protected String getCommandPrefix() {
			return this.commandPrefix;
		}
	}

	static enum Selection {
		ONLY(false, false),
		THROUGH(true, true),
		FROM(false, true),
		UNTIL(true, false),
		EVERYTHING(true, true);

		private final boolean before;
		private final boolean after;

		private Selection(boolean bl, boolean bl2) {
			this.before = bl;
			this.after = bl2;
		}
	}
}
