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
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NumberRange;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.BossBarCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class ExecuteCommand {
    private static final Dynamic2CommandExceptionType BLOCKS_TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.execute.blocks.toobig", object, object2));
    private static final SimpleCommandExceptionType CONDITIONAL_FAIL_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.execute.conditional.fail"));
    private static final DynamicCommandExceptionType CONDITIONAL_FAIL_COUNT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.execute.conditional.fail_count", object));
    private static final BinaryOperator<ResultConsumer<ServerCommandSource>> BINARY_RESULT_CONSUMER = (resultConsumer, resultConsumer2) -> (commandContext, bl, i) -> {
        resultConsumer.onCommandComplete(commandContext, bl, i);
        resultConsumer2.onCommandComplete(commandContext, bl, i);
    };
    private static final SuggestionProvider<ServerCommandSource> field_20852 = (commandContext, suggestionsBuilder) -> {
        LootConditionManager lootConditionManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPredicateManager();
        return CommandSource.suggestIdentifiers(lootConditionManager.getIds(), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("execute").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("execute").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("run").redirect(dispatcher.getRoot()))).then(ExecuteCommand.addConditionArguments(literalCommandNode, CommandManager.literal("if"), true))).then(ExecuteCommand.addConditionArguments(literalCommandNode, CommandManager.literal("unless"), false))).then(CommandManager.literal("as").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, commandContext -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withEntity(entity));
            }
            return list;
        })))).then(CommandManager.literal("at").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, commandContext -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withWorld((ServerWorld)entity.world).withPosition(entity.getPos()).withRotation(entity.getRotationClient()));
            }
            return list;
        })))).then(((LiteralArgumentBuilder)CommandManager.literal("store").then(ExecuteCommand.addStoreArguments(literalCommandNode, CommandManager.literal("result"), true))).then(ExecuteCommand.addStoreArguments(literalCommandNode, CommandManager.literal("success"), false)))).then(((LiteralArgumentBuilder)CommandManager.literal("positioned").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect(literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withPosition(Vec3ArgumentType.getVec3(commandContext, "pos")).withEntityAnchor(EntityAnchorArgumentType.EntityAnchor.FEET)))).then(CommandManager.literal("as").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, commandContext -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withPosition(entity.getPos()));
            }
            return list;
        }))))).then(((LiteralArgumentBuilder)CommandManager.literal("rotated").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("rot", RotationArgumentType.rotation()).redirect(literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withRotation(RotationArgumentType.getRotation(commandContext, "rot").toAbsoluteRotation((ServerCommandSource)commandContext.getSource()))))).then(CommandManager.literal("as").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, commandContext -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withRotation(entity.getRotationClient()));
            }
            return list;
        }))))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).fork(literalCommandNode, commandContext -> {
            ArrayList<ServerCommandSource> list = Lists.newArrayList();
            EntityAnchorArgumentType.EntityAnchor entityAnchor = EntityAnchorArgumentType.getEntityAnchor(commandContext, "anchor");
            for (Entity entity : EntityArgumentType.getOptionalEntities(commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withLookingAt(entity, entityAnchor));
            }
            return list;
        }))))).then(CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect(literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withLookingAt(Vec3ArgumentType.getVec3(commandContext, "pos")))))).then(CommandManager.literal("align").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("axes", SwizzleArgumentType.swizzle()).redirect(literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withPosition(((ServerCommandSource)commandContext.getSource()).getPosition().floorAlongAxes(SwizzleArgumentType.getSwizzle(commandContext, "axes"))))))).then(CommandManager.literal("anchored").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).redirect(literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withEntityAnchor(EntityAnchorArgumentType.getEntityAnchor(commandContext, "anchor")))))).then(CommandManager.literal("in").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("dimension", DimensionArgumentType.dimension()).redirect(literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withWorld(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getWorld(DimensionArgumentType.getDimensionArgument(commandContext, "dimension")))))));
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addStoreArguments(LiteralCommandNode<ServerCommandSource> node, LiteralArgumentBuilder<ServerCommandSource> builder, boolean requestResult) {
        builder.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("score").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("objective", ObjectiveArgumentType.objective()).redirect(node, commandContext -> ExecuteCommand.executeStoreScore((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "targets"), ObjectiveArgumentType.getObjective(commandContext, "objective"), requestResult)))));
        builder.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("bossbar").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(BossBarCommand.suggestionProvider).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("value").redirect(node, commandContext -> ExecuteCommand.executeStoreBossbar((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), true, requestResult)))).then(CommandManager.literal("max").redirect(node, commandContext -> ExecuteCommand.executeStoreBossbar((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar(commandContext), false, requestResult)))));
        for (DataCommand.ObjectType objectType : DataCommand.TARGET_OBJECT_TYPES) {
            objectType.addArgumentsToBuilder(builder, argumentBuilder -> argumentBuilder.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("int").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"), i -> IntTag.of((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))).then(CommandManager.literal("float").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"), i -> FloatTag.of((float)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))).then(CommandManager.literal("short").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"), i -> ShortTag.of((short)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))).then(CommandManager.literal("long").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"), i -> LongTag.of((long)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))).then(CommandManager.literal("double").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"), i -> DoubleTag.of((double)i * DoubleArgumentType.getDouble(commandContext, "scale")), requestResult))))).then(CommandManager.literal("byte").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"), i -> ByteTag.of((byte)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))));
        }
        return builder;
    }

    private static ServerCommandSource executeStoreScore(ServerCommandSource source, Collection<String> targets, ScoreboardObjective objective, boolean requestResult) {
        ServerScoreboard scoreboard = source.getMinecraftServer().getScoreboard();
        return source.mergeConsumers((commandContext, bl2, i) -> {
            for (String string : targets) {
                ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, objective);
                int j = requestResult ? i : (bl2 ? 1 : 0);
                scoreboardPlayerScore.setScore(j);
            }
        }, BINARY_RESULT_CONSUMER);
    }

    private static ServerCommandSource executeStoreBossbar(ServerCommandSource source, CommandBossBar bossBar, boolean storeInValue, boolean requestResult) {
        return source.mergeConsumers((commandContext, bl3, i) -> {
            int j;
            int n = requestResult ? i : (j = bl3 ? 1 : 0);
            if (storeInValue) {
                bossBar.setValue(j);
            } else {
                bossBar.setMaxValue(j);
            }
        }, BINARY_RESULT_CONSUMER);
    }

    private static ServerCommandSource executeStoreData(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, IntFunction<Tag> tagSetter, boolean requestResult) {
        return source.mergeConsumers((commandContext, bl2, i) -> {
            try {
                CompoundTag compoundTag = object.getTag();
                int j = requestResult ? i : (bl2 ? 1 : 0);
                path.put(compoundTag, () -> (Tag)tagSetter.apply(j));
                object.setTag(compoundTag);
            } catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }, BINARY_RESULT_CONSUMER);
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addConditionArguments(CommandNode<ServerCommandSource> root, LiteralArgumentBuilder<ServerCommandSource> argumentBuilder2, boolean positive) {
        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)argumentBuilder2.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("block").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("block", BlockPredicateArgumentType.blockPredicate()), positive, commandContext -> BlockPredicateArgumentType.getBlockPredicate(commandContext, "block").test(new CachedBlockPosition(((ServerCommandSource)commandContext.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), true))))))).then(CommandManager.literal("score").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targetObjective", ObjectiveArgumentType.objective()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("=").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), positive, commandContext -> ExecuteCommand.testScoreCondition(commandContext, Integer::equals)))))).then(CommandManager.literal("<").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), positive, commandContext -> ExecuteCommand.testScoreCondition(commandContext, (integer, integer2) -> integer < integer2)))))).then(CommandManager.literal("<=").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), positive, commandContext -> ExecuteCommand.testScoreCondition(commandContext, (integer, integer2) -> integer <= integer2)))))).then(CommandManager.literal(">").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), positive, commandContext -> ExecuteCommand.testScoreCondition(commandContext, (integer, integer2) -> integer > integer2)))))).then(CommandManager.literal(">=").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), positive, commandContext -> ExecuteCommand.testScoreCondition(commandContext, (integer, integer2) -> integer >= integer2)))))).then(CommandManager.literal("matches").then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("range", NumberRangeArgumentType.numberRange()), positive, commandContext -> ExecuteCommand.testScoreMatch(commandContext, NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument(commandContext, "range"))))))))).then(CommandManager.literal("blocks").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("start", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("end", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.blockPos()).then(ExecuteCommand.addBlocksConditionLogic(root, CommandManager.literal("all"), positive, false))).then(ExecuteCommand.addBlocksConditionLogic(root, CommandManager.literal("masked"), positive, true))))))).then(CommandManager.literal("entity").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("entities", EntityArgumentType.entities()).fork(root, commandContext -> ExecuteCommand.getSourceOrEmptyForConditionFork(commandContext, positive, !EntityArgumentType.getOptionalEntities(commandContext, "entities").isEmpty()))).executes(ExecuteCommand.getExistsConditionExecute(positive, commandContext -> EntityArgumentType.getOptionalEntities(commandContext, "entities").size()))))).then(CommandManager.literal("predicate").then(ExecuteCommand.addConditionLogic(root, CommandManager.argument("predicate", IdentifierArgumentType.identifier()).suggests(field_20852), positive, commandContext -> ExecuteCommand.testLootCondition((ServerCommandSource)commandContext.getSource(), IdentifierArgumentType.method_23727(commandContext, "predicate")))));
        for (DataCommand.ObjectType objectType : DataCommand.SOURCE_OBJECT_TYPES) {
            argumentBuilder2.then(objectType.addArgumentsToBuilder(CommandManager.literal("data"), argumentBuilder -> argumentBuilder.then(((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).fork(root, commandContext -> ExecuteCommand.getSourceOrEmptyForConditionFork(commandContext, positive, ExecuteCommand.countPathMatches(objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path")) > 0))).executes(ExecuteCommand.getExistsConditionExecute(positive, commandContext -> ExecuteCommand.countPathMatches(objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path")))))));
        }
        return argumentBuilder2;
    }

    private static Command<ServerCommandSource> getExistsConditionExecute(boolean positive, ExistsCondition condition) {
        if (positive) {
            return commandContext -> {
                int i = condition.test(commandContext);
                if (i > 0) {
                    ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass_count", i), false);
                    return i;
                }
                throw CONDITIONAL_FAIL_EXCEPTION.create();
            };
        }
        return commandContext -> {
            int i = condition.test(commandContext);
            if (i == 0) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(i);
        };
    }

    private static int countPathMatches(DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        return path.count(object.getTag());
    }

    private static boolean testScoreCondition(CommandContext<ServerCommandSource> context, BiPredicate<Integer, Integer> condition) throws CommandSyntaxException {
        String string = ScoreHolderArgumentType.getScoreHolder(context, "target");
        ScoreboardObjective scoreboardObjective = ObjectiveArgumentType.getObjective(context, "targetObjective");
        String string2 = ScoreHolderArgumentType.getScoreHolder(context, "source");
        ScoreboardObjective scoreboardObjective2 = ObjectiveArgumentType.getObjective(context, "sourceObjective");
        ServerScoreboard scoreboard = context.getSource().getMinecraftServer().getScoreboard();
        if (!scoreboard.playerHasObjective(string, scoreboardObjective) || !scoreboard.playerHasObjective(string2, scoreboardObjective2)) {
            return false;
        }
        ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
        ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
        return condition.test(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore());
    }

    private static boolean testScoreMatch(CommandContext<ServerCommandSource> context, NumberRange.IntRange range) throws CommandSyntaxException {
        String string = ScoreHolderArgumentType.getScoreHolder(context, "target");
        ScoreboardObjective scoreboardObjective = ObjectiveArgumentType.getObjective(context, "targetObjective");
        ServerScoreboard scoreboard = context.getSource().getMinecraftServer().getScoreboard();
        if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
            return false;
        }
        return range.test(scoreboard.getPlayerScore(string, scoreboardObjective).getScore());
    }

    private static boolean testLootCondition(ServerCommandSource serverCommandSource, LootCondition lootCondition) {
        ServerWorld serverWorld = serverCommandSource.getWorld();
        LootContext.Builder builder = new LootContext.Builder(serverWorld).put(LootContextParameters.POSITION, new BlockPos(serverCommandSource.getPosition())).putNullable(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity());
        return lootCondition.test(builder.build(LootContextTypes.COMMAND));
    }

    private static Collection<ServerCommandSource> getSourceOrEmptyForConditionFork(CommandContext<ServerCommandSource> context, boolean positive, boolean value) {
        if (value == positive) {
            return Collections.singleton(context.getSource());
        }
        return Collections.emptyList();
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addConditionLogic(CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, Condition condition) {
        return ((ArgumentBuilder)builder.fork(root, commandContext -> ExecuteCommand.getSourceOrEmptyForConditionFork(commandContext, positive, condition.test(commandContext)))).executes(commandContext -> {
            if (positive == condition.test(commandContext)) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw CONDITIONAL_FAIL_EXCEPTION.create();
        });
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addBlocksConditionLogic(CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, boolean masked) {
        return ((ArgumentBuilder)builder.fork(root, commandContext -> ExecuteCommand.getSourceOrEmptyForConditionFork(commandContext, positive, ExecuteCommand.testBlocksCondition(commandContext, masked).isPresent()))).executes(positive ? commandContext -> ExecuteCommand.executePositiveBlockCondition(commandContext, masked) : commandContext -> ExecuteCommand.executeNegativeBlockCondition(commandContext, masked));
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
        BlockBox blockBox = new BlockBox(start, end);
        BlockBox blockBox2 = new BlockBox(destination, destination.add(blockBox.getDimensions()));
        BlockPos blockPos = new BlockPos(blockBox2.minX - blockBox.minX, blockBox2.minY - blockBox.minY, blockBox2.minZ - blockBox.minZ);
        int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
        if (i > 32768) {
            throw BLOCKS_TOOBIG_EXCEPTION.create(32768, i);
        }
        int j = 0;
        for (int k = blockBox.minZ; k <= blockBox.maxZ; ++k) {
            for (int l = blockBox.minY; l <= blockBox.maxY; ++l) {
                for (int m = blockBox.minX; m <= blockBox.maxX; ++m) {
                    BlockPos blockPos2 = new BlockPos(m, l, k);
                    BlockPos blockPos3 = blockPos2.add(blockPos);
                    BlockState blockState = world.getBlockState(blockPos2);
                    if (masked && blockState.getBlock() == Blocks.AIR) continue;
                    if (blockState != world.getBlockState(blockPos3)) {
                        return OptionalInt.empty();
                    }
                    BlockEntity blockEntity = world.getBlockEntity(blockPos2);
                    BlockEntity blockEntity2 = world.getBlockEntity(blockPos3);
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
                    ++j;
                }
            }
        }
        return OptionalInt.of(j);
    }

    @FunctionalInterface
    static interface ExistsCondition {
        public int test(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface Condition {
        public boolean test(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }
}

