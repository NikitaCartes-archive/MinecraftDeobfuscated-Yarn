/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class KillCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kill").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> KillCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets")))));
    }

    private static int execute(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection) {
        for (Entity entity : collection) {
            entity.kill();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.kill.success.single", collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.kill.success.multiple", collection.size()), true);
        }
        return collection.size();
    }
}

