/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CloneCommand {
    private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.overlap"));
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.clone.toobig", object, object2));
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.failed"));
    public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = cachedBlockPosition -> !cachedBlockPosition.getBlockState().isAir();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clone").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("begin", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("end", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.blockPos()).executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.NORMAL))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("replace").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.NORMAL))).then(CommandManager.literal("force").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.FORCE)))).then(CommandManager.literal("move").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.MOVE)))).then(CommandManager.literal("normal").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.NORMAL))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("masked").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), IS_AIR_PREDICATE, Mode.NORMAL))).then(CommandManager.literal("force").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), IS_AIR_PREDICATE, Mode.FORCE)))).then(CommandManager.literal("move").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), IS_AIR_PREDICATE, Mode.MOVE)))).then(CommandManager.literal("normal").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), IS_AIR_PREDICATE, Mode.NORMAL))))).then(CommandManager.literal("filtered").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate()).executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"), Mode.NORMAL))).then(CommandManager.literal("force").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"), Mode.FORCE)))).then(CommandManager.literal("move").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"), Mode.MOVE)))).then(CommandManager.literal("normal").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"), Mode.NORMAL)))))))));
    }

    private static int execute(ServerCommandSource source, BlockPos begin, BlockPos end, BlockPos destination, Predicate<CachedBlockPosition> filter, Mode mode) throws CommandSyntaxException {
        BlockBox blockBox = new BlockBox(begin, end);
        BlockPos blockPos = destination.add(blockBox.getDimensions());
        BlockBox blockBox2 = new BlockBox(destination, blockPos);
        if (!mode.allowsOverlap() && blockBox2.intersects(blockBox)) {
            throw OVERLAP_EXCEPTION.create();
        }
        int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
        if (i > 32768) {
            throw TOO_BIG_EXCEPTION.create(32768, i);
        }
        ServerWorld serverWorld = source.getWorld();
        if (!serverWorld.isRegionLoaded(begin, end) || !serverWorld.isRegionLoaded(destination, blockPos)) {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
        }
        ArrayList<BlockInfo> list = Lists.newArrayList();
        ArrayList<BlockInfo> list2 = Lists.newArrayList();
        ArrayList<BlockInfo> list3 = Lists.newArrayList();
        LinkedList<BlockPos> deque = Lists.newLinkedList();
        BlockPos blockPos2 = new BlockPos(blockBox2.minX - blockBox.minX, blockBox2.minY - blockBox.minY, blockBox2.minZ - blockBox.minZ);
        for (int j = blockBox.minZ; j <= blockBox.maxZ; ++j) {
            for (int k = blockBox.minY; k <= blockBox.maxY; ++k) {
                for (int l = blockBox.minX; l <= blockBox.maxX; ++l) {
                    BlockPos blockPos3 = new BlockPos(l, k, j);
                    BlockPos blockPos4 = blockPos3.add(blockPos2);
                    CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos3, false);
                    BlockState blockState = cachedBlockPosition.getBlockState();
                    if (!filter.test(cachedBlockPosition)) continue;
                    BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos3);
                    if (blockEntity != null) {
                        CompoundTag compoundTag = blockEntity.writeNbt(new CompoundTag());
                        list2.add(new BlockInfo(blockPos4, blockState, compoundTag));
                        deque.addLast(blockPos3);
                        continue;
                    }
                    if (blockState.isOpaqueFullCube(serverWorld, blockPos3) || blockState.isFullCube(serverWorld, blockPos3)) {
                        list.add(new BlockInfo(blockPos4, blockState, null));
                        deque.addLast(blockPos3);
                        continue;
                    }
                    list3.add(new BlockInfo(blockPos4, blockState, null));
                    deque.addFirst(blockPos3);
                }
            }
        }
        if (mode == Mode.MOVE) {
            for (BlockPos blockPos5 : deque) {
                BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos5);
                Clearable.clear(blockEntity2);
                serverWorld.setBlockState(blockPos5, Blocks.BARRIER.getDefaultState(), 2);
            }
            for (BlockPos blockPos5 : deque) {
                serverWorld.setBlockState(blockPos5, Blocks.AIR.getDefaultState(), 3);
            }
        }
        ArrayList<BlockInfo> list4 = Lists.newArrayList();
        list4.addAll(list);
        list4.addAll(list2);
        list4.addAll(list3);
        List<BlockInfo> list5 = Lists.reverse(list4);
        for (BlockInfo blockInfo : list5) {
            BlockEntity blockEntity3 = serverWorld.getBlockEntity(blockInfo.pos);
            Clearable.clear(blockEntity3);
            serverWorld.setBlockState(blockInfo.pos, Blocks.BARRIER.getDefaultState(), 2);
        }
        int l = 0;
        for (BlockInfo blockInfo2 : list4) {
            if (!serverWorld.setBlockState(blockInfo2.pos, blockInfo2.state, 2)) continue;
            ++l;
        }
        for (BlockInfo blockInfo2 : list2) {
            BlockEntity blockEntity4 = serverWorld.getBlockEntity(blockInfo2.pos);
            if (blockInfo2.blockEntityTag != null && blockEntity4 != null) {
                blockInfo2.blockEntityTag.putInt("x", blockInfo2.pos.getX());
                blockInfo2.blockEntityTag.putInt("y", blockInfo2.pos.getY());
                blockInfo2.blockEntityTag.putInt("z", blockInfo2.pos.getZ());
                blockEntity4.readNbt(blockInfo2.blockEntityTag);
                blockEntity4.markDirty();
            }
            serverWorld.setBlockState(blockInfo2.pos, blockInfo2.state, 2);
        }
        for (BlockInfo blockInfo2 : list5) {
            serverWorld.updateNeighbors(blockInfo2.pos, blockInfo2.state.getBlock());
        }
        ((ServerTickScheduler)serverWorld.getBlockTickScheduler()).copyScheduledTicks(blockBox, blockPos2);
        if (l == 0) {
            throw FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableText("commands.clone.success", l), true);
        return l;
    }

    static class BlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        @Nullable
        public final CompoundTag blockEntityTag;

        public BlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag blockEntityTag) {
            this.pos = pos;
            this.state = state;
            this.blockEntityTag = blockEntityTag;
        }
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
}

