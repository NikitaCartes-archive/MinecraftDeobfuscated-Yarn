/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class DefaultGameModeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("defaultgamemode").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (GameMode gameMode : GameMode.values()) {
            if (gameMode == GameMode.INVALID) continue;
            literalArgumentBuilder.then(CommandManager.literal(gameMode.getName()).executes(commandContext -> DefaultGameModeCommand.execute((ServerCommandSource)commandContext.getSource(), gameMode)));
        }
        commandDispatcher.register(literalArgumentBuilder);
    }

    private static int execute(ServerCommandSource serverCommandSource, GameMode gameMode) {
        int i = 0;
        MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
        minecraftServer.setDefaultGameMode(gameMode);
        if (minecraftServer.shouldForceGameMode()) {
            for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
                if (serverPlayerEntity.interactionManager.getGameMode() == gameMode) continue;
                serverPlayerEntity.setGameMode(gameMode);
                ++i;
            }
        }
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.defaultgamemode.success", gameMode.getTextComponent()), true);
        return i;
    }
}

