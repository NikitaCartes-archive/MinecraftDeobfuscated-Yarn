/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class StopCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("stop").requires(source -> source.hasPermissionLevel(4))).executes(context -> {
            ((ServerCommandSource)context.getSource()).sendFeedback(new TranslatableText("commands.stop.stopping"), true);
            ((ServerCommandSource)context.getSource()).getMinecraftServer().stop(false);
            return 1;
        }));
    }
}

