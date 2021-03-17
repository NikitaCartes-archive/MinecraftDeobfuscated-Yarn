/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class SaveOffCommand {
    private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.save.alreadyOff"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-off").requires(source -> source.hasPermissionLevel(4))).executes(context -> {
            ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
            boolean bl = false;
            for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
                if (serverWorld == null || serverWorld.savingDisabled) continue;
                serverWorld.savingDisabled = true;
                bl = true;
            }
            if (!bl) {
                throw ALREADY_OFF_EXCEPTION.create();
            }
            serverCommandSource.sendFeedback(new TranslatableText("commands.save.disabled"), true);
            return 1;
        }));
    }
}

