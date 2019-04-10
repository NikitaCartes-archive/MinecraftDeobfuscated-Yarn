package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.NumberRangeArgumentType;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.SwizzleArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;

public class ExecuteCommand {
	private static final Dynamic2CommandExceptionType BLOCKS_TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("commands.execute.blocks.toobig", object, object2)
	);
	private static final SimpleCommandExceptionType CONDITIONAL_FAIL_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.execute.conditional.fail")
	);
	private static final DynamicCommandExceptionType CONDITIONAL_FAIL_COUNT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.execute.conditional.fail_count", object)
	);
	private static final BinaryOperator<ResultConsumer<ServerCommandSource>> field_13634 = (resultConsumer, resultConsumer2) -> (commandContext, bl, i) -> {
			resultConsumer.onCommandComplete(commandContext, bl, i);
			resultConsumer2.onCommandComplete(commandContext, bl, i);
		};

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			CommandManager.literal("execute").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
		);
		commandDispatcher.register(
			CommandManager.literal("execute")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(CommandManager.literal("run").redirect(commandDispatcher.getRoot()))
				.then(addConditionArguments(literalCommandNode, CommandManager.literal("if"), true))
				.then(addConditionArguments(literalCommandNode, CommandManager.literal("unless"), false))
				.then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, commandContext -> {
					List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

					for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
						list.add(commandContext.getSource().withEntity(entity));
					}

					return list;
				})))
				.then(CommandManager.literal("at").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, commandContext -> {
					List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

					for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
						list.add(commandContext.getSource().withWorld((ServerWorld)entity.world).withPosition(entity.getPosVector()).withRotation(entity.getRotationClient()));
					}

					return list;
				})))
				.then(
					CommandManager.literal("store")
						.then(addStoreArguments(literalCommandNode, CommandManager.literal("result"), true))
						.then(addStoreArguments(literalCommandNode, CommandManager.literal("success"), false))
				)
				.then(
					CommandManager.literal("positioned")
						.then(
							CommandManager.argument("pos", Vec3ArgumentType.create())
								.redirect(literalCommandNode, commandContext -> commandContext.getSource().withPosition(Vec3ArgumentType.getVec3(commandContext, "pos")))
						)
						.then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, commandContext -> {
							List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

							for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
								list.add(commandContext.getSource().withPosition(entity.getPosVector()));
							}

							return list;
						})))
				)
				.then(
					CommandManager.literal("rotated")
						.then(
							CommandManager.argument("rot", RotationArgumentType.create())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource()
											.withRotation(RotationArgumentType.getRotation(commandContext, "rot").toAbsoluteRotation(commandContext.getSource()))
								)
						)
						.then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, commandContext -> {
							List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

							for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
								list.add(commandContext.getSource().withRotation(entity.getRotationClient()));
							}

							return list;
						})))
				)
				.then(
					CommandManager.literal("facing")
						.then(
							CommandManager.literal("entity")
								.then(
									CommandManager.argument("targets", EntityArgumentType.entities())
										.then(CommandManager.argument("anchor", EntityAnchorArgumentType.create()).fork(literalCommandNode, commandContext -> {
											List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();
											EntityAnchorArgumentType.EntityAnchor entityAnchor = EntityAnchorArgumentType.getEntityAnchor(commandContext, "anchor");

											for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
												list.add(commandContext.getSource().withLookingAt(entity, entityAnchor));
											}

											return list;
										}))
								)
						)
						.then(
							CommandManager.argument("pos", Vec3ArgumentType.create())
								.redirect(literalCommandNode, commandContext -> commandContext.getSource().withLookingAt(Vec3ArgumentType.getVec3(commandContext, "pos")))
						)
				)
				.then(
					CommandManager.literal("align")
						.then(
							CommandManager.argument("axes", SwizzleArgumentType.create())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource()
											.withPosition(commandContext.getSource().getPosition().floorAlongAxes(SwizzleArgumentType.getSwizzle(commandContext, "axes")))
								)
						)
				)
				.then(
					CommandManager.literal("anchored")
						.then(
							CommandManager.argument("anchor", EntityAnchorArgumentType.create())
								.redirect(
									literalCommandNode, commandContext -> commandContext.getSource().withEntityAnchor(EntityAnchorArgumentType.getEntityAnchor(commandContext, "anchor"))
								)
						)
				)
				.then(
					CommandManager.literal("in")
						.then(
							CommandManager.argument("dimension", DimensionArgumentType.create())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource()
											.withWorld(commandContext.getSource().getMinecraftServer().getWorld(DimensionArgumentType.getDimensionArgument(commandContext, "dimension")))
								)
						)
				)
		);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addStoreArguments(
		LiteralCommandNode<ServerCommandSource> literalCommandNode, LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, boolean bl
	) {
		literalArgumentBuilder.then(
			CommandManager.literal("score")
				.then(
					CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders())
						.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
						.then(
							CommandManager.argument("objective", ObjectiveArgumentType.create())
								.redirect(
									literalCommandNode,
									commandContext -> executeStoreScore(
											commandContext.getSource(),
											ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"),
											ObjectiveArgumentType.getObjective(commandContext, "objective"),
											bl
										)
								)
						)
				)
		);
		literalArgumentBuilder.then(
			CommandManager.literal("bossbar")
				.then(
					CommandManager.argument("id", IdentifierArgumentType.create())
						.suggests(BossBarCommand.suggestionProvider)
						.then(
							CommandManager.literal("value")
								.redirect(literalCommandNode, commandContext -> executeStoreBossbar(commandContext.getSource(), BossBarCommand.createBossBar(commandContext), true, bl))
						)
						.then(
							CommandManager.literal("max")
								.redirect(
									literalCommandNode, commandContext -> executeStoreBossbar(commandContext.getSource(), BossBarCommand.createBossBar(commandContext), false, bl)
								)
						)
				)
		);

		for (DataCommand.ObjectType objectType : DataCommand.TARGET_OBJECT_TYPES) {
			objectType.addArgumentsToBuilder(
				literalArgumentBuilder,
				argumentBuilder -> argumentBuilder.then(
						CommandManager.argument("path", NbtPathArgumentType.create())
							.then(
								CommandManager.literal("int")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> executeStoreData(
														commandContext.getSource(),
														objectType.getObject(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new IntTag((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
														bl
													)
											)
									)
							)
							.then(
								CommandManager.literal("float")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> executeStoreData(
														commandContext.getSource(),
														objectType.getObject(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new FloatTag((float)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
														bl
													)
											)
									)
							)
							.then(
								CommandManager.literal("short")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> executeStoreData(
														commandContext.getSource(),
														objectType.getObject(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new ShortTag((short)((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale")))),
														bl
													)
											)
									)
							)
							.then(
								CommandManager.literal("long")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> executeStoreData(
														commandContext.getSource(),
														objectType.getObject(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new LongTag((long)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
														bl
													)
											)
									)
							)
							.then(
								CommandManager.literal("double")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> executeStoreData(
														commandContext.getSource(),
														objectType.getObject(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new DoubleTag((double)i * DoubleArgumentType.getDouble(commandContext, "scale")),
														bl
													)
											)
									)
							)
							.then(
								CommandManager.literal("byte")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> executeStoreData(
														commandContext.getSource(),
														objectType.getObject(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new ByteTag((byte)((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale")))),
														bl
													)
											)
									)
							)
					)
			);
		}

		return literalArgumentBuilder;
	}

	private static ServerCommandSource executeStoreScore(
		ServerCommandSource serverCommandSource, Collection<String> collection, ScoreboardObjective scoreboardObjective, boolean bl
	) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		return serverCommandSource.mergeConsumers((commandContext, bl2, i) -> {
			for (String string : collection) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
				int j = bl ? i : (bl2 ? 1 : 0);
				scoreboardPlayerScore.setScore(j);
			}
		}, field_13634);
	}

	private static ServerCommandSource executeStoreBossbar(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, boolean bl, boolean bl2) {
		return serverCommandSource.mergeConsumers((commandContext, bl3, i) -> {
			int j = bl2 ? i : (bl3 ? 1 : 0);
			if (bl) {
				commandBossBar.setValue(j);
			} else {
				commandBossBar.setMaxValue(j);
			}
		}, field_13634);
	}

	private static ServerCommandSource executeStoreData(
		ServerCommandSource serverCommandSource, DataCommandObject dataCommandObject, NbtPathArgumentType.NbtPath nbtPath, IntFunction<Tag> intFunction, boolean bl
	) {
		return serverCommandSource.mergeConsumers((commandContext, bl2, i) -> {
			try {
				CompoundTag compoundTag = dataCommandObject.getTag();
				int j = bl ? i : (bl2 ? 1 : 0);
				nbtPath.method_9368(compoundTag, () -> (Tag)intFunction.apply(j));
				dataCommandObject.setTag(compoundTag);
			} catch (CommandSyntaxException var9) {
			}
		}, field_13634);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addConditionArguments(
		CommandNode<ServerCommandSource> commandNode, LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, boolean bl
	) {
		literalArgumentBuilder.then(
				CommandManager.literal("block")
					.then(
						CommandManager.argument("pos", BlockPosArgumentType.create())
							.then(
								executeCondition(
									commandNode,
									CommandManager.argument("block", BlockPredicateArgumentType.create()),
									bl,
									commandContext -> BlockPredicateArgumentType.getBlockPredicate(commandContext, "block")
											.test(new CachedBlockPosition(commandContext.getSource().getWorld(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), true))
								)
							)
					)
			)
			.then(
				CommandManager.literal("score")
					.then(
						CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder())
							.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
							.then(
								CommandManager.argument("targetObjective", ObjectiveArgumentType.create())
									.then(
										CommandManager.literal("=")
											.then(
												CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														executeCondition(
															commandNode,
															CommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> testObjectiveCondition(commandContext, Integer::equals)
														)
													)
											)
									)
									.then(
										CommandManager.literal("<")
											.then(
												CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														executeCondition(
															commandNode,
															CommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> testObjectiveCondition(commandContext, (integer, integer2) -> integer < integer2)
														)
													)
											)
									)
									.then(
										CommandManager.literal("<=")
											.then(
												CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														executeCondition(
															commandNode,
															CommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> testObjectiveCondition(commandContext, (integer, integer2) -> integer <= integer2)
														)
													)
											)
									)
									.then(
										CommandManager.literal(">")
											.then(
												CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														executeCondition(
															commandNode,
															CommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> testObjectiveCondition(commandContext, (integer, integer2) -> integer > integer2)
														)
													)
											)
									)
									.then(
										CommandManager.literal(">=")
											.then(
												CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														executeCondition(
															commandNode,
															CommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> testObjectiveCondition(commandContext, (integer, integer2) -> integer >= integer2)
														)
													)
											)
									)
									.then(
										CommandManager.literal("matches")
											.then(
												executeCondition(
													commandNode,
													CommandManager.argument("range", NumberRangeArgumentType.create()),
													bl,
													commandContext -> testObjectiveCondition(commandContext, NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument(commandContext, "range"))
												)
											)
									)
							)
					)
			)
			.then(
				CommandManager.literal("blocks")
					.then(
						CommandManager.argument("start", BlockPosArgumentType.create())
							.then(
								CommandManager.argument("end", BlockPosArgumentType.create())
									.then(
										CommandManager.argument("destination", BlockPosArgumentType.create())
											.then(addBlockConditionExecutes(commandNode, CommandManager.literal("all"), bl, false))
											.then(addBlockConditionExecutes(commandNode, CommandManager.literal("masked"), bl, true))
									)
							)
					)
			)
			.then(
				CommandManager.literal("entity")
					.then(
						CommandManager.argument("entities", EntityArgumentType.entities())
							.fork(
								commandNode,
								commandContext -> transformSourceForCondition(commandContext, bl, !EntityArgumentType.getOptionalEntities(commandContext, "entities").isEmpty())
							)
							.executes(executeCondition(bl, commandContext -> EntityArgumentType.getOptionalEntities(commandContext, "entities").size()))
					)
			);

		for (DataCommand.ObjectType objectType : DataCommand.SOURCE_OBJECT_TYPES) {
			literalArgumentBuilder.then(
				objectType.addArgumentsToBuilder(
					CommandManager.literal("data"),
					argumentBuilder -> argumentBuilder.then(
							CommandManager.argument("path", NbtPathArgumentType.create())
								.fork(
									commandNode,
									commandContext -> transformSourceForCondition(
											commandContext, bl, countPathMatches(objectType.getObject(commandContext), NbtPathArgumentType.method_9358(commandContext, "path")) > 0
										)
								)
								.executes(
									executeCondition(bl, commandContext -> countPathMatches(objectType.getObject(commandContext), NbtPathArgumentType.method_9358(commandContext, "path")))
								)
						)
				)
			);
		}

		return literalArgumentBuilder;
	}

	private static Command<ServerCommandSource> executeCondition(boolean bl, ExecuteCommand.Condition condition) {
		return bl ? commandContext -> {
			int i = condition.test(commandContext);
			if (i > 0) {
				commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass_count", i), false);
				return i;
			} else {
				throw CONDITIONAL_FAIL_EXCEPTION.create();
			}
		} : commandContext -> {
			int i = condition.test(commandContext);
			if (i == 0) {
				commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(i);
			}
		};
	}

	private static int countPathMatches(DataCommandObject dataCommandObject, NbtPathArgumentType.NbtPath nbtPath) throws CommandSyntaxException {
		return nbtPath.count(dataCommandObject.getTag());
	}

	private static boolean testObjectiveCondition(CommandContext<ServerCommandSource> commandContext, BiPredicate<Integer, Integer> biPredicate) throws CommandSyntaxException {
		String string = ScoreHolderArgumentType.getScoreHolder(commandContext, "target");
		ScoreboardObjective scoreboardObjective = ObjectiveArgumentType.getObjective(commandContext, "targetObjective");
		String string2 = ScoreHolderArgumentType.getScoreHolder(commandContext, "source");
		ScoreboardObjective scoreboardObjective2 = ObjectiveArgumentType.getObjective(commandContext, "sourceObjective");
		Scoreboard scoreboard = commandContext.getSource().getMinecraftServer().getScoreboard();
		if (scoreboard.playerHasObjective(string, scoreboardObjective) && scoreboard.playerHasObjective(string2, scoreboardObjective2)) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
			return biPredicate.test(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore());
		} else {
			return false;
		}
	}

	private static boolean testObjectiveCondition(CommandContext<ServerCommandSource> commandContext, NumberRange.IntRange intRange) throws CommandSyntaxException {
		String string = ScoreHolderArgumentType.getScoreHolder(commandContext, "target");
		ScoreboardObjective scoreboardObjective = ObjectiveArgumentType.getObjective(commandContext, "targetObjective");
		Scoreboard scoreboard = commandContext.getSource().getMinecraftServer().getScoreboard();
		return !scoreboard.playerHasObjective(string, scoreboardObjective) ? false : intRange.test(scoreboard.getPlayerScore(string, scoreboardObjective).getScore());
	}

	private static Collection<ServerCommandSource> transformSourceForCondition(CommandContext<ServerCommandSource> commandContext, boolean bl, boolean bl2) {
		return (Collection<ServerCommandSource>)(bl2 == bl ? Collections.singleton(commandContext.getSource()) : Collections.emptyList());
	}

	private static ArgumentBuilder<ServerCommandSource, ?> executeCondition(
		CommandNode<ServerCommandSource> commandNode, ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, boolean bl, ExecuteCommand.class_3052 arg
	) {
		return argumentBuilder.fork(commandNode, commandContext -> transformSourceForCondition(commandContext, bl, arg.test(commandContext)))
			.executes(commandContext -> {
				if (bl == arg.test(commandContext)) {
					commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass"), false);
					return 1;
				} else {
					throw CONDITIONAL_FAIL_EXCEPTION.create();
				}
			});
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addBlockConditionExecutes(
		CommandNode<ServerCommandSource> commandNode, ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, boolean bl, boolean bl2
	) {
		return argumentBuilder.fork(
				commandNode, commandContext -> transformSourceForCondition(commandContext, bl, testBlockCondition(commandContext, bl2).isPresent())
			)
			.executes(bl ? commandContext -> executePositiveBlockCondition(commandContext, bl2) : commandContext -> executeNegativeBlockCondition(commandContext, bl2));
	}

	private static int executePositiveBlockCondition(CommandContext<ServerCommandSource> commandContext, boolean bl) throws CommandSyntaxException {
		OptionalInt optionalInt = testBlockCondition(commandContext, bl);
		if (optionalInt.isPresent()) {
			commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass_count", optionalInt.getAsInt()), false);
			return optionalInt.getAsInt();
		} else {
			throw CONDITIONAL_FAIL_EXCEPTION.create();
		}
	}

	private static int executeNegativeBlockCondition(CommandContext<ServerCommandSource> commandContext, boolean bl) throws CommandSyntaxException {
		OptionalInt optionalInt = testBlockCondition(commandContext, bl);
		if (optionalInt.isPresent()) {
			throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(optionalInt.getAsInt());
		} else {
			commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass"), false);
			return 1;
		}
	}

	private static OptionalInt testBlockCondition(CommandContext<ServerCommandSource> commandContext, boolean bl) throws CommandSyntaxException {
		return testBlockCondition(
			commandContext.getSource().getWorld(),
			BlockPosArgumentType.getLoadedBlockPos(commandContext, "start"),
			BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
			BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
			bl
		);
	}

	private static OptionalInt testBlockCondition(ServerWorld serverWorld, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, boolean bl) throws CommandSyntaxException {
		MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(blockPos, blockPos2);
		MutableIntBoundingBox mutableIntBoundingBox2 = new MutableIntBoundingBox(blockPos3, blockPos3.add(mutableIntBoundingBox.getSize()));
		BlockPos blockPos4 = new BlockPos(
			mutableIntBoundingBox2.minX - mutableIntBoundingBox.minX,
			mutableIntBoundingBox2.minY - mutableIntBoundingBox.minY,
			mutableIntBoundingBox2.minZ - mutableIntBoundingBox.minZ
		);
		int i = mutableIntBoundingBox.getBlockCountX() * mutableIntBoundingBox.getBlockCountY() * mutableIntBoundingBox.getBlockCountZ();
		if (i > 32768) {
			throw BLOCKS_TOOBIG_EXCEPTION.create(32768, i);
		} else {
			int j = 0;

			for (int k = mutableIntBoundingBox.minZ; k <= mutableIntBoundingBox.maxZ; k++) {
				for (int l = mutableIntBoundingBox.minY; l <= mutableIntBoundingBox.maxY; l++) {
					for (int m = mutableIntBoundingBox.minX; m <= mutableIntBoundingBox.maxX; m++) {
						BlockPos blockPos5 = new BlockPos(m, l, k);
						BlockPos blockPos6 = blockPos5.add(blockPos4);
						BlockState blockState = serverWorld.getBlockState(blockPos5);
						if (!bl || blockState.getBlock() != Blocks.field_10124) {
							if (blockState != serverWorld.getBlockState(blockPos6)) {
								return OptionalInt.empty();
							}

							BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos5);
							BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos6);
							if (blockEntity != null) {
								if (blockEntity2 == null) {
									return OptionalInt.empty();
								}

								CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
								compoundTag.remove("x");
								compoundTag.remove("y");
								compoundTag.remove("z");
								CompoundTag compoundTag2 = blockEntity2.toTag(new CompoundTag());
								compoundTag2.remove("x");
								compoundTag2.remove("y");
								compoundTag2.remove("z");
								if (!compoundTag.equals(compoundTag2)) {
									return OptionalInt.empty();
								}
							}

							j++;
						}
					}
				}
			}

			return OptionalInt.of(j);
		}
	}

	@FunctionalInterface
	interface Condition {
		int test(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface class_3052 {
		boolean test(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;
	}
}
