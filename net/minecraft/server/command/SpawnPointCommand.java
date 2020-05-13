/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class SpawnPointCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spawnpoint").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> SpawnPointCommand.execute((ServerCommandSource)commandContext.getSource(), Collections.singleton(((ServerCommandSource)commandContext.getSource()).getPlayer()), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> SpawnPointCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> SpawnPointCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), BlockPosArgumentType.getBlockPos(commandContext, "pos"))))));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, BlockPos pos) {
        DimensionType dimensionType = source.getWorld().method_27983();
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.setSpawnPoint(dimensionType, pos, true, false);
        }
        String string = DimensionType.getId(dimensionType).toString();
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.spawnpoint.success.single", pos.getX(), pos.getY(), pos.getZ(), string, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.spawnpoint.success.multiple", pos.getX(), pos.getY(), pos.getZ(), string, targets.size()), true);
        }
        return targets.size();
    }
}

