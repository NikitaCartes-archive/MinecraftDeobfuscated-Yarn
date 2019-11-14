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
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        final LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamerule").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        GameRules.forEachType(new GameRules.RuleTypeConsumer(){

            @Override
            public <T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> ruleKey, GameRules.RuleType<T> ruleType) {
                literalArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(ruleKey.getName()).executes(commandContext -> GameRuleCommand.executeQuery((ServerCommandSource)commandContext.getSource(), ruleKey))).then(ruleType.argument("value").executes(commandContext -> GameRuleCommand.executeSet(commandContext, ruleKey))));
            }
        });
        commandDispatcher.register(literalArgumentBuilder);
    }

    private static <T extends GameRules.Rule<T>> int executeSet(CommandContext<ServerCommandSource> commandContext, GameRules.RuleKey<T> ruleKey) {
        ServerCommandSource serverCommandSource = commandContext.getSource();
        T rule = serverCommandSource.getMinecraftServer().getGameRules().get(ruleKey);
        ((GameRules.Rule)rule).set(commandContext, "value");
        serverCommandSource.sendFeedback(new TranslatableText("commands.gamerule.set", ruleKey.getName(), ((GameRules.Rule)rule).toString()), true);
        return ((GameRules.Rule)rule).getCommandResult();
    }

    private static <T extends GameRules.Rule<T>> int executeQuery(ServerCommandSource serverCommandSource, GameRules.RuleKey<T> ruleKey) {
        T rule = serverCommandSource.getMinecraftServer().getGameRules().get(ruleKey);
        serverCommandSource.sendFeedback(new TranslatableText("commands.gamerule.query", ruleKey.getName(), ((GameRules.Rule)rule).toString()), false);
        return ((GameRules.Rule)rule).getCommandResult();
    }
}

