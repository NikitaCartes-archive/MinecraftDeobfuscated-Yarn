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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import org.jetbrains.annotations.Nullable;

public class FillCommand {
    private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.fill.toobig", object, object2));
    private static final BlockStateArgument AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.AIR.getDefaultState(), Collections.emptySet(), null);
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.fill.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("fill").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("from", BlockPosArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("to", BlockPosArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", BlockStateArgumentType.create()).executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, null))).then(((LiteralArgumentBuilder)CommandManager.literal("replace").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, null))).then(CommandManager.argument("filter", BlockPredicateArgumentType.create()).executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter")))))).then(CommandManager.literal("keep").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, cachedBlockPosition -> cachedBlockPosition.getWorld().isAir(cachedBlockPosition.getBlockPos()))))).then(CommandManager.literal("outline").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.OUTLINE, null)))).then(CommandManager.literal("hollow").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.HOLLOW, null)))).then(CommandManager.literal("destroy").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.DESTROY, null)))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, MutableIntBoundingBox mutableIntBoundingBox, BlockStateArgument blockStateArgument, Mode mode, @Nullable Predicate<CachedBlockPosition> predicate) throws CommandSyntaxException {
        int i = mutableIntBoundingBox.getBlockCountX() * mutableIntBoundingBox.getBlockCountY() * mutableIntBoundingBox.getBlockCountZ();
        if (i > 32768) {
            throw TOOBIG_EXCEPTION.create(32768, i);
        }
        ArrayList<BlockPos> list = Lists.newArrayList();
        ServerWorld serverWorld = serverCommandSource.getWorld();
        int j = 0;
        for (BlockPos blockPos : BlockPos.iterate(mutableIntBoundingBox.minX, mutableIntBoundingBox.minY, mutableIntBoundingBox.minZ, mutableIntBoundingBox.maxX, mutableIntBoundingBox.maxY, mutableIntBoundingBox.maxZ)) {
            BlockStateArgument blockStateArgument2;
            if (predicate != null && !predicate.test(new CachedBlockPosition(serverWorld, blockPos, true)) || (blockStateArgument2 = mode.filter.filter(mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld)) == null) continue;
            BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
            Clearable.clear(blockEntity);
            if (!blockStateArgument2.setBlockState(serverWorld, blockPos, 2)) continue;
            list.add(blockPos.toImmutable());
            ++j;
        }
        for (BlockPos blockPos : list) {
            Block block = serverWorld.getBlockState(blockPos).getBlock();
            serverWorld.updateNeighbors(blockPos, block);
        }
        if (j == 0) {
            throw FAILED_EXCEPTION.create();
        }
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.fill.success", j), true);
        return j;
    }

    static enum Mode {
        REPLACE((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> blockStateArgument),
        OUTLINE((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> {
            if (blockPos.getX() == mutableIntBoundingBox.minX || blockPos.getX() == mutableIntBoundingBox.maxX || blockPos.getY() == mutableIntBoundingBox.minY || blockPos.getY() == mutableIntBoundingBox.maxY || blockPos.getZ() == mutableIntBoundingBox.minZ || blockPos.getZ() == mutableIntBoundingBox.maxZ) {
                return blockStateArgument;
            }
            return null;
        }),
        HOLLOW((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> {
            if (blockPos.getX() == mutableIntBoundingBox.minX || blockPos.getX() == mutableIntBoundingBox.maxX || blockPos.getY() == mutableIntBoundingBox.minY || blockPos.getY() == mutableIntBoundingBox.maxY || blockPos.getZ() == mutableIntBoundingBox.minZ || blockPos.getZ() == mutableIntBoundingBox.maxZ) {
                return blockStateArgument;
            }
            return AIR_BLOCK_ARGUMENT;
        }),
        DESTROY((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> {
            serverWorld.breakBlock(blockPos, true);
            return blockStateArgument;
        });

        public final SetBlockCommand.Filter filter;

        private Mode(SetBlockCommand.Filter filter) {
            this.filter = filter;
        }
    }
}

