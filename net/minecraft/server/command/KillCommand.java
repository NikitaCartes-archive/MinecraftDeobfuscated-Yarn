/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class KillCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kill").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> KillCommand.execute((ServerCommandSource)commandContext.getSource(), ImmutableList.of(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow())))).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> KillCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets")))));
    }

    private static int execute(ServerCommandSource source, Collection<? extends Entity> targets) {
        for (Entity entity : targets) {
            entity.kill();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.kill.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.kill.success.multiple", targets.size()), true);
        }
        return targets.size();
    }
}

