/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class GameModeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamemode").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (GameMode gameMode : GameMode.values()) {
            if (gameMode == GameMode.NOT_SET) continue;
            literalArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(gameMode.getName()).executes(commandContext -> GameModeCommand.execute(commandContext, Collections.singleton(((ServerCommandSource)commandContext.getSource()).getPlayer()), gameMode))).then(CommandManager.argument("target", EntityArgumentType.players()).executes(commandContext -> GameModeCommand.execute(commandContext, EntityArgumentType.getPlayers(commandContext, "target"), gameMode))));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    private static void setGameMode(ServerCommandSource source, ServerPlayerEntity player, GameMode gameMode) {
        TranslatableText text = new TranslatableText("gameMode." + gameMode.getName(), new Object[0]);
        if (source.getEntity() == player) {
            source.sendFeedback(new TranslatableText("commands.gamemode.success.self", text), true);
        } else {
            if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                player.sendMessage(new TranslatableText("gameMode.changed", text));
            }
            source.sendFeedback(new TranslatableText("commands.gamemode.success.other", player.getDisplayName(), text), true);
        }
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, GameMode gameMode) {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            if (serverPlayerEntity.interactionManager.getGameMode() == gameMode) continue;
            serverPlayerEntity.setGameMode(gameMode);
            GameModeCommand.setGameMode(context.getSource(), serverPlayerEntity, gameMode);
            ++i;
        }
        return i;
    }
}

