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
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FillCommand {
    private static final int MAX_BLOCKS = 32768;
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((maxCount, count) -> new TranslatableText("commands.fill.toobig", maxCount, count));
    static final BlockStateArgument AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.AIR.getDefaultState(), Collections.emptySet(), null);
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.fill.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("fill").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("to", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", BlockStateArgumentType.blockState(commandRegistryAccess)).executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")), BlockStateArgumentType.getBlockState(context, "block"), Mode.REPLACE, null))).then(((LiteralArgumentBuilder)CommandManager.literal("replace").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")), BlockStateArgumentType.getBlockState(context, "block"), Mode.REPLACE, null))).then(CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate(commandRegistryAccess)).executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")), BlockStateArgumentType.getBlockState(context, "block"), Mode.REPLACE, BlockPredicateArgumentType.getBlockPredicate(context, "filter")))))).then(CommandManager.literal("keep").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")), BlockStateArgumentType.getBlockState(context, "block"), Mode.REPLACE, pos -> pos.getWorld().isAir(pos.getBlockPos()))))).then(CommandManager.literal("outline").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")), BlockStateArgumentType.getBlockState(context, "block"), Mode.OUTLINE, null)))).then(CommandManager.literal("hollow").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")), BlockStateArgumentType.getBlockState(context, "block"), Mode.HOLLOW, null)))).then(CommandManager.literal("destroy").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")), BlockStateArgumentType.getBlockState(context, "block"), Mode.DESTROY, null)))))));
    }

    private static int execute(ServerCommandSource source, BlockBox range, BlockStateArgument block, Mode mode, @Nullable Predicate<CachedBlockPosition> filter) throws CommandSyntaxException {
        int i = range.getBlockCountX() * range.getBlockCountY() * range.getBlockCountZ();
        if (i > 32768) {
            throw TOO_BIG_EXCEPTION.create(32768, i);
        }
        ArrayList<BlockPos> list = Lists.newArrayList();
        ServerWorld serverWorld = source.getWorld();
        int j = 0;
        for (BlockPos blockPos : BlockPos.iterate(range.getMinX(), range.getMinY(), range.getMinZ(), range.getMaxX(), range.getMaxY(), range.getMaxZ())) {
            BlockStateArgument blockStateArgument;
            if (filter != null && !filter.test(new CachedBlockPosition(serverWorld, blockPos, true)) || (blockStateArgument = mode.filter.filter(range, blockPos, block, serverWorld)) == null) continue;
            BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
            Clearable.clear(blockEntity);
            if (!blockStateArgument.setBlockState(serverWorld, blockPos, Block.NOTIFY_LISTENERS)) continue;
            list.add(blockPos.toImmutable());
            ++j;
        }
        for (BlockPos blockPos : list) {
            Block block2 = serverWorld.getBlockState(blockPos).getBlock();
            serverWorld.updateNeighbors(blockPos, block2);
        }
        if (j == 0) {
            throw FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableText("commands.fill.success", j), true);
        return j;
    }

    static enum Mode {
        REPLACE((range, pos, block, world) -> block),
        OUTLINE((range, pos, block, world) -> {
            if (pos.getX() == range.getMinX() || pos.getX() == range.getMaxX() || pos.getY() == range.getMinY() || pos.getY() == range.getMaxY() || pos.getZ() == range.getMinZ() || pos.getZ() == range.getMaxZ()) {
                return block;
            }
            return null;
        }),
        HOLLOW((range, pos, block, world) -> {
            if (pos.getX() == range.getMinX() || pos.getX() == range.getMaxX() || pos.getY() == range.getMinY() || pos.getY() == range.getMaxY() || pos.getZ() == range.getMinZ() || pos.getZ() == range.getMaxZ()) {
                return block;
            }
            return AIR_BLOCK_ARGUMENT;
        }),
        DESTROY((range, pos, block, world) -> {
            world.breakBlock(pos, true);
            return block;
        });

        public final SetBlockCommand.Filter filter;

        private Mode(SetBlockCommand.Filter filter) {
            this.filter = filter;
        }
    }
}

