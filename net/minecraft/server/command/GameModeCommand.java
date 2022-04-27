/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class GameModeCommand {
    public static final int REQUIRED_PERMISSION_LEVEL = 2;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamemode").requires(source -> source.hasPermissionLevel(2));
        for (GameMode gameMode : GameMode.values()) {
            literalArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(gameMode.getName()).executes(context -> GameModeCommand.execute(context, Collections.singleton(((ServerCommandSource)context.getSource()).getPlayer()), gameMode))).then(CommandManager.argument("target", EntityArgumentType.players()).executes(context -> GameModeCommand.execute(context, EntityArgumentType.getPlayers(context, "target"), gameMode))));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    private static void sendFeedback(ServerCommandSource source, ServerPlayerEntity player, GameMode gameMode) {
        MutableText text = Text.translatable("gameMode." + gameMode.getName());
        if (source.getEntity() == player) {
            source.sendFeedback(Text.translatable("commands.gamemode.success.self", text), true);
        } else {
            if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                player.sendMessage(Text.translatable("gameMode.changed", text));
            }
            source.sendFeedback(Text.translatable("commands.gamemode.success.other", player.getDisplayName(), text), true);
        }
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, GameMode gameMode) {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            if (!serverPlayerEntity.changeGameMode(gameMode)) continue;
            GameModeCommand.sendFeedback(context.getSource(), serverPlayerEntity, gameMode);
            ++i;
        }
        return i;
    }
}

