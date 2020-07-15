/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.argument.AngleArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setworldspawn").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> SetWorldSpawnCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition()), 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> SetWorldSpawnCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getBlockPos(commandContext, "pos"), 0.0f))).then(CommandManager.argument("angle", AngleArgumentType.angle()).executes(commandContext -> SetWorldSpawnCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getBlockPos(commandContext, "pos"), AngleArgumentType.getAngle(commandContext, "angle"))))));
    }

    private static int execute(ServerCommandSource source, BlockPos pos, float angle) {
        source.getWorld().setSpawnPos(pos, angle);
        source.sendFeedback(new TranslatableText("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ(), Float.valueOf(angle)), true);
        return 1;
    }
}

