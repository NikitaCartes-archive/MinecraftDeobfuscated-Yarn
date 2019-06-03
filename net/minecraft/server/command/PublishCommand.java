/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class PublishCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.publish.failed", new Object[0]));
    private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.publish.alreadyPublished", object));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("publish").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().isSinglePlayer() && serverCommandSource.hasPermissionLevel(4))).executes(commandContext -> PublishCommand.execute((ServerCommandSource)commandContext.getSource(), NetworkUtils.findLocalPort()))).then(CommandManager.argument("port", IntegerArgumentType.integer(0, 65535)).executes(commandContext -> PublishCommand.execute((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port")))));
    }

    private static int execute(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
        if (serverCommandSource.getMinecraftServer().isRemote()) {
            throw ALREADY_PUBLISHED_EXCEPTION.create(serverCommandSource.getMinecraftServer().getServerPort());
        }
        if (!serverCommandSource.getMinecraftServer().openToLan(serverCommandSource.getMinecraftServer().getDefaultGameMode(), false, i)) {
            throw FAILED_EXCEPTION.create();
        }
        serverCommandSource.sendFeedback(new TranslatableText("commands.publish.success", i), true);
        return i;
    }
}

