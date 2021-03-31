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
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SetBlockCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.setblock.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setblock").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", BlockStateArgumentType.blockState()).executes(context -> SetBlockCommand.execute((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), BlockStateArgumentType.getBlockState(context, "block"), Mode.REPLACE, null))).then(CommandManager.literal("destroy").executes(context -> SetBlockCommand.execute((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), BlockStateArgumentType.getBlockState(context, "block"), Mode.DESTROY, null)))).then(CommandManager.literal("keep").executes(context -> SetBlockCommand.execute((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), BlockStateArgumentType.getBlockState(context, "block"), Mode.REPLACE, pos -> pos.getWorld().isAir(pos.getBlockPos()))))).then(CommandManager.literal("replace").executes(context -> SetBlockCommand.execute((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), BlockStateArgumentType.getBlockState(context, "block"), Mode.REPLACE, null))))));
    }

    private static int execute(ServerCommandSource source, BlockPos pos, BlockStateArgument block, Mode mode, @Nullable Predicate<CachedBlockPosition> condition) throws CommandSyntaxException {
        boolean bl;
        ServerWorld serverWorld = source.getWorld();
        if (condition != null && !condition.test(new CachedBlockPosition(serverWorld, pos, true))) {
            throw FAILED_EXCEPTION.create();
        }
        if (mode == Mode.DESTROY) {
            serverWorld.breakBlock(pos, true);
            bl = !block.getBlockState().isAir() || !serverWorld.getBlockState(pos).isAir();
        } else {
            BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
            Clearable.clear(blockEntity);
            bl = true;
        }
        if (bl && !block.setBlockState(serverWorld, pos, Block.NOTIFY_LISTENERS)) {
            throw FAILED_EXCEPTION.create();
        }
        serverWorld.updateNeighbors(pos, block.getBlockState().getBlock());
        source.sendFeedback(new TranslatableText("commands.setblock.success", pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }

    public static interface Filter {
        @Nullable
        public BlockStateArgument filter(BlockBox var1, BlockPos var2, BlockStateArgument var3, ServerWorld var4);
    }

    public static enum Mode {
        REPLACE,
        DESTROY;

    }
}

