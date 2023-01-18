/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.tick.WorldTickScheduler;
import org.jetbrains.annotations.Nullable;

public class CloneCommand {
    private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.clone.overlap"));
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((maxCount, count) -> Text.translatable("commands.clone.toobig", maxCount, count));
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.clone.failed"));
    public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = pos -> !pos.getBlockState().isAir();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clone").requires(source -> source.hasPermissionLevel(2))).then(CloneCommand.createSourceArgs(commandRegistryAccess, context -> ((ServerCommandSource)context.getSource()).getWorld()))).then(CommandManager.literal("from").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("sourceDimension", DimensionArgumentType.dimension()).then(CloneCommand.createSourceArgs(commandRegistryAccess, context -> DimensionArgumentType.getDimensionArgument(context, "sourceDimension"))))));
    }

    private static ArgumentBuilder<ServerCommandSource, ?> createSourceArgs(CommandRegistryAccess commandRegistryAccess, ArgumentGetter<CommandContext<ServerCommandSource>, ServerWorld> worldGetter) {
        return CommandManager.argument("begin", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("end", BlockPosArgumentType.blockPos()).then(CloneCommand.createDestinationArgs(commandRegistryAccess, worldGetter, context -> ((ServerCommandSource)context.getSource()).getWorld()))).then(CommandManager.literal("to").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetDimension", DimensionArgumentType.dimension()).then(CloneCommand.createDestinationArgs(commandRegistryAccess, worldGetter, context -> DimensionArgumentType.getDimensionArgument(context, "targetDimension"))))));
    }

    private static DimensionalPos createDimensionalPos(CommandContext<ServerCommandSource> context, ServerWorld world, String name) throws CommandSyntaxException {
        BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(context, world, name);
        return new DimensionalPos(world, blockPos);
    }

    private static ArgumentBuilder<ServerCommandSource, ?> createDestinationArgs(CommandRegistryAccess commandRegistryAccess, ArgumentGetter<CommandContext<ServerCommandSource>, ServerWorld> sourceWorldGetter, ArgumentGetter<CommandContext<ServerCommandSource>, ServerWorld> targetWorldGetter) {
        ArgumentGetter<CommandContext<ServerCommandSource>, DimensionalPos> argumentGetter = context -> CloneCommand.createDimensionalPos(context, (ServerWorld)sourceWorldGetter.apply((CommandContext<ServerCommandSource>)context), "begin");
        ArgumentGetter<CommandContext<ServerCommandSource>, DimensionalPos> argumentGetter2 = context -> CloneCommand.createDimensionalPos(context, (ServerWorld)sourceWorldGetter.apply((CommandContext<ServerCommandSource>)context), "end");
        ArgumentGetter<CommandContext<ServerCommandSource>, DimensionalPos> argumentGetter3 = context -> CloneCommand.createDimensionalPos(context, (ServerWorld)targetWorldGetter.apply((CommandContext<ServerCommandSource>)context), "destination");
        return ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.blockPos()).executes(context -> CloneCommand.execute((ServerCommandSource)context.getSource(), (DimensionalPos)argumentGetter.apply(context), (DimensionalPos)argumentGetter2.apply(context), (DimensionalPos)argumentGetter3.apply(context), cachedBlockPosition -> true, Mode.NORMAL))).then(CloneCommand.createModeArgs(argumentGetter, argumentGetter2, argumentGetter3, context -> cachedBlockPosition -> true, CommandManager.literal("replace").executes(context -> CloneCommand.execute((ServerCommandSource)context.getSource(), (DimensionalPos)argumentGetter.apply(context), (DimensionalPos)argumentGetter2.apply(context), (DimensionalPos)argumentGetter3.apply(context), cachedBlockPosition -> true, Mode.NORMAL))))).then(CloneCommand.createModeArgs(argumentGetter, argumentGetter2, argumentGetter3, context -> IS_AIR_PREDICATE, CommandManager.literal("masked").executes(context -> CloneCommand.execute((ServerCommandSource)context.getSource(), (DimensionalPos)argumentGetter.apply(context), (DimensionalPos)argumentGetter2.apply(context), (DimensionalPos)argumentGetter3.apply(context), IS_AIR_PREDICATE, Mode.NORMAL))))).then(CommandManager.literal("filtered").then(CloneCommand.createModeArgs(argumentGetter, argumentGetter2, argumentGetter3, context -> BlockPredicateArgumentType.getBlockPredicate(context, "filter"), CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate(commandRegistryAccess)).executes(context -> CloneCommand.execute((ServerCommandSource)context.getSource(), (DimensionalPos)argumentGetter.apply(context), (DimensionalPos)argumentGetter2.apply(context), (DimensionalPos)argumentGetter3.apply(context), BlockPredicateArgumentType.getBlockPredicate(context, "filter"), Mode.NORMAL)))));
    }

    private static ArgumentBuilder<ServerCommandSource, ?> createModeArgs(ArgumentGetter<CommandContext<ServerCommandSource>, DimensionalPos> beginPosGetter, ArgumentGetter<CommandContext<ServerCommandSource>, DimensionalPos> endPosGetter, ArgumentGetter<CommandContext<ServerCommandSource>, DimensionalPos> destinationPosGetter, ArgumentGetter<CommandContext<ServerCommandSource>, Predicate<CachedBlockPosition>> filterGetter, ArgumentBuilder<ServerCommandSource, ?> builder) {
        return ((ArgumentBuilder)((ArgumentBuilder)builder.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("force").executes(context -> CloneCommand.execute((ServerCommandSource)context.getSource(), (DimensionalPos)beginPosGetter.apply(context), (DimensionalPos)endPosGetter.apply(context), (DimensionalPos)destinationPosGetter.apply(context), (Predicate)filterGetter.apply(context), Mode.FORCE)))).then(CommandManager.literal("move").executes(context -> CloneCommand.execute((ServerCommandSource)context.getSource(), (DimensionalPos)beginPosGetter.apply(context), (DimensionalPos)endPosGetter.apply(context), (DimensionalPos)destinationPosGetter.apply(context), (Predicate)filterGetter.apply(context), Mode.MOVE)))).then(CommandManager.literal("normal").executes(context -> CloneCommand.execute((ServerCommandSource)context.getSource(), (DimensionalPos)beginPosGetter.apply(context), (DimensionalPos)endPosGetter.apply(context), (DimensionalPos)destinationPosGetter.apply(context), (Predicate)filterGetter.apply(context), Mode.NORMAL)));
    }

    private static int execute(ServerCommandSource source, DimensionalPos begin, DimensionalPos end, DimensionalPos destination, Predicate<CachedBlockPosition> filter, Mode mode) throws CommandSyntaxException {
        int j;
        BlockPos blockPos = begin.position();
        BlockPos blockPos2 = end.position();
        BlockBox blockBox = BlockBox.create(blockPos, blockPos2);
        BlockPos blockPos3 = destination.position();
        BlockPos blockPos4 = blockPos3.add(blockBox.getDimensions());
        BlockBox blockBox2 = BlockBox.create(blockPos3, blockPos4);
        ServerWorld serverWorld = begin.dimension();
        ServerWorld serverWorld2 = destination.dimension();
        if (!mode.allowsOverlap() && serverWorld == serverWorld2 && blockBox2.intersects(blockBox)) {
            throw OVERLAP_EXCEPTION.create();
        }
        int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
        if (i > (j = source.getWorld().getGameRules().getInt(GameRules.COMMAND_MODIFICATION_BLOCK_LIMIT))) {
            throw TOO_BIG_EXCEPTION.create(j, i);
        }
        if (!serverWorld.isRegionLoaded(blockPos, blockPos2) || !serverWorld2.isRegionLoaded(blockPos3, blockPos4)) {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
        }
        ArrayList<BlockInfo> list = Lists.newArrayList();
        ArrayList<BlockInfo> list2 = Lists.newArrayList();
        ArrayList<BlockInfo> list3 = Lists.newArrayList();
        LinkedList<BlockPos> deque = Lists.newLinkedList();
        BlockPos blockPos5 = new BlockPos(blockBox2.getMinX() - blockBox.getMinX(), blockBox2.getMinY() - blockBox.getMinY(), blockBox2.getMinZ() - blockBox.getMinZ());
        for (int k = blockBox.getMinZ(); k <= blockBox.getMaxZ(); ++k) {
            for (int l = blockBox.getMinY(); l <= blockBox.getMaxY(); ++l) {
                for (int m = blockBox.getMinX(); m <= blockBox.getMaxX(); ++m) {
                    BlockPos blockPos6 = new BlockPos(m, l, k);
                    BlockPos blockPos7 = blockPos6.add(blockPos5);
                    CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos6, false);
                    BlockState blockState = cachedBlockPosition.getBlockState();
                    if (!filter.test(cachedBlockPosition)) continue;
                    BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos6);
                    if (blockEntity != null) {
                        NbtCompound nbtCompound = blockEntity.createNbt();
                        list2.add(new BlockInfo(blockPos7, blockState, nbtCompound));
                        deque.addLast(blockPos6);
                        continue;
                    }
                    if (blockState.isOpaqueFullCube(serverWorld, blockPos6) || blockState.isFullCube(serverWorld, blockPos6)) {
                        list.add(new BlockInfo(blockPos7, blockState, null));
                        deque.addLast(blockPos6);
                        continue;
                    }
                    list3.add(new BlockInfo(blockPos7, blockState, null));
                    deque.addFirst(blockPos6);
                }
            }
        }
        if (mode == Mode.MOVE) {
            for (BlockPos blockPos8 : deque) {
                BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos8);
                Clearable.clear(blockEntity2);
                serverWorld.setBlockState(blockPos8, Blocks.BARRIER.getDefaultState(), Block.NOTIFY_LISTENERS);
            }
            for (BlockPos blockPos8 : deque) {
                serverWorld.setBlockState(blockPos8, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            }
        }
        ArrayList<BlockInfo> list4 = Lists.newArrayList();
        list4.addAll(list);
        list4.addAll(list2);
        list4.addAll(list3);
        List<BlockInfo> list5 = Lists.reverse(list4);
        for (BlockInfo blockInfo : list5) {
            BlockEntity blockEntity3 = serverWorld2.getBlockEntity(blockInfo.pos);
            Clearable.clear(blockEntity3);
            serverWorld2.setBlockState(blockInfo.pos, Blocks.BARRIER.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
        int m = 0;
        for (BlockInfo blockInfo2 : list4) {
            if (!serverWorld2.setBlockState(blockInfo2.pos, blockInfo2.state, Block.NOTIFY_LISTENERS)) continue;
            ++m;
        }
        for (BlockInfo blockInfo2 : list2) {
            BlockEntity blockEntity4 = serverWorld2.getBlockEntity(blockInfo2.pos);
            if (blockInfo2.blockEntityNbt != null && blockEntity4 != null) {
                blockEntity4.readNbt(blockInfo2.blockEntityNbt);
                blockEntity4.markDirty();
            }
            serverWorld2.setBlockState(blockInfo2.pos, blockInfo2.state, Block.NOTIFY_LISTENERS);
        }
        for (BlockInfo blockInfo2 : list5) {
            serverWorld2.updateNeighbors(blockInfo2.pos, blockInfo2.state.getBlock());
        }
        ((WorldTickScheduler)serverWorld2.getBlockTickScheduler()).scheduleTicks(serverWorld.getBlockTickScheduler(), blockBox, blockPos5);
        if (m == 0) {
            throw FAILED_EXCEPTION.create();
        }
        source.sendFeedback(Text.translatable("commands.clone.success", m), true);
        return m;
    }

    @FunctionalInterface
    static interface ArgumentGetter<T, R> {
        public R apply(T var1) throws CommandSyntaxException;
    }

    record DimensionalPos(ServerWorld dimension, BlockPos position) {
    }

    static enum Mode {
        FORCE(true),
        MOVE(true),
        NORMAL(false);

        private final boolean allowsOverlap;

        private Mode(boolean allowsOverlap) {
            this.allowsOverlap = allowsOverlap;
        }

        public boolean allowsOverlap() {
            return this.allowsOverlap;
        }
    }

    static class BlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        @Nullable
        public final NbtCompound blockEntityNbt;

        public BlockInfo(BlockPos pos, BlockState state, @Nullable NbtCompound blockEntityNbt) {
            this.pos = pos;
            this.state = state;
            this.blockEntityNbt = blockEntityNbt;
        }
    }
}

