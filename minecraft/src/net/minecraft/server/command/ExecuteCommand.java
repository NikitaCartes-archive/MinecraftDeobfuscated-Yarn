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
import com.mojang.brigadier.suggestion.SuggestionProvider;
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
import net.minecraft.command.CommandSource;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.NumberRangeArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.command.argument.SwizzleArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.predicate.NumberRange;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class ExecuteCommand {
	private static final int MAX_BLOCKS = 32768;
	private static final Dynamic2CommandExceptionType BLOCKS_TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(maxCount, count) -> new TranslatableText("commands.execute.blocks.toobig", maxCount, count)
	);
	private static final SimpleCommandExceptionType CONDITIONAL_FAIL_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.execute.conditional.fail")
	);
	private static final DynamicCommandExceptionType CONDITIONAL_FAIL_COUNT_EXCEPTION = new DynamicCommandExceptionType(
		count -> new TranslatableText("commands.execute.conditional.fail_count", count)
	);
	private static final BinaryOperator<ResultConsumer<ServerCommandSource>> BINARY_RESULT_CONSUMER = (resultConsumer, resultConsumer2) -> (context, success, result) -> {
			resultConsumer.onCommandComplete(context, success, result);
			resultConsumer2.onCommandComplete(context, success, result);
		};
	private static final SuggestionProvider<ServerCommandSource> LOOT_CONDITIONS = (context, builder) -> {
		LootConditionManager lootConditionManager = context.getSource().getMinecraftServer().getPredicateManager();
		return CommandSource.suggestIdentifiers(lootConditionManager.getIds(), builder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
			CommandManager.literal("execute").requires(source -> source.hasPermissionLevel(2))
		);
		dispatcher.register(
			CommandManager.literal("execute")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.literal("run").redirect(dispatcher.getRoot()))
				.then(addConditionArguments(literalCommandNode, CommandManager.literal("if"), true))
				.then(addConditionArguments(literalCommandNode, CommandManager.literal("unless"), false))
				.then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, context -> {
					List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

					for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
						list.add(context.getSource().withEntity(entity));
					}

					return list;
				})))
				.then(CommandManager.literal("at").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, context -> {
					List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

					for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
						list.add(context.getSource().withWorld((ServerWorld)entity.world).withPosition(entity.getPos()).withRotation(entity.getRotationClient()));
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
							CommandManager.argument("pos", Vec3ArgumentType.vec3())
								.redirect(
									literalCommandNode,
									context -> context.getSource().withPosition(Vec3ArgumentType.getVec3(context, "pos")).withEntityAnchor(EntityAnchorArgumentType.EntityAnchor.FEET)
								)
						)
						.then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, context -> {
							List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

							for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
								list.add(context.getSource().withPosition(entity.getPos()));
							}

							return list;
						})))
				)
				.then(
					CommandManager.literal("rotated")
						.then(
							CommandManager.argument("rot", RotationArgumentType.rotation())
								.redirect(
									literalCommandNode,
									context -> context.getSource().withRotation(RotationArgumentType.getRotation(context, "rot").toAbsoluteRotation(context.getSource()))
								)
						)
						.then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, context -> {
							List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

							for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
								list.add(context.getSource().withRotation(entity.getRotationClient()));
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
										.then(CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).fork(literalCommandNode, context -> {
											List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();
											EntityAnchorArgumentType.EntityAnchor entityAnchor = EntityAnchorArgumentType.getEntityAnchor(context, "anchor");

											for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
												list.add(context.getSource().withLookingAt(entity, entityAnchor));
											}

											return list;
										}))
								)
						)
						.then(
							CommandManager.argument("pos", Vec3ArgumentType.vec3())
								.redirect(literalCommandNode, context -> context.getSource().withLookingAt(Vec3ArgumentType.getVec3(context, "pos")))
						)
				)
				.then(
					CommandManager.literal("align")
						.then(
							CommandManager.argument("axes", SwizzleArgumentType.swizzle())
								.redirect(
									literalCommandNode,
									context -> context.getSource().withPosition(context.getSource().getPosition().floorAlongAxes(SwizzleArgumentType.getSwizzle(context, "axes")))
								)
						)
				)
				.then(
					CommandManager.literal("anchored")
						.then(
							CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor())
								.redirect(literalCommandNode, context -> context.getSource().withEntityAnchor(EntityAnchorArgumentType.getEntityAnchor(context, "anchor")))
						)
				)
				.then(
					CommandManager.literal("in")
						.then(
							CommandManager.argument("dimension", DimensionArgumentType.dimension())
								.redirect(literalCommandNode, context -> context.getSource().withWorld(DimensionArgumentType.getDimensionArgument(context, "dimension")))
						)
				)
		);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addStoreArguments(
		LiteralCommandNode<ServerCommandSource> node, LiteralArgumentBuilder<ServerCommandSource> builder, boolean requestResult
	) {
		builder.then(
			CommandManager.literal("score")
				.then(
					CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders())
						.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
						.then(
							CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective())
								.redirect(
									node,
									context -> executeStoreScore(
											context.getSource(),
											ScoreHolderArgumentType.getScoreboardScoreHolders(context, "targets"),
											ScoreboardObjectiveArgumentType.getObjective(context, "objective"),
											requestResult
										)
								)
						)
				)
		);
		builder.then(
			CommandManager.literal("bossbar")
				.then(
					CommandManager.argument("id", IdentifierArgumentType.identifier())
						.suggests(BossBarCommand.SUGGESTION_PROVIDER)
						.then(
							CommandManager.literal("value")
								.redirect(node, context -> executeStoreBossbar(context.getSource(), BossBarCommand.getBossBar(context), true, requestResult))
						)
						.then(
							CommandManager.literal("max")
								.redirect(node, context -> executeStoreBossbar(context.getSource(), BossBarCommand.getBossBar(context), false, requestResult))
						)
				)
		);

		for (DataCommand.ObjectType objectType : DataCommand.TARGET_OBJECT_TYPES) {
			objectType.addArgumentsToBuilder(
				builder,
				builderx -> builderx.then(
						CommandManager.argument("path", NbtPathArgumentType.nbtPath())
							.then(
								CommandManager.literal("int")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												node,
												context -> executeStoreData(
														context.getSource(),
														objectType.getObject(context),
														NbtPathArgumentType.getNbtPath(context, "path"),
														result -> NbtInt.of((int)((double)result * DoubleArgumentType.getDouble(context, "scale"))),
														requestResult
													)
											)
									)
							)
							.then(
								CommandManager.literal("float")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												node,
												context -> executeStoreData(
														context.getSource(),
														objectType.getObject(context),
														NbtPathArgumentType.getNbtPath(context, "path"),
														result -> NbtFloat.of((float)((double)result * DoubleArgumentType.getDouble(context, "scale"))),
														requestResult
													)
											)
									)
							)
							.then(
								CommandManager.literal("short")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												node,
												context -> executeStoreData(
														context.getSource(),
														objectType.getObject(context),
														NbtPathArgumentType.getNbtPath(context, "path"),
														result -> NbtShort.of((short)((int)((double)result * DoubleArgumentType.getDouble(context, "scale")))),
														requestResult
													)
											)
									)
							)
							.then(
								CommandManager.literal("long")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												node,
												context -> executeStoreData(
														context.getSource(),
														objectType.getObject(context),
														NbtPathArgumentType.getNbtPath(context, "path"),
														result -> NbtLong.of((long)((double)result * DoubleArgumentType.getDouble(context, "scale"))),
														requestResult
													)
											)
									)
							)
							.then(
								CommandManager.literal("double")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												node,
												context -> executeStoreData(
														context.getSource(),
														objectType.getObject(context),
														NbtPathArgumentType.getNbtPath(context, "path"),
														result -> NbtDouble.of((double)result * DoubleArgumentType.getDouble(context, "scale")),
														requestResult
													)
											)
									)
							)
							.then(
								CommandManager.literal("byte")
									.then(
										CommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												node,
												context -> executeStoreData(
														context.getSource(),
														objectType.getObject(context),
														NbtPathArgumentType.getNbtPath(context, "path"),
														result -> NbtByte.of((byte)((int)((double)result * DoubleArgumentType.getDouble(context, "scale")))),
														requestResult
													)
											)
									)
							)
					)
			);
		}

		return builder;
	}

	private static ServerCommandSource executeStoreScore(
		ServerCommandSource source, Collection<String> targets, ScoreboardObjective objective, boolean requestResult
	) {
		Scoreboard scoreboard = source.getMinecraftServer().getScoreboard();
		return source.mergeConsumers((context, success, result) -> {
			for (String string : targets) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, objective);
				int i = requestResult ? result : (success ? 1 : 0);
				scoreboardPlayerScore.setScore(i);
			}
		}, BINARY_RESULT_CONSUMER);
	}

	private static ServerCommandSource executeStoreBossbar(ServerCommandSource source, CommandBossBar bossBar, boolean storeInValue, boolean requestResult) {
		return source.mergeConsumers((context, success, result) -> {
			int i = requestResult ? result : (success ? 1 : 0);
			if (storeInValue) {
				bossBar.setValue(i);
			} else {
				bossBar.setMaxValue(i);
			}
		}, BINARY_RESULT_CONSUMER);
	}

	private static ServerCommandSource executeStoreData(
		ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, IntFunction<NbtElement> nbtSetter, boolean requestResult
	) {
		return source.mergeConsumers((context, success, result) -> {
			try {
				NbtCompound nbtCompound = object.getNbt();
				int i = requestResult ? result : (success ? 1 : 0);
				path.put(nbtCompound, () -> (NbtElement)nbtSetter.apply(i));
				object.setNbt(nbtCompound);
			} catch (CommandSyntaxException var9) {
			}
		}, BINARY_RESULT_CONSUMER);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addConditionArguments(
		CommandNode<ServerCommandSource> root, LiteralArgumentBuilder<ServerCommandSource> argumentBuilder, boolean positive
	) {
		argumentBuilder.then(
				CommandManager.literal("block")
					.then(
						CommandManager.argument("pos", BlockPosArgumentType.blockPos())
							.then(
								addConditionLogic(
									root,
									CommandManager.argument("block", BlockPredicateArgumentType.blockPredicate()),
									positive,
									context -> BlockPredicateArgumentType.getBlockPredicate(context, "block")
											.test(new CachedBlockPosition(context.getSource().getWorld(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), true))
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
								CommandManager.argument("targetObjective", ScoreboardObjectiveArgumentType.scoreboardObjective())
									.then(
										CommandManager.literal("=")
											.then(
												CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														addConditionLogic(
															root,
															CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()),
															positive,
															context -> testScoreCondition(context, Integer::equals)
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
														addConditionLogic(
															root,
															CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()),
															positive,
															context -> testScoreCondition(context, (a, b) -> a < b)
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
														addConditionLogic(
															root,
															CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()),
															positive,
															context -> testScoreCondition(context, (a, b) -> a <= b)
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
														addConditionLogic(
															root,
															CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()),
															positive,
															context -> testScoreCondition(context, (a, b) -> a > b)
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
														addConditionLogic(
															root,
															CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()),
															positive,
															context -> testScoreCondition(context, (a, b) -> a >= b)
														)
													)
											)
									)
									.then(
										CommandManager.literal("matches")
											.then(
												addConditionLogic(
													root,
													CommandManager.argument("range", NumberRangeArgumentType.intRange()),
													positive,
													context -> testScoreMatch(context, NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument(context, "range"))
												)
											)
									)
							)
					)
			)
			.then(
				CommandManager.literal("blocks")
					.then(
						CommandManager.argument("start", BlockPosArgumentType.blockPos())
							.then(
								CommandManager.argument("end", BlockPosArgumentType.blockPos())
									.then(
										CommandManager.argument("destination", BlockPosArgumentType.blockPos())
											.then(addBlocksConditionLogic(root, CommandManager.literal("all"), positive, false))
											.then(addBlocksConditionLogic(root, CommandManager.literal("masked"), positive, true))
									)
							)
					)
			)
			.then(
				CommandManager.literal("entity")
					.then(
						CommandManager.argument("entities", EntityArgumentType.entities())
							.fork(root, context -> getSourceOrEmptyForConditionFork(context, positive, !EntityArgumentType.getOptionalEntities(context, "entities").isEmpty()))
							.executes(getExistsConditionExecute(positive, context -> EntityArgumentType.getOptionalEntities(context, "entities").size()))
					)
			)
			.then(
				CommandManager.literal("predicate")
					.then(
						addConditionLogic(
							root,
							CommandManager.argument("predicate", IdentifierArgumentType.identifier()).suggests(LOOT_CONDITIONS),
							positive,
							context -> testLootCondition(context.getSource(), IdentifierArgumentType.getPredicateArgument(context, "predicate"))
						)
					)
			);

		for (DataCommand.ObjectType objectType : DataCommand.SOURCE_OBJECT_TYPES) {
			argumentBuilder.then(
				objectType.addArgumentsToBuilder(
					CommandManager.literal("data"),
					builder -> builder.then(
							CommandManager.argument("path", NbtPathArgumentType.nbtPath())
								.fork(
									root,
									commandContext -> getSourceOrEmptyForConditionFork(
											commandContext, positive, countPathMatches(objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path")) > 0
										)
								)
								.executes(
									getExistsConditionExecute(positive, context -> countPathMatches(objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path")))
								)
						)
				)
			);
		}

		return argumentBuilder;
	}

	private static Command<ServerCommandSource> getExistsConditionExecute(boolean positive, ExecuteCommand.ExistsCondition condition) {
		return positive ? context -> {
			int i = condition.test(context);
			if (i > 0) {
				context.getSource().sendFeedback(new TranslatableText("commands.execute.conditional.pass_count", i), false);
				return i;
			} else {
				throw CONDITIONAL_FAIL_EXCEPTION.create();
			}
		} : context -> {
			int i = condition.test(context);
			if (i == 0) {
				context.getSource().sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(i);
			}
		};
	}

	private static int countPathMatches(DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
		return path.count(object.getNbt());
	}

	private static boolean testScoreCondition(CommandContext<ServerCommandSource> context, BiPredicate<Integer, Integer> condition) throws CommandSyntaxException {
		String string = ScoreHolderArgumentType.getScoreHolder(context, "target");
		ScoreboardObjective scoreboardObjective = ScoreboardObjectiveArgumentType.getObjective(context, "targetObjective");
		String string2 = ScoreHolderArgumentType.getScoreHolder(context, "source");
		ScoreboardObjective scoreboardObjective2 = ScoreboardObjectiveArgumentType.getObjective(context, "sourceObjective");
		Scoreboard scoreboard = context.getSource().getMinecraftServer().getScoreboard();
		if (scoreboard.playerHasObjective(string, scoreboardObjective) && scoreboard.playerHasObjective(string2, scoreboardObjective2)) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
			return condition.test(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore());
		} else {
			return false;
		}
	}

	private static boolean testScoreMatch(CommandContext<ServerCommandSource> context, NumberRange.IntRange range) throws CommandSyntaxException {
		String string = ScoreHolderArgumentType.getScoreHolder(context, "target");
		ScoreboardObjective scoreboardObjective = ScoreboardObjectiveArgumentType.getObjective(context, "targetObjective");
		Scoreboard scoreboard = context.getSource().getMinecraftServer().getScoreboard();
		return !scoreboard.playerHasObjective(string, scoreboardObjective) ? false : range.test(scoreboard.getPlayerScore(string, scoreboardObjective).getScore());
	}

	private static boolean testLootCondition(ServerCommandSource source, LootCondition condition) {
		ServerWorld serverWorld = source.getWorld();
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.parameter(LootContextParameters.ORIGIN, source.getPosition())
			.optionalParameter(LootContextParameters.THIS_ENTITY, source.getEntity());
		return condition.test(builder.build(LootContextTypes.COMMAND));
	}

	private static Collection<ServerCommandSource> getSourceOrEmptyForConditionFork(CommandContext<ServerCommandSource> context, boolean positive, boolean value) {
		return (Collection<ServerCommandSource>)(value == positive ? Collections.singleton(context.getSource()) : Collections.emptyList());
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addConditionLogic(
		CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, ExecuteCommand.Condition condition
	) {
		return builder.fork(root, context -> getSourceOrEmptyForConditionFork(context, positive, condition.test(context))).executes(context -> {
			if (positive == condition.test(context)) {
				context.getSource().sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw CONDITIONAL_FAIL_EXCEPTION.create();
			}
		});
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addBlocksConditionLogic(
		CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, boolean masked
	) {
		return builder.fork(root, context -> getSourceOrEmptyForConditionFork(context, positive, testBlocksCondition(context, masked).isPresent()))
			.executes(positive ? context -> executePositiveBlockCondition(context, masked) : context -> executeNegativeBlockCondition(context, masked));
	}

	private static int executePositiveBlockCondition(CommandContext<ServerCommandSource> context, boolean masked) throws CommandSyntaxException {
		OptionalInt optionalInt = testBlocksCondition(context, masked);
		if (optionalInt.isPresent()) {
			context.getSource().sendFeedback(new TranslatableText("commands.execute.conditional.pass_count", optionalInt.getAsInt()), false);
			return optionalInt.getAsInt();
		} else {
			throw CONDITIONAL_FAIL_EXCEPTION.create();
		}
	}

	private static int executeNegativeBlockCondition(CommandContext<ServerCommandSource> context, boolean masked) throws CommandSyntaxException {
		OptionalInt optionalInt = testBlocksCondition(context, masked);
		if (optionalInt.isPresent()) {
			throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(optionalInt.getAsInt());
		} else {
			context.getSource().sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
			return 1;
		}
	}

	private static OptionalInt testBlocksCondition(CommandContext<ServerCommandSource> context, boolean masked) throws CommandSyntaxException {
		return testBlocksCondition(
			context.getSource().getWorld(),
			BlockPosArgumentType.getLoadedBlockPos(context, "start"),
			BlockPosArgumentType.getLoadedBlockPos(context, "end"),
			BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
			masked
		);
	}

	private static OptionalInt testBlocksCondition(ServerWorld world, BlockPos start, BlockPos end, BlockPos destination, boolean masked) throws CommandSyntaxException {
		BlockBox blockBox = BlockBox.create(start, end);
		BlockBox blockBox2 = BlockBox.create(destination, destination.add(blockBox.getDimensions()));
		BlockPos blockPos = new BlockPos(blockBox2.getMinX() - blockBox.getMinX(), blockBox2.getMinY() - blockBox.getMinY(), blockBox2.getMinZ() - blockBox.getMinZ());
		int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
		if (i > 32768) {
			throw BLOCKS_TOOBIG_EXCEPTION.create(32768, i);
		} else {
			int j = 0;

			for (int k = blockBox.getMinZ(); k <= blockBox.getMaxZ(); k++) {
				for (int l = blockBox.getMinY(); l <= blockBox.getMaxY(); l++) {
					for (int m = blockBox.getMinX(); m <= blockBox.getMaxX(); m++) {
						BlockPos blockPos2 = new BlockPos(m, l, k);
						BlockPos blockPos3 = blockPos2.add(blockPos);
						BlockState blockState = world.getBlockState(blockPos2);
						if (!masked || !blockState.isOf(Blocks.AIR)) {
							if (blockState != world.getBlockState(blockPos3)) {
								return OptionalInt.empty();
							}

							BlockEntity blockEntity = world.getBlockEntity(blockPos2);
							BlockEntity blockEntity2 = world.getBlockEntity(blockPos3);
							if (blockEntity != null) {
								if (blockEntity2 == null) {
									return OptionalInt.empty();
								}

								NbtCompound nbtCompound = blockEntity.writeNbt(new NbtCompound());
								nbtCompound.remove("x");
								nbtCompound.remove("y");
								nbtCompound.remove("z");
								NbtCompound nbtCompound2 = blockEntity2.writeNbt(new NbtCompound());
								nbtCompound2.remove("x");
								nbtCompound2.remove("y");
								nbtCompound2.remove("z");
								if (!nbtCompound.equals(nbtCompound2)) {
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
		boolean test(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface ExistsCondition {
		int test(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
	}
}
