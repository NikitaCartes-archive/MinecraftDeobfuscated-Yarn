package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;

public class AdvancementCommand {
	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		Collection<SimpleAdvancement> collection = commandContext.getSource().getMinecraftServer().getAdvancementManager().getAdvancements();
		return CommandSource.suggestIdentifiers(collection.stream().map(SimpleAdvancement::getId), suggestionsBuilder);
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
											CommandManager.argument("advancement", IdentifierArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.GRANT,
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
											CommandManager.argument("advancement", IdentifierArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.GRANT,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13458)
														)
												)
										)
								)
								.then(
									CommandManager.literal("until")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.GRANT,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13465)
														)
												)
										)
								)
								.then(
									CommandManager.literal("through")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.GRANT,
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
											CommandManager.argument("advancement", IdentifierArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.REVOKE,
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
											CommandManager.argument("advancement", IdentifierArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.REVOKE,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13458)
														)
												)
										)
								)
								.then(
									CommandManager.literal("until")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.REVOKE,
															select(IdentifierArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.Selection.field_13465)
														)
												)
										)
								)
								.then(
									CommandManager.literal("through")
										.then(
											CommandManager.argument("advancement", IdentifierArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeAdvancement(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															AdvancementCommand.Operation.REVOKE,
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
		ServerCommandSource serverCommandSource,
		Collection<ServerPlayerEntity> collection,
		AdvancementCommand.Operation operation,
		Collection<SimpleAdvancement> collection2
	) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += operation.processAll(serverPlayerEntity, collection2);
		}

		if (i == 0) {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					throw new CommandException(
						new TranslatableTextComponent(
							operation.getCommandPrefix() + ".one.to.one.failure",
							((SimpleAdvancement)collection2.iterator().next()).getTextComponent(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableTextComponent(
							operation.getCommandPrefix() + ".one.to.many.failure", ((SimpleAdvancement)collection2.iterator().next()).getTextComponent(), collection.size()
						)
					);
				}
			} else if (collection.size() == 1) {
				throw new CommandException(
					new TranslatableTextComponent(
						operation.getCommandPrefix() + ".many.to.one.failure", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
					)
				);
			} else {
				throw new CommandException(new TranslatableTextComponent(operation.getCommandPrefix() + ".many.to.many.failure", collection2.size(), collection.size()));
			}
		} else {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent(
							operation.getCommandPrefix() + ".one.to.one.success",
							((SimpleAdvancement)collection2.iterator().next()).getTextComponent(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						),
						true
					);
				} else {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent(
							operation.getCommandPrefix() + ".one.to.many.success", ((SimpleAdvancement)collection2.iterator().next()).getTextComponent(), collection.size()
						),
						true
					);
				}
			} else if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent(
						operation.getCommandPrefix() + ".many.to.one.success", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
					),
					true
				);
			} else {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent(operation.getCommandPrefix() + ".many.to.many.success", collection2.size(), collection.size()), true
				);
			}

			return i;
		}
	}

	private static int executeCriterion(
		ServerCommandSource serverCommandSource,
		Collection<ServerPlayerEntity> collection,
		AdvancementCommand.Operation operation,
		SimpleAdvancement simpleAdvancement,
		String string
	) {
		int i = 0;
		if (!simpleAdvancement.getCriteria().containsKey(string)) {
			throw new CommandException(new TranslatableTextComponent("commands.advancement.criterionNotFound", simpleAdvancement.getTextComponent(), string));
		} else {
			for (ServerPlayerEntity serverPlayerEntity : collection) {
				if (operation.processEachCriterion(serverPlayerEntity, simpleAdvancement, string)) {
					i++;
				}
			}

			if (i == 0) {
				if (collection.size() == 1) {
					throw new CommandException(
						new TranslatableTextComponent(
							operation.getCommandPrefix() + ".criterion.to.one.failure",
							string,
							simpleAdvancement.getTextComponent(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableTextComponent(
							operation.getCommandPrefix() + ".criterion.to.many.failure", string, simpleAdvancement.getTextComponent(), collection.size()
						)
					);
				}
			} else {
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent(
							operation.getCommandPrefix() + ".criterion.to.one.success",
							string,
							simpleAdvancement.getTextComponent(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						),
						true
					);
				} else {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent(
							operation.getCommandPrefix() + ".criterion.to.many.success", string, simpleAdvancement.getTextComponent(), collection.size()
						),
						true
					);
				}

				return i;
			}
		}
	}

	private static List<SimpleAdvancement> select(SimpleAdvancement simpleAdvancement, AdvancementCommand.Selection selection) {
		List<SimpleAdvancement> list = Lists.<SimpleAdvancement>newArrayList();
		if (selection.before) {
			for (SimpleAdvancement simpleAdvancement2 = simpleAdvancement.getParent(); simpleAdvancement2 != null; simpleAdvancement2 = simpleAdvancement2.getParent()) {
				list.add(simpleAdvancement2);
			}
		}

		list.add(simpleAdvancement);
		if (selection.after) {
			searchAndAdd(simpleAdvancement, list);
		}

		return list;
	}

	private static void searchAndAdd(SimpleAdvancement simpleAdvancement, List<SimpleAdvancement> list) {
		for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
			list.add(simpleAdvancement2);
			searchAndAdd(simpleAdvancement2, list);
		}
	}

	static enum Operation {
		GRANT("grant") {
			@Override
			protected boolean processEach(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementManager().getProgress(simpleAdvancement);
				if (advancementProgress.isDone()) {
					return false;
				} else {
					for (String string : advancementProgress.getUnobtainedCriteria()) {
						serverPlayerEntity.getAdvancementManager().grantCriterion(simpleAdvancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string) {
				return serverPlayerEntity.getAdvancementManager().grantCriterion(simpleAdvancement, string);
			}
		},
		REVOKE("revoke") {
			@Override
			protected boolean processEach(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementManager().getProgress(simpleAdvancement);
				if (!advancementProgress.isAnyObtained()) {
					return false;
				} else {
					for (String string : advancementProgress.getObtainedCriteria()) {
						serverPlayerEntity.getAdvancementManager().revokeCriterion(simpleAdvancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string) {
				return serverPlayerEntity.getAdvancementManager().revokeCriterion(simpleAdvancement, string);
			}
		};

		private final String commandPrefix;

		private Operation(String string2) {
			this.commandPrefix = "commands.advancement." + string2;
		}

		public int processAll(ServerPlayerEntity serverPlayerEntity, Iterable<SimpleAdvancement> iterable) {
			int i = 0;

			for (SimpleAdvancement simpleAdvancement : iterable) {
				if (this.processEach(serverPlayerEntity, simpleAdvancement)) {
					i++;
				}
			}

			return i;
		}

		protected abstract boolean processEach(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement);

		protected abstract boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string);

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
