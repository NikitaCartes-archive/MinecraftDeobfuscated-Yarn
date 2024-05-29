package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class AdvancementCommand {
	private static final DynamicCommandExceptionType GENERIC_EXCEPTION = new DynamicCommandExceptionType(message -> (Text)message);
	private static final Dynamic2CommandExceptionType CRITERION_NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType(
		(advancement, criterion) -> Text.stringifiedTranslatable("commands.advancement.criterionNotFound", advancement, criterion)
	);
	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
		Collection<AdvancementEntry> collection = context.getSource().getServer().getAdvancementLoader().getAdvancements();
		return CommandSource.suggestIdentifiers(collection.stream().map(AdvancementEntry::id), builder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("advancement")
				.requires(source -> source.hasPermissionLevel(2))
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
													context -> executeAdvancement(
															context.getSource(),
															EntityArgumentType.getPlayers(context, "targets"),
															AdvancementCommand.Operation.GRANT,
															select(context, IdentifierArgumentType.getAdvancementArgument(context, "advancement"), AdvancementCommand.Selection.ONLY)
														)
												)
												.then(
													CommandManager.argument("criterion", StringArgumentType.greedyString())
														.suggests(
															(context, builder) -> CommandSource.suggestMatching(
																	IdentifierArgumentType.getAdvancementArgument(context, "advancement").value().criteria().keySet(), builder
																)
														)
														.executes(
															context -> executeCriterion(
																	context.getSource(),
																	EntityArgumentType.getPlayers(context, "targets"),
																	AdvancementCommand.Operation.GRANT,
																	IdentifierArgumentType.getAdvancementArgument(context, "advancement"),
																	StringArgumentType.getString(context, "criterion")
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
													context -> executeAdvancement(
															context.getSource(),
															EntityArgumentType.getPlayers(context, "targets"),
															AdvancementCommand.Operation.GRANT,
															select(context, IdentifierArgumentType.getAdvancementArgument(context, "advancement"), AdvancementCommand.Selection.FROM)
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
													context -> executeAdvancement(
															context.getSource(),
															EntityArgumentType.getPlayers(context, "targets"),
															AdvancementCommand.Operation.GRANT,
															select(context, IdentifierArgumentType.getAdvancementArgument(context, "advancement"), AdvancementCommand.Selection.UNTIL)
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
													context -> executeAdvancement(
															context.getSource(),
															EntityArgumentType.getPlayers(context, "targets"),
															AdvancementCommand.Operation.GRANT,
															select(context, IdentifierArgumentType.getAdvancementArgument(context, "advancement"), AdvancementCommand.Selection.THROUGH)
														)
												)
										)
								)
								.then(
									CommandManager.literal("everything")
										.executes(
											context -> executeAdvancement(
													context.getSource(),
													EntityArgumentType.getPlayers(context, "targets"),
													AdvancementCommand.Operation.GRANT,
													context.getSource().getServer().getAdvancementLoader().getAdvancements()
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
													context -> executeAdvancement(
															context.getSource(),
															EntityArgumentType.getPlayers(context, "targets"),
															AdvancementCommand.Operation.REVOKE,
															select(context, IdentifierArgumentType.getAdvancementArgument(context, "advancement"), AdvancementCommand.Selection.ONLY)
														)
												)
												.then(
													CommandManager.argument("criterion", StringArgumentType.greedyString())
														.suggests(
															(context, builder) -> CommandSource.suggestMatching(
																	IdentifierArgumentType.getAdvancementArgument(context, "advancement").value().criteria().keySet(), builder
																)
														)
														.executes(
															context -> executeCriterion(
																	context.getSource(),
																	EntityArgumentType.getPlayers(context, "targets"),
																	AdvancementCommand.Operation.REVOKE,
																	IdentifierArgumentType.getAdvancementArgument(context, "advancement"),
																	StringArgumentType.getString(context, "criterion")
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
													context -> executeAdvancement(
															context.getSource(),
															EntityArgumentType.getPlayers(context, "targets"),
															AdvancementCommand.Operation.REVOKE,
															select(context, IdentifierArgumentType.getAdvancementArgument(context, "advancement"), AdvancementCommand.Selection.FROM)
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
													context -> executeAdvancement(
															context.getSource(),
															EntityArgumentType.getPlayers(context, "targets"),
															AdvancementCommand.Operation.REVOKE,
															select(context, IdentifierArgumentType.getAdvancementArgument(context, "advancement"), AdvancementCommand.Selection.UNTIL)
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
													context -> executeAdvancement(
															context.getSource(),
															EntityArgumentType.getPlayers(context, "targets"),
															AdvancementCommand.Operation.REVOKE,
															select(context, IdentifierArgumentType.getAdvancementArgument(context, "advancement"), AdvancementCommand.Selection.THROUGH)
														)
												)
										)
								)
								.then(
									CommandManager.literal("everything")
										.executes(
											context -> executeAdvancement(
													context.getSource(),
													EntityArgumentType.getPlayers(context, "targets"),
													AdvancementCommand.Operation.REVOKE,
													context.getSource().getServer().getAdvancementLoader().getAdvancements()
												)
										)
								)
						)
				)
		);
	}

	private static int executeAdvancement(
		ServerCommandSource source, Collection<ServerPlayerEntity> targets, AdvancementCommand.Operation operation, Collection<AdvancementEntry> selection
	) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			i += operation.processAll(serverPlayerEntity, selection);
		}

		if (i == 0) {
			if (selection.size() == 1) {
				if (targets.size() == 1) {
					throw GENERIC_EXCEPTION.create(
						Text.translatable(
							operation.getCommandPrefix() + ".one.to.one.failure",
							Advancement.getNameFromIdentity((AdvancementEntry)selection.iterator().next()),
							((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw GENERIC_EXCEPTION.create(
						Text.translatable(
							operation.getCommandPrefix() + ".one.to.many.failure", Advancement.getNameFromIdentity((AdvancementEntry)selection.iterator().next()), targets.size()
						)
					);
				}
			} else if (targets.size() == 1) {
				throw GENERIC_EXCEPTION.create(
					Text.translatable(
						operation.getCommandPrefix() + ".many.to.one.failure", selection.size(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
					)
				);
			} else {
				throw GENERIC_EXCEPTION.create(Text.translatable(operation.getCommandPrefix() + ".many.to.many.failure", selection.size(), targets.size()));
			}
		} else {
			if (selection.size() == 1) {
				if (targets.size() == 1) {
					source.sendFeedback(
						() -> Text.translatable(
								operation.getCommandPrefix() + ".one.to.one.success",
								Advancement.getNameFromIdentity((AdvancementEntry)selection.iterator().next()),
								((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
							),
						true
					);
				} else {
					source.sendFeedback(
						() -> Text.translatable(
								operation.getCommandPrefix() + ".one.to.many.success", Advancement.getNameFromIdentity((AdvancementEntry)selection.iterator().next()), targets.size()
							),
						true
					);
				}
			} else if (targets.size() == 1) {
				source.sendFeedback(
					() -> Text.translatable(
							operation.getCommandPrefix() + ".many.to.one.success", selection.size(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
						),
					true
				);
			} else {
				source.sendFeedback(() -> Text.translatable(operation.getCommandPrefix() + ".many.to.many.success", selection.size(), targets.size()), true);
			}

			return i;
		}
	}

	private static int executeCriterion(
		ServerCommandSource source, Collection<ServerPlayerEntity> targets, AdvancementCommand.Operation operation, AdvancementEntry advancement, String criterion
	) throws CommandSyntaxException {
		int i = 0;
		Advancement advancement2 = advancement.value();
		if (!advancement2.criteria().containsKey(criterion)) {
			throw CRITERION_NOT_FOUND_EXCEPTION.create(Advancement.getNameFromIdentity(advancement), criterion);
		} else {
			for (ServerPlayerEntity serverPlayerEntity : targets) {
				if (operation.processEachCriterion(serverPlayerEntity, advancement, criterion)) {
					i++;
				}
			}

			if (i == 0) {
				if (targets.size() == 1) {
					throw GENERIC_EXCEPTION.create(
						Text.translatable(
							operation.getCommandPrefix() + ".criterion.to.one.failure",
							criterion,
							Advancement.getNameFromIdentity(advancement),
							((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw GENERIC_EXCEPTION.create(
						Text.translatable(operation.getCommandPrefix() + ".criterion.to.many.failure", criterion, Advancement.getNameFromIdentity(advancement), targets.size())
					);
				}
			} else {
				if (targets.size() == 1) {
					source.sendFeedback(
						() -> Text.translatable(
								operation.getCommandPrefix() + ".criterion.to.one.success",
								criterion,
								Advancement.getNameFromIdentity(advancement),
								((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
							),
						true
					);
				} else {
					source.sendFeedback(
						() -> Text.translatable(
								operation.getCommandPrefix() + ".criterion.to.many.success", criterion, Advancement.getNameFromIdentity(advancement), targets.size()
							),
						true
					);
				}

				return i;
			}
		}
	}

	private static List<AdvancementEntry> select(CommandContext<ServerCommandSource> context, AdvancementEntry advancement, AdvancementCommand.Selection selection) {
		AdvancementManager advancementManager = context.getSource().getServer().getAdvancementLoader().getManager();
		PlacedAdvancement placedAdvancement = advancementManager.get(advancement);
		if (placedAdvancement == null) {
			return List.of(advancement);
		} else {
			List<AdvancementEntry> list = new ArrayList();
			if (selection.before) {
				for (PlacedAdvancement placedAdvancement2 = placedAdvancement.getParent(); placedAdvancement2 != null; placedAdvancement2 = placedAdvancement2.getParent()) {
					list.add(placedAdvancement2.getAdvancementEntry());
				}
			}

			list.add(advancement);
			if (selection.after) {
				addChildrenRecursivelyToList(placedAdvancement, list);
			}

			return list;
		}
	}

	private static void addChildrenRecursivelyToList(PlacedAdvancement parent, List<AdvancementEntry> childList) {
		for (PlacedAdvancement placedAdvancement : parent.getChildren()) {
			childList.add(placedAdvancement.getAdvancementEntry());
			addChildrenRecursivelyToList(placedAdvancement, childList);
		}
	}

	static enum Operation {
		GRANT("grant") {
			@Override
			protected boolean processEach(ServerPlayerEntity player, AdvancementEntry advancement) {
				AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(advancement);
				if (advancementProgress.isDone()) {
					return false;
				} else {
					for (String string : advancementProgress.getUnobtainedCriteria()) {
						player.getAdvancementTracker().grantCriterion(advancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean processEachCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) {
				return player.getAdvancementTracker().grantCriterion(advancement, criterion);
			}
		},
		REVOKE("revoke") {
			@Override
			protected boolean processEach(ServerPlayerEntity player, AdvancementEntry advancement) {
				AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(advancement);
				if (!advancementProgress.isAnyObtained()) {
					return false;
				} else {
					for (String string : advancementProgress.getObtainedCriteria()) {
						player.getAdvancementTracker().revokeCriterion(advancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean processEachCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) {
				return player.getAdvancementTracker().revokeCriterion(advancement, criterion);
			}
		};

		private final String commandPrefix;

		Operation(final String name) {
			this.commandPrefix = "commands.advancement." + name;
		}

		public int processAll(ServerPlayerEntity player, Iterable<AdvancementEntry> advancements) {
			int i = 0;

			for (AdvancementEntry advancementEntry : advancements) {
				if (this.processEach(player, advancementEntry)) {
					i++;
				}
			}

			return i;
		}

		protected abstract boolean processEach(ServerPlayerEntity player, AdvancementEntry advancement);

		protected abstract boolean processEachCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion);

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

		final boolean before;
		final boolean after;

		private Selection(final boolean before, final boolean after) {
			this.before = before;
			this.after = after;
		}
	}
}
