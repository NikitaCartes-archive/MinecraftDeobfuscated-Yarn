/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import org.jetbrains.annotations.Nullable;

public class SetBlockCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.setblock.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setblock").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", BlockStateArgumentType.blockState()).executes(commandContext -> SetBlockCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, null))).then(CommandManager.literal("destroy").executes(commandContext -> SetBlockCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.DESTROY, null)))).then(CommandManager.literal("keep").executes(commandContext -> SetBlockCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, cachedBlockPosition -> cachedBlockPosition.getWorld().isAir(cachedBlockPosition.getBlockPos()))))).then(CommandManager.literal("replace").executes(commandContext -> SetBlockCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, null))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, BlockPos blockPos, BlockStateArgument blockStateArgument, Mode mode, @Nullable Predicate<CachedBlockPosition> predicate) throws CommandSyntaxException {
        boolean bl;
        ServerWorld serverWorld = serverCommandSource.getWorld();
        if (predicate != null && !predicate.test(new CachedBlockPosition(serverWorld, blockPos, true))) {
            throw FAILED_EXCEPTION.create();
        }
        if (mode == Mode.DESTROY) {
            serverWorld.breakBlock(blockPos, true);
            bl = !blockStateArgument.getBlockState().isAir();
        } else {
            BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
            Clearable.clear(blockEntity);
            bl = true;
        }
        if (bl && !blockStateArgument.setBlockState(serverWorld, blockPos, 2)) {
            throw FAILED_EXCEPTION.create();
        }
        serverWorld.updateNeighbors(blockPos, blockStateArgument.getBlockState().getBlock());
        serverCommandSource.sendFeedback(new TranslatableText("commands.setblock.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
        return 1;
    }

    public static interface Filter {
        @Nullable
        public BlockStateArgument filter(MutableIntBoundingBox var1, BlockPos var2, BlockStateArgument var3, ServerWorld var4);
    }

    public static enum Mode {
        REPLACE,
        DESTROY;

    }
}

