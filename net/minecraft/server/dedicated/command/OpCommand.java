/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

public class OpCommand {
    private static final SimpleCommandExceptionType ALREADY_OPPED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.op.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("op").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("targets", GameProfileArgumentType.create()).suggests((commandContext, suggestionsBuilder) -> {
            PlayerManager playerManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter(serverPlayerEntity -> !playerManager.isOperator(serverPlayerEntity.getGameProfile())).map(serverPlayerEntity -> serverPlayerEntity.getGameProfile().getName()), suggestionsBuilder);
        }).executes(commandContext -> OpCommand.op((ServerCommandSource)commandContext.getSource(), GameProfileArgumentType.getProfileArgument(commandContext, "targets")))));
    }

    private static int op(ServerCommandSource serverCommandSource, Collection<GameProfile> collection) throws CommandSyntaxException {
        PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
        int i = 0;
        for (GameProfile gameProfile : collection) {
            if (playerManager.isOperator(gameProfile)) continue;
            playerManager.addToOperators(gameProfile);
            ++i;
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.op.success", collection.iterator().next().getName()), true);
        }
        if (i == 0) {
            throw ALREADY_OPPED_EXCEPTION.create();
        }
        return i;
    }
}

