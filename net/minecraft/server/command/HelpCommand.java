/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Map;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class HelpCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.help.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("help").executes(commandContext -> {
            Map map = commandDispatcher.getSmartUsage(commandDispatcher.getRoot(), (ServerCommandSource)commandContext.getSource());
            for (String string : map.values()) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new LiteralText("/" + string), false);
            }
            return map.size();
        })).then(CommandManager.argument("command", StringArgumentType.greedyString()).executes(commandContext -> {
            ParseResults parseResults = commandDispatcher.parse(StringArgumentType.getString(commandContext, "command"), (ServerCommandSource)commandContext.getSource());
            if (parseResults.getContext().getNodes().isEmpty()) {
                throw FAILED_EXCEPTION.create();
            }
            Map map = commandDispatcher.getSmartUsage(Iterables.getLast(parseResults.getContext().getNodes()).getNode(), (ServerCommandSource)commandContext.getSource());
            for (String string : map.values()) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new LiteralText("/" + parseResults.getReader().getString() + " " + string), false);
            }
            return map.size();
        })));
    }
}

