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
import net.minecraft.class_3162;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.NumberRangeArgumentType;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.ResourceLocationArgumentType;
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
			ServerCommandManager.literal("execute").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
		);
		commandDispatcher.register(
			ServerCommandManager.literal("execute")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(ServerCommandManager.literal("run").redirect(commandDispatcher.getRoot()))
				.then(method_13298(literalCommandNode, ServerCommandManager.literal("if"), true))
				.then(method_13298(literalCommandNode, ServerCommandManager.literal("unless"), false))
				.then(
					ServerCommandManager.literal("as")
						.then(ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities()).fork(literalCommandNode, commandContext -> {
							List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

							for (Entity entity : EntityArgumentType.method_9307(commandContext, "targets")) {
								list.add(commandContext.getSource().withEntity(entity));
							}

							return list;
						}))
				)
				.then(
					ServerCommandManager.literal("at")
						.then(ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities()).fork(literalCommandNode, commandContext -> {
							List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

							for (Entity entity : EntityArgumentType.method_9307(commandContext, "targets")) {
								list.add(commandContext.getSource().withWorld((ServerWorld)entity.world).withPosition(entity.getPosVector()).withRotation(entity.getRotationClient()));
							}

							return list;
						}))
				)
				.then(
					ServerCommandManager.literal("store")
						.then(method_13289(literalCommandNode, ServerCommandManager.literal("result"), true))
						.then(method_13289(literalCommandNode, ServerCommandManager.literal("success"), false))
				)
				.then(
					ServerCommandManager.literal("positioned")
						.then(
							ServerCommandManager.argument("pos", Vec3ArgumentType.create())
								.redirect(literalCommandNode, commandContext -> commandContext.getSource().withPosition(Vec3ArgumentType.getVec3Argument(commandContext, "pos")))
						)
						.then(
							ServerCommandManager.literal("as")
								.then(ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities()).fork(literalCommandNode, commandContext -> {
									List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

									for (Entity entity : EntityArgumentType.method_9307(commandContext, "targets")) {
										list.add(commandContext.getSource().withPosition(entity.getPosVector()));
									}

									return list;
								}))
						)
				)
				.then(
					ServerCommandManager.literal("rotated")
						.then(
							ServerCommandManager.argument("rot", RotationArgumentType.create())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource()
											.withRotation(RotationArgumentType.getRotationArgument(commandContext, "rot").toAbsoluteRotation(commandContext.getSource()))
								)
						)
						.then(
							ServerCommandManager.literal("as")
								.then(ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities()).fork(literalCommandNode, commandContext -> {
									List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();

									for (Entity entity : EntityArgumentType.method_9307(commandContext, "targets")) {
										list.add(commandContext.getSource().withRotation(entity.getRotationClient()));
									}

									return list;
								}))
						)
				)
				.then(
					ServerCommandManager.literal("facing")
						.then(
							ServerCommandManager.literal("entity")
								.then(
									ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities())
										.then(ServerCommandManager.argument("anchor", EntityAnchorArgumentType.create()).fork(literalCommandNode, commandContext -> {
											List<ServerCommandSource> list = Lists.<ServerCommandSource>newArrayList();
											EntityAnchorArgumentType.EntityAnchor entityAnchor = EntityAnchorArgumentType.getAnchorArgument(commandContext, "anchor");

											for (Entity entity : EntityArgumentType.method_9307(commandContext, "targets")) {
												list.add(commandContext.getSource().withLookingAt(entity, entityAnchor));
											}

											return list;
										}))
								)
						)
						.then(
							ServerCommandManager.argument("pos", Vec3ArgumentType.create())
								.redirect(literalCommandNode, commandContext -> commandContext.getSource().withLookingAt(Vec3ArgumentType.getVec3Argument(commandContext, "pos")))
						)
				)
				.then(
					ServerCommandManager.literal("align")
						.then(
							ServerCommandManager.argument("axes", SwizzleArgumentType.create())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource()
											.withPosition(commandContext.getSource().getPosition().method_1032(SwizzleArgumentType.getSwizzleArgument(commandContext, "axes")))
								)
						)
				)
				.then(
					ServerCommandManager.literal("anchored")
						.then(
							ServerCommandManager.argument("anchor", EntityAnchorArgumentType.create())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource().withEntityAnchor(EntityAnchorArgumentType.getAnchorArgument(commandContext, "anchor"))
								)
						)
				)
				.then(
					ServerCommandManager.literal("in")
						.then(
							ServerCommandManager.argument("dimension", DimensionArgumentType.create())
								.redirect(
									literalCommandNode,
									commandContext -> commandContext.getSource()
											.withWorld(commandContext.getSource().getMinecraftServer().getWorld(DimensionArgumentType.getDimensionArgument(commandContext, "dimension")))
								)
						)
				)
		);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> method_13289(
		LiteralCommandNode<ServerCommandSource> literalCommandNode, LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, boolean bl
	) {
		literalArgumentBuilder.then(
			ServerCommandManager.literal("score")
				.then(
					ServerCommandManager.argument("targets", ScoreHolderArgumentType.method_9451())
						.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
						.then(
							ServerCommandManager.argument("objective", ObjectiveArgumentType.create())
								.redirect(
									literalCommandNode,
									commandContext -> method_13290(
											commandContext.getSource(),
											ScoreHolderArgumentType.method_9449(commandContext, "targets"),
											ObjectiveArgumentType.getObjectiveArgument(commandContext, "objective"),
											bl
										)
								)
						)
				)
		);
		literalArgumentBuilder.then(
			ServerCommandManager.literal("bossbar")
				.then(
					ServerCommandManager.argument("id", ResourceLocationArgumentType.create())
						.suggests(BossBarCommand.suggestionProvider)
						.then(
							ServerCommandManager.literal("value")
								.redirect(literalCommandNode, commandContext -> method_13297(commandContext.getSource(), BossBarCommand.createBossBar(commandContext), true, bl))
						)
						.then(
							ServerCommandManager.literal("max")
								.redirect(literalCommandNode, commandContext -> method_13297(commandContext.getSource(), BossBarCommand.createBossBar(commandContext), false, bl))
						)
				)
		);

		for (DataCommand.class_3167 lv : DataCommand.field_13798) {
			lv.method_13925(
				literalArgumentBuilder,
				argumentBuilder -> argumentBuilder.then(
						ServerCommandManager.argument("path", NbtPathArgumentType.create())
							.then(
								ServerCommandManager.literal("int")
									.then(
										ServerCommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new IntTag((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
														bl
													)
											)
									)
							)
							.then(
								ServerCommandManager.literal("float")
									.then(
										ServerCommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new FloatTag((float)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
														bl
													)
											)
									)
							)
							.then(
								ServerCommandManager.literal("short")
									.then(
										ServerCommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new ShortTag((short)((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale")))),
														bl
													)
											)
									)
							)
							.then(
								ServerCommandManager.literal("long")
									.then(
										ServerCommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new LongTag((long)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
														bl
													)
											)
									)
							)
							.then(
								ServerCommandManager.literal("double")
									.then(
										ServerCommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
														NbtPathArgumentType.method_9358(commandContext, "path"),
														i -> new DoubleTag((double)i * DoubleArgumentType.getDouble(commandContext, "scale")),
														bl
													)
											)
									)
							)
							.then(
								ServerCommandManager.literal("byte")
									.then(
										ServerCommandManager.argument("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_13265(
														commandContext.getSource(),
														lv.method_13924(commandContext),
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

	private static ServerCommandSource method_13290(
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

	private static ServerCommandSource method_13297(ServerCommandSource serverCommandSource, CommandBossBar commandBossBar, boolean bl, boolean bl2) {
		return serverCommandSource.mergeConsumers((commandContext, bl3, i) -> {
			int j = bl2 ? i : (bl3 ? 1 : 0);
			if (bl) {
				commandBossBar.setValue(j);
			} else {
				commandBossBar.setMaxValue(j);
			}
		}, field_13634);
	}

	private static ServerCommandSource method_13265(
		ServerCommandSource serverCommandSource, class_3162 arg, NbtPathArgumentType.class_2209 arg2, IntFunction<Tag> intFunction, boolean bl
	) {
		return serverCommandSource.mergeConsumers((commandContext, bl2, i) -> {
			try {
				CompoundTag compoundTag = arg.method_13881();
				int j = bl ? i : (bl2 ? 1 : 0);
				arg2.method_9368(compoundTag, () -> (Tag)intFunction.apply(j));
				arg.method_13880(compoundTag);
			} catch (CommandSyntaxException var9) {
			}
		}, field_13634);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> method_13298(
		CommandNode<ServerCommandSource> commandNode, LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, boolean bl
	) {
		literalArgumentBuilder.then(
				ServerCommandManager.literal("block")
					.then(
						ServerCommandManager.argument("pos", BlockPosArgumentType.create())
							.then(
								method_13310(
									commandNode,
									ServerCommandManager.argument("block", BlockPredicateArgumentType.create()),
									bl,
									commandContext -> BlockPredicateArgumentType.getPredicateArgument(commandContext, "block")
											.test(new CachedBlockPosition(commandContext.getSource().getWorld(), BlockPosArgumentType.getValidPosArgument(commandContext, "pos"), true))
								)
							)
					)
			)
			.then(
				ServerCommandManager.literal("score")
					.then(
						ServerCommandManager.argument("target", ScoreHolderArgumentType.method_9447())
							.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
							.then(
								ServerCommandManager.argument("targetObjective", ObjectiveArgumentType.create())
									.then(
										ServerCommandManager.literal("=")
											.then(
												ServerCommandManager.argument("source", ScoreHolderArgumentType.method_9447())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														method_13310(
															commandNode,
															ServerCommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> method_13263(commandContext, Integer::equals)
														)
													)
											)
									)
									.then(
										ServerCommandManager.literal("<")
											.then(
												ServerCommandManager.argument("source", ScoreHolderArgumentType.method_9447())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														method_13310(
															commandNode,
															ServerCommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> method_13263(commandContext, (integer, integer2) -> integer < integer2)
														)
													)
											)
									)
									.then(
										ServerCommandManager.literal("<=")
											.then(
												ServerCommandManager.argument("source", ScoreHolderArgumentType.method_9447())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														method_13310(
															commandNode,
															ServerCommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> method_13263(commandContext, (integer, integer2) -> integer <= integer2)
														)
													)
											)
									)
									.then(
										ServerCommandManager.literal(">")
											.then(
												ServerCommandManager.argument("source", ScoreHolderArgumentType.method_9447())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														method_13310(
															commandNode,
															ServerCommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> method_13263(commandContext, (integer, integer2) -> integer > integer2)
														)
													)
											)
									)
									.then(
										ServerCommandManager.literal(">=")
											.then(
												ServerCommandManager.argument("source", ScoreHolderArgumentType.method_9447())
													.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
													.then(
														method_13310(
															commandNode,
															ServerCommandManager.argument("sourceObjective", ObjectiveArgumentType.create()),
															bl,
															commandContext -> method_13263(commandContext, (integer, integer2) -> integer >= integer2)
														)
													)
											)
									)
									.then(
										ServerCommandManager.literal("matches")
											.then(
												method_13310(
													commandNode,
													ServerCommandManager.argument("range", NumberRangeArgumentType.method_9422()),
													bl,
													commandContext -> method_13313(commandContext, NumberRangeArgumentType.IntRangeArgumentType.method_9425(commandContext, "range"))
												)
											)
									)
							)
					)
			)
			.then(
				ServerCommandManager.literal("blocks")
					.then(
						ServerCommandManager.argument("start", BlockPosArgumentType.create())
							.then(
								ServerCommandManager.argument("end", BlockPosArgumentType.create())
									.then(
										ServerCommandManager.argument("destination", BlockPosArgumentType.create())
											.then(method_13320(commandNode, ServerCommandManager.literal("all"), bl, false))
											.then(method_13320(commandNode, ServerCommandManager.literal("masked"), bl, true))
									)
							)
					)
			)
			.then(
				ServerCommandManager.literal("entity")
					.then(
						ServerCommandManager.argument("entities", EntityArgumentType.multipleEntities())
							.fork(commandNode, commandContext -> method_13319(commandContext, bl, !EntityArgumentType.method_9307(commandContext, "entities").isEmpty()))
							.executes(method_13323(bl, commandContext -> EntityArgumentType.method_9307(commandContext, "entities").size()))
					)
			);

		for (DataCommand.class_3167 lv : DataCommand.field_13792) {
			literalArgumentBuilder.then(
				lv.method_13925(
					ServerCommandManager.literal("data"),
					argumentBuilder -> argumentBuilder.then(
							ServerCommandManager.argument("path", NbtPathArgumentType.create())
								.fork(
									commandNode,
									commandContext -> method_13319(
											commandContext, bl, method_13303(lv.method_13924(commandContext), NbtPathArgumentType.method_9358(commandContext, "path")) > 0
										)
								)
								.executes(method_13323(bl, commandContext -> method_13303(lv.method_13924(commandContext), NbtPathArgumentType.method_9358(commandContext, "path"))))
						)
				)
			);
		}

		return literalArgumentBuilder;
	}

	private static Command<ServerCommandSource> method_13323(boolean bl, ExecuteCommand.class_3051 arg) {
		return bl ? commandContext -> {
			int i = arg.test(commandContext);
			if (i > 0) {
				commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass_count", i), false);
				return i;
			} else {
				throw CONDITIONAL_FAIL_EXCEPTION.create();
			}
		} : commandContext -> {
			int i = arg.test(commandContext);
			if (i == 0) {
				commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(i);
			}
		};
	}

	private static int method_13303(class_3162 arg, NbtPathArgumentType.class_2209 arg2) throws CommandSyntaxException {
		return arg2.method_9374(arg.method_13881());
	}

	private static boolean method_13263(CommandContext<ServerCommandSource> commandContext, BiPredicate<Integer, Integer> biPredicate) throws CommandSyntaxException {
		String string = ScoreHolderArgumentType.getHolderArgument(commandContext, "target");
		ScoreboardObjective scoreboardObjective = ObjectiveArgumentType.getObjectiveArgument(commandContext, "targetObjective");
		String string2 = ScoreHolderArgumentType.getHolderArgument(commandContext, "source");
		ScoreboardObjective scoreboardObjective2 = ObjectiveArgumentType.getObjectiveArgument(commandContext, "sourceObjective");
		Scoreboard scoreboard = commandContext.getSource().getMinecraftServer().getScoreboard();
		if (scoreboard.playerHasObjective(string, scoreboardObjective) && scoreboard.playerHasObjective(string2, scoreboardObjective2)) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
			return biPredicate.test(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore());
		} else {
			return false;
		}
	}

	private static boolean method_13313(CommandContext<ServerCommandSource> commandContext, NumberRange.Integer integer) throws CommandSyntaxException {
		String string = ScoreHolderArgumentType.getHolderArgument(commandContext, "target");
		ScoreboardObjective scoreboardObjective = ObjectiveArgumentType.getObjectiveArgument(commandContext, "targetObjective");
		Scoreboard scoreboard = commandContext.getSource().getMinecraftServer().getScoreboard();
		return !scoreboard.playerHasObjective(string, scoreboardObjective) ? false : integer.test(scoreboard.getPlayerScore(string, scoreboardObjective).getScore());
	}

	private static Collection<ServerCommandSource> method_13319(CommandContext<ServerCommandSource> commandContext, boolean bl, boolean bl2) {
		return (Collection<ServerCommandSource>)(bl2 == bl ? Collections.singleton(commandContext.getSource()) : Collections.emptyList());
	}

	private static ArgumentBuilder<ServerCommandSource, ?> method_13310(
		CommandNode<ServerCommandSource> commandNode, ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, boolean bl, ExecuteCommand.class_3052 arg
	) {
		return argumentBuilder.fork(commandNode, commandContext -> method_13319(commandContext, bl, arg.test(commandContext))).executes(commandContext -> {
			if (bl == arg.test(commandContext)) {
				commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw CONDITIONAL_FAIL_EXCEPTION.create();
			}
		});
	}

	private static ArgumentBuilder<ServerCommandSource, ?> method_13320(
		CommandNode<ServerCommandSource> commandNode, ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, boolean bl, boolean bl2
	) {
		return argumentBuilder.fork(commandNode, commandContext -> method_13319(commandContext, bl, method_13272(commandContext, bl2).isPresent()))
			.executes(bl ? commandContext -> method_13306(commandContext, bl2) : commandContext -> method_13304(commandContext, bl2));
	}

	private static int method_13306(CommandContext<ServerCommandSource> commandContext, boolean bl) throws CommandSyntaxException {
		OptionalInt optionalInt = method_13272(commandContext, bl);
		if (optionalInt.isPresent()) {
			commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass_count", optionalInt.getAsInt()), false);
			return optionalInt.getAsInt();
		} else {
			throw CONDITIONAL_FAIL_EXCEPTION.create();
		}
	}

	private static int method_13304(CommandContext<ServerCommandSource> commandContext, boolean bl) throws CommandSyntaxException {
		OptionalInt optionalInt = method_13272(commandContext, bl);
		if (optionalInt.isPresent()) {
			throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(optionalInt.getAsInt());
		} else {
			commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass"), false);
			return 1;
		}
	}

	private static OptionalInt method_13272(CommandContext<ServerCommandSource> commandContext, boolean bl) throws CommandSyntaxException {
		return method_13261(
			commandContext.getSource().getWorld(),
			BlockPosArgumentType.getValidPosArgument(commandContext, "start"),
			BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
			BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
			bl
		);
	}

	private static OptionalInt method_13261(ServerWorld serverWorld, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, boolean bl) throws CommandSyntaxException {
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
	interface class_3051 {
		int test(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface class_3052 {
		boolean test(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;
	}
}
