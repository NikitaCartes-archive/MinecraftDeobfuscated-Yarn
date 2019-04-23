/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class TeammsgCommand {
    private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.teammsg.failed.noteam", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("teammsg").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", MessageArgumentType.create()).executes(commandContext -> TeammsgCommand.execute((ServerCommandSource)commandContext.getSource(), MessageArgumentType.getMessage(commandContext, "message")))));
        commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tm").redirect(literalCommandNode));
    }

    private static int execute(ServerCommandSource serverCommandSource, Component component) throws CommandSyntaxException {
        Entity entity = serverCommandSource.getEntityOrThrow();
        Team team = (Team)entity.getScoreboardTeam();
        if (team == null) {
            throw NO_TEAM_EXCEPTION.create();
        }
        Consumer<Style> consumer = style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.type.team.hover", new Object[0]))).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
        Component component2 = team.getFormattedName().modifyStyle(consumer);
        for (Component component3 : component2.getSiblings()) {
            component3.modifyStyle(consumer);
        }
        List<ServerPlayerEntity> list = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayerList();
        for (ServerPlayerEntity serverPlayerEntity : list) {
            if (serverPlayerEntity == entity) {
                serverPlayerEntity.sendMessage(new TranslatableComponent("chat.type.team.sent", component2, serverCommandSource.getDisplayName(), component.copy()));
                continue;
            }
            if (serverPlayerEntity.getScoreboardTeam() != team) continue;
            serverPlayerEntity.sendMessage(new TranslatableComponent("chat.type.team.text", component2, serverCommandSource.getDisplayName(), component.copy()));
        }
        return list.size();
    }
}

