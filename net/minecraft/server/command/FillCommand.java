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
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateArgumentType;
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
    private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.fill.toobig", object, object2));
    private static final BlockStateArgument AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.AIR.getDefaultState(), Collections.emptySet(), null);
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.fill.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("fill").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("to", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", BlockStateArgumentType.blockState()).executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, null))).then(((LiteralArgumentBuilder)CommandManager.literal("replace").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, null))).then(CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate()).executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter")))))).then(CommandManager.literal("keep").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, cachedBlockPosition -> cachedBlockPosition.getWorld().isAir(cachedBlockPosition.getBlockPos()))))).then(CommandManager.literal("outline").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.OUTLINE, null)))).then(CommandManager.literal("hollow").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.HOLLOW, null)))).then(CommandManager.literal("destroy").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.DESTROY, null)))))));
    }

    private static int execute(ServerCommandSource source, BlockBox range, BlockStateArgument block, Mode mode, @Nullable Predicate<CachedBlockPosition> filter) throws CommandSyntaxException {
        int i = range.getBlockCountX() * range.getBlockCountY() * range.getBlockCountZ();
        if (i > 32768) {
            throw TOOBIG_EXCEPTION.create(32768, i);
        }
        ArrayList<BlockPos> list = Lists.newArrayList();
        ServerWorld serverWorld = source.getWorld();
        int j = 0;
        for (BlockPos blockPos : BlockPos.iterate(range.minX, range.minY, range.minZ, range.maxX, range.maxY, range.maxZ)) {
            BlockStateArgument blockStateArgument;
            if (filter != null && !filter.test(new CachedBlockPosition(serverWorld, blockPos, true)) || (blockStateArgument = mode.filter.filter(range, blockPos, block, serverWorld)) == null) continue;
            BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
            Clearable.clear(blockEntity);
            if (!blockStateArgument.setBlockState(serverWorld, blockPos, 2)) continue;
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
        REPLACE((blockBox, blockPos, blockStateArgument, serverWorld) -> blockStateArgument),
        OUTLINE((blockBox, blockPos, blockStateArgument, serverWorld) -> {
            if (blockPos.getX() == blockBox.minX || blockPos.getX() == blockBox.maxX || blockPos.getY() == blockBox.minY || blockPos.getY() == blockBox.maxY || blockPos.getZ() == blockBox.minZ || blockPos.getZ() == blockBox.maxZ) {
                return blockStateArgument;
            }
            return null;
        }),
        HOLLOW((blockBox, blockPos, blockStateArgument, serverWorld) -> {
            if (blockPos.getX() == blockBox.minX || blockPos.getX() == blockBox.maxX || blockPos.getY() == blockBox.minY || blockPos.getY() == blockBox.maxY || blockPos.getZ() == blockBox.minZ || blockPos.getZ() == blockBox.maxZ) {
                return blockStateArgument;
            }
            return AIR_BLOCK_ARGUMENT;
        }),
        DESTROY((blockBox, blockPos, blockStateArgument, serverWorld) -> {
            serverWorld.breakBlock(blockPos, true);
            return blockStateArgument;
        });

        public final SetBlockCommand.Filter filter;

        private Mode(SetBlockCommand.Filter filter) {
            this.filter = filter;
        }
    }
}

