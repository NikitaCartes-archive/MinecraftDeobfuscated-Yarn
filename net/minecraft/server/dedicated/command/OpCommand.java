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
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class OpCommand {
    private static final SimpleCommandExceptionType ALREADY_OPPED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.op.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("op").requires(source -> source.hasPermissionLevel(3))).then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> {
            PlayerManager playerManager = ((ServerCommandSource)context.getSource()).getMinecraftServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter(player -> !playerManager.isOperator(player.getGameProfile())).map(player -> player.getGameProfile().getName()), builder);
        }).executes(context -> OpCommand.op((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets")))));
    }

    private static int op(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        PlayerManager playerManager = source.getMinecraftServer().getPlayerManager();
        int i = 0;
        for (GameProfile gameProfile : targets) {
            if (playerManager.isOperator(gameProfile)) continue;
            playerManager.addToOperators(gameProfile);
            ++i;
            source.sendFeedback(new TranslatableText("commands.op.success", targets.iterator().next().getName()), true);
        }
        if (i == 0) {
            throw ALREADY_OPPED_EXCEPTION.create();
        }
        return i;
    }
}

