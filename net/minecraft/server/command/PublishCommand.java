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
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.publish.failed"));
    private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.publish.alreadyPublished", object));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("publish").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).executes(commandContext -> PublishCommand.execute((ServerCommandSource)commandContext.getSource(), NetworkUtils.findLocalPort()))).then(CommandManager.argument("port", IntegerArgumentType.integer(0, 65535)).executes(commandContext -> PublishCommand.execute((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port")))));
    }

    private static int execute(ServerCommandSource source, int port) throws CommandSyntaxException {
        if (source.getMinecraftServer().isRemote()) {
            throw ALREADY_PUBLISHED_EXCEPTION.create(source.getMinecraftServer().getServerPort());
        }
        if (!source.getMinecraftServer().openToLan(null, false, port)) {
            throw FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableText("commands.publish.success", port), true);
        return port;
    }
}

