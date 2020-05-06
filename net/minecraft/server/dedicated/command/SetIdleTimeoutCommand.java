/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class SetIdleTimeoutCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setidletimeout").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("minutes", IntegerArgumentType.integer(0)).executes(commandContext -> SetIdleTimeoutCommand.execute((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "minutes")))));
    }

    private static int execute(ServerCommandSource source, int minutes) {
        source.getMinecraftServer().setPlayerIdleTimeout(minutes);
        source.sendFeedback(new TranslatableText("commands.setidletimeout.success", minutes), true);
        return minutes;
    }
}

