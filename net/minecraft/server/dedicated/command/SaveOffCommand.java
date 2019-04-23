/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class SaveOffCommand {
    private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.save.alreadyOff", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-off").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).executes(commandContext -> {
            ServerCommandSource serverCommandSource = (ServerCommandSource)commandContext.getSource();
            boolean bl = false;
            for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
                if (serverWorld == null || serverWorld.savingDisabled) continue;
                serverWorld.savingDisabled = true;
                bl = true;
            }
            if (!bl) {
                throw ALREADY_OFF_EXCEPTION.create();
            }
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.save.disabled", new Object[0]), true);
            return 1;
        }));
    }
}

