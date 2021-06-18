/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.BossBarCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class ExecuteCommand {
    private static final int MAX_BLOCKS = 32768;
    private static final Dynamic2CommandExceptionType BLOCKS_TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((maxCount, count) -> new TranslatableText("commands.execute.blocks.toobig", maxCount, count));
    private static final SimpleCommandExceptionType CONDITIONAL_FAIL_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.execute.conditional.fail"));
    private static final DynamicCommandExceptionType CONDITIONAL_FAIL_COUNT_EXCEPTION = new DynamicCommandExceptionType(count -> new TranslatableText("commands.execute.conditional.fail_count", count));
    private static final BinaryOperator<ResultConsumer<ServerCommandSource>> BINARY_RESULT_CONSUMER = (resultConsumer, resultConsumer2) -> (context, success, result) -> {
        resultConsumer.onCommandComplete(context, success, result);
        resultConsumer2.onCommandComplete(context, success, result);
    };
    private static final SuggestionProvider<ServerCommandSource> LOOT_CONDITIONS = (context, builder) -> {
        LootConditionManager lootConditionManager = ((ServerCommandSource)context.getSource()).getServer().getPredicateManager();
        return CommandSource.suggestIdentifiers(lootConditionManager.getIds(), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("execute").requires(source -> source.hasPermissionLevel(2)));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("execute").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("run").redirect(dispatcher.getRoot()))).then(ExecuteCommand.addConditionArguments(literalCommandNode, CommandManager.literal("if"), true))).then(ExecuteCommand.addConditionArguments(literalCommandNode, CommandManager.literal("unless"), false))).then(CommandManager.literal("as").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, context -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
                list.add(((ServerCommandSource)context.getSource()).withEntity(entity));
            }
            return list;
        })))).then(CommandManager.literal("at").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, context -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
                list.add(((ServerCommandSource)context.getSource()).withWorld((ServerWorld)entity.world).withPosition(entity.getPos()).withRotation(entity.getRotationClient()));
            }
            return list;
        })))).then(((LiteralArgumentBuilder)CommandManager.literal("store").then(ExecuteCommand.addStoreArguments(literalCommandNode, CommandManager.literal("result"), true))).then(ExecuteCommand.addStoreArguments(literalCommandNode, CommandManager.literal("success"), false)))).then(((LiteralArgumentBuilder)CommandManager.literal("positioned").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect(literalCommandNode, context -> ((ServerCommandSource)context.getSource()).withPosition(Vec3ArgumentType.getVec3(context, "pos")).withEntityAnchor(EntityAnchorArgumentType.EntityAnchor.FEET)))).then(CommandManager.literal("as").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, context -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
                list.add(((ServerCommandSource)context.getSource()).withPosition(entity.getPos()));
            }
            return list;
        }))))).then(((LiteralArgumentBuilder)CommandManager.literal("rotated").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("rot", RotationArgumentType.rotation()).redirect(literalCommandNode, context -> ((ServerCommandSource)context.getSource()).withRotation(RotationArgumentType.getRotation(context, "rot").toAbsoluteRotation((ServerCommandSource)context.getSource()))))).then(CommandManager.literal("as").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, context -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
                list.add(((ServerCommandSource)context.getSource()).withRotation(entity.getRotationClient()));
            }
            return list;
        }))))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).fork(literalCommandNode, context -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            EntityAnchorArgumentType.EntityAnchor entityAnchor = EntityAnchorArgumentType.getEntityAnchor(context, "anchor");
            for (Entity entity : EntityArgumentType.getOptionalEntities(context, "targets")) {
                list.add(((ServerCommandSource)context.getSource()).withLookingAt(entity, entityAnchor));
            }
            return list;
        }))))).then(CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect(literalCommandNode, context -> ((ServerCommandSource)context.getSource()).withLookingAt(Vec3ArgumentType.getVec3(context, "pos")))))).then(CommandManager.literal("align").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("axes", SwizzleArgumentType.swizzle()).redirect(literalCommandNode, context -> ((ServerCommandSource)context.getSource()).withPosition(((ServerCommandSource)context.getSource()).getPosition().floorAlongAxes(SwizzleArgumentType.getSwizzle(context, "axes"))))))).then(CommandManager.literal("anchored").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).redirect(literalCommandNode, context -> ((ServerCommandSource)context.getSource()).withEntityAnchor(EntityAnchorArgumentType.getEntityAnchor(context, "anchor")))))).then(CommandManager.literal("in").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("dimension", DimensionArgumentType.dimension()).redirect(literalCommandNode, context -> ((ServerCommandSource)context.getSource()).withWorld(DimensionArgumentType.getDimensionArgument(context, "dimension"))))));
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addStoreArguments(LiteralCommandNode<ServerCommandSource> node, LiteralArgumentBuilder<ServerCommandSource> builder2, boolean requestResult) {
        builder2.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("score").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective()).redirect(node, context -> ExecuteCommand.executeStoreScore((ServerCommandSource)context.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(context, "targets"), ScoreboardObjectiveArgumentType.getObjective(context, "objective"), requestResult)))));
        builder2.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("bossbar").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(BossBarCommand.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("value").redirect(node, context -> ExecuteCommand.executeStoreBossbar((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), true, requestResult)))).then(CommandManager.literal("max").redirect(node, context -> ExecuteCommand.executeStoreBossbar((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), false, requestResult)))));
        for (DataCommand.ObjectType objectType : DataCommand.TARGET_OBJECT_TYPES) {
            objectType.addArgumentsToBuilder(builder2, builder -> builder.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("int").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, context -> ExecuteCommand.executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), result -> NbtInt.of((int)((double)result * DoubleArgumentType.getDouble(context, "scale"))), requestResult))))).then(CommandManager.literal("float").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, context -> ExecuteCommand.executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), result -> NbtFloat.of((float)((double)result * DoubleArgumentType.getDouble(context, "scale"))), requestResult))))).then(CommandManager.literal("short").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, context -> ExecuteCommand.executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), result -> NbtShort.of((short)((double)result * DoubleArgumentType.getDouble(context, "scale"))), requestResult))))).then(CommandManager.literal("long").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, context -> ExecuteCommand.executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), result -> NbtLong.of((long)((double)result * DoubleArgumentType.getDouble(context, "scale"))), requestResult))))).then(CommandManager.literal("double").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, context -> ExecuteCommand.executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), result -> NbtDouble.of((double)result * DoubleArgumentType.getDouble(context, "scale")), requestResult))))).then(CommandManager.literal("byte").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, context -> ExecuteCommand.executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), result -> NbtByte.of((byte)((double)result * DoubleArgumentType.getDouble(context, "scale"))), requestResult))))));
        }
        return builder2;
    }

    private static ServerCommandSource executeStoreScore(ServerCommandSource source, Collection<String> targets, ScoreboardObjective objective, boolean requestResult) {
        ServerScoreboard scoreboard = source.getServer().getScoreboard();
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
            int i;
            int n = requestResult ? result : (i = success ? 1 : 0);
            if (storeInValue) {
                bossBar.setValue(i);
            } else {
                bossBar.setMaxValue(i);
            }
        }, BINARY_RESULT_CONSUMER);
    }

    private static ServerCommandSource executeStoreData(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, IntFunction<NbtElement> nbtSetter, boolean requestResult) {
        return source.mergeConsumers((context, success, result) -> {
            try {
                NbtCompound nbtCompound = object.getNbt();
                int i = requestResult ? result : (success ? 1 : 0);
                path.put((NbtElement)nbtCompound, () -> (NbtElement)nbtSetter.apply(i));
                object.setNbt(nbtCompound);
            } catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }, BINARY_RESULT_CONSUMER);
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addConditionArguments(CommandNode<ServerCommandSource> root, LiteralArgumentBuilder<ServerCommandSource> argumentBuilder, boolean positive) {
        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)argumentBuilder.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("block").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("block", BlockPredicateArgumentType.blockPredicate()), positive, context -> BlockPredicateArgumentType.getBlockPredicate(context, "block").test(new CachedBlockPosition(((ServerCommandSource)context.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), true))))))).then(CommandManager.literal("score").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targetObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("=").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, context -> ExecuteCommand.testScoreCondition(context, Integer::equals)))))).then(CommandManager.literal("<").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, context -> ExecuteCommand.testScoreCondition(context, (a, b) -> a < b)))))).then(CommandManager.literal("<=").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, context -> ExecuteCommand.testScoreCondition(context, (a, b) -> a <= b)))))).then(CommandManager.literal(">").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, context -> ExecuteCommand.testScoreCondition(context, (a, b) -> a > b)))))).then(CommandManager.literal(">=").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, context -> ExecuteCommand.testScoreCondition(context, (a, b) -> a >= b)))))).then(CommandManager.literal("matches").then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("range", NumberRangeArgumentType.intRange()), positive, context -> ExecuteCommand.testScoreMatch(context, NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument(context, "range"))))))))).then(CommandManager.literal("blocks").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("start", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("end", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.blockPos()).then(ExecuteCommand.addBlocksConditionLogic(root, CommandManager.literal("all"), positive, false))).then(ExecuteCommand.addBlocksConditionLogic(root, CommandManager.literal("masked"), positive, true))))))).then(CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("entities", EntityArgumentType.entities()).fork(root, context -> ExecuteCommand.getSourceOrEmptyForConditionFork(context, positive, !EntityArgumentType.getOptionalEntities(context, "entities").isEmpty()))).executes(ExecuteCommand.getExistsConditionExecute(positive, context -> EntityArgumentType.getOptionalEntities(context, "entities").size()))))).then(CommandManager.literal("predicate").then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("predicate", IdentifierArgumentType.identifier()).suggests(LOOT_CONDITIONS), positive, context -> ExecuteCommand.testLootCondition((ServerCommandSource)context.getSource(), IdentifierArgumentType.getPredicateArgument(context, "predicate")))));
        for (DataCommand.ObjectType objectType : DataCommand.SOURCE_OBJECT_TYPES) {
            argumentBuilder.then(objectType.addArgumentsToBuilder(CommandManager.literal("data"), builder -> builder.then(((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).fork(root, commandContext -> ExecuteCommand.getSourceOrEmptyForConditionFork(commandContext, positive, ExecuteCommand.countPathMatches(objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path")) > 0))).executes(ExecuteCommand.getExistsConditionExecute(positive, context -> ExecuteCommand.countPathMatches(objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path")))))));
        }
        return argumentBuilder;
    }

    private static Command<ServerCommandSource> getExistsConditionExecute(boolean positive, ExistsCondition condition) {
        if (positive) {
            return context -> {
                int i = condition.test(context);
                if (i > 0) {
                    ((ServerCommandSource)context.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass_count", i), false);
                    return i;
                }
                throw CONDITIONAL_FAIL_EXCEPTION.create();
            };
        }
        return context -> {
            int i = condition.test(context);
            if (i == 0) {
                ((ServerCommandSource)context.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(i);
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
        ServerScoreboard scoreboard = context.getSource().getServer().getScoreboard();
        if (!scoreboard.playerHasObjective(string, scoreboardObjective) || !scoreboard.playerHasObjective(string2, scoreboardObjective2)) {
            return false;
        }
        ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
        ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
        return condition.test(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore());
    }

    private static boolean testScoreMatch(CommandContext<ServerCommandSource> context, NumberRange.IntRange range) throws CommandSyntaxException {
        String string = ScoreHolderArgumentType.getScoreHolder(context, "target");
        ScoreboardObjective scoreboardObjective = ScoreboardObjectiveArgumentType.getObjective(context, "targetObjective");
        ServerScoreboard scoreboard = context.getSource().getServer().getScoreboard();
        if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
            return false;
        }
        return range.test(scoreboard.getPlayerScore(string, scoreboardObjective).getScore());
    }

    private static boolean testLootCondition(ServerCommandSource source, LootCondition condition) {
        ServerWorld serverWorld = source.getWorld();
        LootContext.Builder builder = new LootContext.Builder(serverWorld).parameter(LootContextParameters.ORIGIN, source.getPosition()).optionalParameter(LootContextParameters.THIS_ENTITY, source.getEntity());
        return condition.test(builder.build(LootContextTypes.COMMAND));
    }

    private static Collection<ServerCommandSource> getSourceOrEmptyForConditionFork(CommandContext<ServerCommandSource> context, boolean positive, boolean value) {
        if (value == positive) {
            return Collections.singleton(context.getSource());
        }
        return Collections.emptyList();
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addConditionLogic(CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, Condition condition) {
        return ((ArgumentBuilder)builder.fork(root, context -> ExecuteCommand.getSourceOrEmptyForConditionFork(context, positive, condition.test(context)))).executes(context -> {
            if (positive == condition.test(context)) {
                ((ServerCommandSource)context.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw CONDITIONAL_FAIL_EXCEPTION.create();
        });
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addBlocksConditionLogic(CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, boolean masked) {
        return ((ArgumentBuilder)builder.fork(root, context -> ExecuteCommand.getSourceOrEmptyForConditionFork(context, positive, ExecuteCommand.testBlocksCondition(context, masked).isPresent()))).executes(positive ? context -> ExecuteCommand.executePositiveBlockCondition(context, masked) : context -> ExecuteCommand.executeNegativeBlockCondition(context, masked));
    }

    private static int executePositiveBlockCondition(CommandContext<ServerCommandSource> context, boolean masked) throws CommandSyntaxException {
        OptionalInt optionalInt = ExecuteCommand.testBlocksCondition(context, masked);
        if (optionalInt.isPresent()) {
            context.getSource().sendFeedback(new TranslatableText("commands.execute.conditional.pass_count", optionalInt.getAsInt()), false);
            return optionalInt.getAsInt();
        }
        throw CONDITIONAL_FAIL_EXCEPTION.create();
    }

    private static int executeNegativeBlockCondition(CommandContext<ServerCommandSource> context, boolean masked) throws CommandSyntaxException {
        OptionalInt optionalInt = ExecuteCommand.testBlocksCondition(context, masked);
        if (optionalInt.isPresent()) {
            throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(optionalInt.getAsInt());
        }
        context.getSource().sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
        return 1;
    }

    private static OptionalInt testBlocksCondition(CommandContext<ServerCommandSource> context, boolean masked) throws CommandSyntaxException {
        return ExecuteCommand.testBlocksCondition(context.getSource().getWorld(), BlockPosArgumentType.getLoadedBlockPos(context, "start"), BlockPosArgumentType.getLoadedBlockPos(context, "end"), BlockPosArgumentType.getLoadedBlockPos(context, "destination"), masked);
    }

    private static OptionalInt testBlocksCondition(ServerWorld world, BlockPos start, BlockPos end, BlockPos destination, boolean masked) throws CommandSyntaxException {
        BlockBox blockBox = BlockBox.create(start, end);
        BlockBox blockBox2 = BlockBox.create(destination, destination.add(blockBox.getDimensions()));
        BlockPos blockPos = new BlockPos(blockBox2.getMinX() - blockBox.getMinX(), blockBox2.getMinY() - blockBox.getMinY(), blockBox2.getMinZ() - blockBox.getMinZ());
        int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
        if (i > 32768) {
            throw BLOCKS_TOOBIG_EXCEPTION.create(32768, i);
        }
        int j = 0;
        for (int k = blockBox.getMinZ(); k <= blockBox.getMaxZ(); ++k) {
            for (int l = blockBox.getMinY(); l <= blockBox.getMaxY(); ++l) {
                for (int m = blockBox.getMinX(); m <= blockBox.getMaxX(); ++m) {
                    BlockPos blockPos2 = new BlockPos(m, l, k);
                    BlockPos blockPos3 = blockPos2.add(blockPos);
                    BlockState blockState = world.getBlockState(blockPos2);
                    if (masked && blockState.isOf(Blocks.AIR)) continue;
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
                    ++j;
                }
            }
        }
        return OptionalInt.of(j);
    }

    @FunctionalInterface
    static interface Condition {
        public boolean test(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface ExistsCondition {
        public int test(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }
}

