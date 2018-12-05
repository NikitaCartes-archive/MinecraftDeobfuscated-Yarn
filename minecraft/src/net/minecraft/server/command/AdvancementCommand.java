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
import net.minecraft.command.arguments.ResourceLocationArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;

public class AdvancementCommand {
	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		Collection<SimpleAdvancement> collection = commandContext.getSource().getMinecraftServer().getAdvancementManager().getAdvancements();
		return CommandSource.suggestIdentifiers(collection.stream().map(SimpleAdvancement::getId), suggestionsBuilder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("advancement")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("grant")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
								.then(
									ServerCommandManager.literal("only")
										.then(
											ServerCommandManager.argument("advancement", ResourceLocationArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															AdvancementCommand.class_3009.field_13457,
															method_12996(ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.class_3010.field_13464)
														)
												)
												.then(
													ServerCommandManager.argument("criterion", StringArgumentType.greedyString())
														.suggests(
															(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
																	ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder
																)
														)
														.executes(
															commandContext -> method_12981(
																	commandContext.getSource(),
																	EntityArgumentType.method_9312(commandContext, "targets"),
																	AdvancementCommand.class_3009.field_13457,
																	ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"),
																	StringArgumentType.getString(commandContext, "criterion")
																)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("from")
										.then(
											ServerCommandManager.argument("advancement", ResourceLocationArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															AdvancementCommand.class_3009.field_13457,
															method_12996(ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.class_3010.field_13458)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("until")
										.then(
											ServerCommandManager.argument("advancement", ResourceLocationArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															AdvancementCommand.class_3009.field_13457,
															method_12996(ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.class_3010.field_13465)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("through")
										.then(
											ServerCommandManager.argument("advancement", ResourceLocationArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															AdvancementCommand.class_3009.field_13457,
															method_12996(ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.class_3010.field_13462)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("everything")
										.executes(
											commandContext -> method_12988(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													AdvancementCommand.class_3009.field_13457,
													commandContext.getSource().getMinecraftServer().getAdvancementManager().getAdvancements()
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("revoke")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
								.then(
									ServerCommandManager.literal("only")
										.then(
											ServerCommandManager.argument("advancement", ResourceLocationArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															AdvancementCommand.class_3009.field_13456,
															method_12996(ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.class_3010.field_13464)
														)
												)
												.then(
													ServerCommandManager.argument("criterion", StringArgumentType.greedyString())
														.suggests(
															(commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
																	ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder
																)
														)
														.executes(
															commandContext -> method_12981(
																	commandContext.getSource(),
																	EntityArgumentType.method_9312(commandContext, "targets"),
																	AdvancementCommand.class_3009.field_13456,
																	ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"),
																	StringArgumentType.getString(commandContext, "criterion")
																)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("from")
										.then(
											ServerCommandManager.argument("advancement", ResourceLocationArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															AdvancementCommand.class_3009.field_13456,
															method_12996(ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.class_3010.field_13458)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("until")
										.then(
											ServerCommandManager.argument("advancement", ResourceLocationArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															AdvancementCommand.class_3009.field_13456,
															method_12996(ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.class_3010.field_13465)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("through")
										.then(
											ServerCommandManager.argument("advancement", ResourceLocationArgumentType.create())
												.suggests(SUGGESTION_PROVIDER)
												.executes(
													commandContext -> method_12988(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															AdvancementCommand.class_3009.field_13456,
															method_12996(ResourceLocationArgumentType.getAdvancementArgument(commandContext, "advancement"), AdvancementCommand.class_3010.field_13462)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("everything")
										.executes(
											commandContext -> method_12988(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													AdvancementCommand.class_3009.field_13456,
													commandContext.getSource().getMinecraftServer().getAdvancementManager().getAdvancements()
												)
										)
								)
						)
				)
		);
	}

	private static int method_12988(
		ServerCommandSource serverCommandSource,
		Collection<ServerPlayerEntity> collection,
		AdvancementCommand.class_3009 arg,
		Collection<SimpleAdvancement> collection2
	) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += arg.method_12999(serverPlayerEntity, collection2);
		}

		if (i == 0) {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					throw new CommandException(
						new TranslatableTextComponent(
							arg.method_13001() + ".one.to.one.failure",
							((SimpleAdvancement)collection2.iterator().next()).getTextComponent(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableTextComponent(
							arg.method_13001() + ".one.to.many.failure", ((SimpleAdvancement)collection2.iterator().next()).getTextComponent(), collection.size()
						)
					);
				}
			} else if (collection.size() == 1) {
				throw new CommandException(
					new TranslatableTextComponent(
						arg.method_13001() + ".many.to.one.failure", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
					)
				);
			} else {
				throw new CommandException(new TranslatableTextComponent(arg.method_13001() + ".many.to.many.failure", collection2.size(), collection.size()));
			}
		} else {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent(
							arg.method_13001() + ".one.to.one.success",
							((SimpleAdvancement)collection2.iterator().next()).getTextComponent(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						),
						true
					);
				} else {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent(
							arg.method_13001() + ".one.to.many.success", ((SimpleAdvancement)collection2.iterator().next()).getTextComponent(), collection.size()
						),
						true
					);
				}
			} else if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent(
						arg.method_13001() + ".many.to.one.success", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
					),
					true
				);
			} else {
				serverCommandSource.sendFeedback(new TranslatableTextComponent(arg.method_13001() + ".many.to.many.success", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}

	private static int method_12981(
		ServerCommandSource serverCommandSource,
		Collection<ServerPlayerEntity> collection,
		AdvancementCommand.class_3009 arg,
		SimpleAdvancement simpleAdvancement,
		String string
	) {
		int i = 0;
		if (!simpleAdvancement.getCriteria().containsKey(string)) {
			throw new CommandException(new TranslatableTextComponent("commands.advancement.criterionNotFound", simpleAdvancement.getTextComponent(), string));
		} else {
			for (ServerPlayerEntity serverPlayerEntity : collection) {
				if (arg.method_13000(serverPlayerEntity, simpleAdvancement, string)) {
					i++;
				}
			}

			if (i == 0) {
				if (collection.size() == 1) {
					throw new CommandException(
						new TranslatableTextComponent(
							arg.method_13001() + ".criterion.to.one.failure",
							string,
							simpleAdvancement.getTextComponent(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableTextComponent(arg.method_13001() + ".criterion.to.many.failure", string, simpleAdvancement.getTextComponent(), collection.size())
					);
				}
			} else {
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent(
							arg.method_13001() + ".criterion.to.one.success",
							string,
							simpleAdvancement.getTextComponent(),
							((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
						),
						true
					);
				} else {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent(arg.method_13001() + ".criterion.to.many.success", string, simpleAdvancement.getTextComponent(), collection.size()), true
					);
				}

				return i;
			}
		}
	}

	private static List<SimpleAdvancement> method_12996(SimpleAdvancement simpleAdvancement, AdvancementCommand.class_3010 arg) {
		List<SimpleAdvancement> list = Lists.<SimpleAdvancement>newArrayList();
		if (arg.field_13460) {
			for (SimpleAdvancement simpleAdvancement2 = simpleAdvancement.getParent(); simpleAdvancement2 != null; simpleAdvancement2 = simpleAdvancement2.getParent()) {
				list.add(simpleAdvancement2);
			}
		}

		list.add(simpleAdvancement);
		if (arg.field_13459) {
			method_12990(simpleAdvancement, list);
		}

		return list;
	}

	private static void method_12990(SimpleAdvancement simpleAdvancement, List<SimpleAdvancement> list) {
		for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
			list.add(simpleAdvancement2);
			method_12990(simpleAdvancement2, list);
		}
	}

	static enum class_3009 {
		field_13457("grant") {
			@Override
			protected boolean method_13002(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementManager().method_12882(simpleAdvancement);
				if (advancementProgress.isDone()) {
					return false;
				} else {
					for (String string : advancementProgress.getAllUnobtained()) {
						serverPlayerEntity.getAdvancementManager().onAdvancement(simpleAdvancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean method_13000(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string) {
				return serverPlayerEntity.getAdvancementManager().onAdvancement(simpleAdvancement, string);
			}
		},
		field_13456("revoke") {
			@Override
			protected boolean method_13002(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementManager().method_12882(simpleAdvancement);
				if (!advancementProgress.isAnyObtained()) {
					return false;
				} else {
					for (String string : advancementProgress.getAllObtained()) {
						serverPlayerEntity.getAdvancementManager().method_12883(simpleAdvancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean method_13000(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string) {
				return serverPlayerEntity.getAdvancementManager().method_12883(simpleAdvancement, string);
			}
		};

		private final String field_13454;

		private class_3009(String string2) {
			this.field_13454 = "commands.advancement." + string2;
		}

		public int method_12999(ServerPlayerEntity serverPlayerEntity, Iterable<SimpleAdvancement> iterable) {
			int i = 0;

			for (SimpleAdvancement simpleAdvancement : iterable) {
				if (this.method_13002(serverPlayerEntity, simpleAdvancement)) {
					i++;
				}
			}

			return i;
		}

		protected abstract boolean method_13002(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement);

		protected abstract boolean method_13000(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string);

		protected String method_13001() {
			return this.field_13454;
		}
	}

	static enum class_3010 {
		field_13464(false, false),
		field_13462(true, true),
		field_13458(false, true),
		field_13465(true, false),
		field_13461(true, true);

		private final boolean field_13460;
		private final boolean field_13459;

		private class_3010(boolean bl, boolean bl2) {
			this.field_13460 = bl;
			this.field_13459 = bl2;
		}
	}
}
