/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamerule").requires(source -> source.hasPermissionLevel(2));
        GameRules.accept(new GameRules.Visitor(){

            @Override
            public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                literalArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(key.getName()).executes(context -> GameRuleCommand.executeQuery((ServerCommandSource)context.getSource(), key))).then(type.argument("value").executes(context -> GameRuleCommand.executeSet(context, key))));
            }
        });
        dispatcher.register(literalArgumentBuilder);
    }

    private static <T extends GameRules.Rule<T>> int executeSet(CommandContext<ServerCommandSource> context, GameRules.Key<T> key) {
        ServerCommandSource serverCommandSource = context.getSource();
        T rule = serverCommandSource.getMinecraftServer().getGameRules().get(key);
        ((GameRules.Rule)rule).set(context, "value");
        serverCommandSource.sendFeedback(new TranslatableText("commands.gamerule.set", key.getName(), ((GameRules.Rule)rule).toString()), true);
        return ((GameRules.Rule)rule).getCommandResult();
    }

    private static <T extends GameRules.Rule<T>> int executeQuery(ServerCommandSource source, GameRules.Key<T> key) {
        T rule = source.getMinecraftServer().getGameRules().get(key);
        source.sendFeedback(new TranslatableText("commands.gamerule.query", key.getName(), ((GameRules.Rule)rule).toString()), false);
        return ((GameRules.Rule)rule).getCommandResult();
    }
}

