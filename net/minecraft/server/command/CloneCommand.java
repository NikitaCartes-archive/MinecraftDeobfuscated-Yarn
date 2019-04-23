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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import org.jetbrains.annotations.Nullable;

public class CloneCommand {
    private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.clone.overlap", new Object[0]));
    private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.clone.toobig", object, object2));
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.clone.failed", new Object[0]));
    public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = cachedBlockPosition -> !cachedBlockPosition.getBlockState().isAir();

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clone").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("begin", BlockPosArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("end", BlockPosArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.create()).executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.NORMAL))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("replace").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.NORMAL))).then(CommandManager.literal("force").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.FORCE)))).then(CommandManager.literal("move").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.MOVE)))).then(CommandManager.literal("normal").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), cachedBlockPosition -> true, Mode.NORMAL))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("masked").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), IS_AIR_PREDICATE, Mode.NORMAL))).then(CommandManager.literal("force").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), IS_AIR_PREDICATE, Mode.FORCE)))).then(CommandManager.literal("move").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), IS_AIR_PREDICATE, Mode.MOVE)))).then(CommandManager.literal("normal").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), IS_AIR_PREDICATE, Mode.NORMAL))))).then(CommandManager.literal("filtered").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("filter", BlockPredicateArgumentType.create()).executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"), Mode.NORMAL))).then(CommandManager.literal("force").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"), Mode.FORCE)))).then(CommandManager.literal("move").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"), Mode.MOVE)))).then(CommandManager.literal("normal").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"), Mode.NORMAL)))))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Predicate<CachedBlockPosition> predicate, Mode mode) throws CommandSyntaxException {
        MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(blockPos, blockPos2);
        BlockPos blockPos4 = blockPos3.add(mutableIntBoundingBox.getSize());
        MutableIntBoundingBox mutableIntBoundingBox2 = new MutableIntBoundingBox(blockPos3, blockPos4);
        if (!mode.allowsOverlap() && mutableIntBoundingBox2.intersects(mutableIntBoundingBox)) {
            throw OVERLAP_EXCEPTION.create();
        }
        int i = mutableIntBoundingBox.getBlockCountX() * mutableIntBoundingBox.getBlockCountY() * mutableIntBoundingBox.getBlockCountZ();
        if (i > 32768) {
            throw TOOBIG_EXCEPTION.create(32768, i);
        }
        ServerWorld serverWorld = serverCommandSource.getWorld();
        if (!serverWorld.isAreaLoaded(blockPos, blockPos2) || !serverWorld.isAreaLoaded(blockPos3, blockPos4)) {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
        }
        ArrayList<BlockInfo> list = Lists.newArrayList();
        ArrayList<BlockInfo> list2 = Lists.newArrayList();
        ArrayList<BlockInfo> list3 = Lists.newArrayList();
        LinkedList<BlockPos> deque = Lists.newLinkedList();
        BlockPos blockPos5 = new BlockPos(mutableIntBoundingBox2.minX - mutableIntBoundingBox.minX, mutableIntBoundingBox2.minY - mutableIntBoundingBox.minY, mutableIntBoundingBox2.minZ - mutableIntBoundingBox.minZ);
        for (int j = mutableIntBoundingBox.minZ; j <= mutableIntBoundingBox.maxZ; ++j) {
            for (int k = mutableIntBoundingBox.minY; k <= mutableIntBoundingBox.maxY; ++k) {
                for (int l = mutableIntBoundingBox.minX; l <= mutableIntBoundingBox.maxX; ++l) {
                    BlockPos blockPos6 = new BlockPos(l, k, j);
                    BlockPos blockPos7 = blockPos6.add(blockPos5);
                    CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos6, false);
                    BlockState blockState = cachedBlockPosition.getBlockState();
                    if (!predicate.test(cachedBlockPosition)) continue;
                    BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos6);
                    if (blockEntity != null) {
                        CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
                        list2.add(new BlockInfo(blockPos7, blockState, compoundTag));
                        deque.addLast(blockPos6);
                        continue;
                    }
                    if (blockState.isFullOpaque(serverWorld, blockPos6) || Block.isShapeFullCube(blockState.getCollisionShape(serverWorld, blockPos6))) {
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
                serverWorld.setBlockState(blockPos8, Blocks.BARRIER.getDefaultState(), 2);
            }
            for (BlockPos blockPos8 : deque) {
                serverWorld.setBlockState(blockPos8, Blocks.AIR.getDefaultState(), 3);
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
                blockEntity4.fromTag(blockInfo2.blockEntityTag);
                blockEntity4.markDirty();
            }
            serverWorld.setBlockState(blockInfo2.pos, blockInfo2.state, 2);
        }
        for (BlockInfo blockInfo2 : list5) {
            serverWorld.updateNeighbors(blockInfo2.pos, blockInfo2.state.getBlock());
        }
        serverWorld.method_14196().copyScheduledTicks(mutableIntBoundingBox, blockPos5);
        if (l == 0) {
            throw FAILED_EXCEPTION.create();
        }
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.clone.success", l), true);
        return l;
    }

    static class BlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        @Nullable
        public final CompoundTag blockEntityTag;

        public BlockInfo(BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
            this.pos = blockPos;
            this.state = blockState;
            this.blockEntityTag = compoundTag;
        }
    }

    static enum Mode {
        FORCE(true),
        MOVE(true),
        NORMAL(false);

        private final boolean allowsOverlap;

        private Mode(boolean bl) {
            this.allowsOverlap = bl;
        }

        public boolean allowsOverlap() {
            return this.allowsOverlap;
        }
    }
}

