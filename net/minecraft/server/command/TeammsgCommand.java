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
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TeammsgCommand {
    private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.teammsg.failed.noteam", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("teammsg").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", MessageArgumentType.create()).executes(commandContext -> TeammsgCommand.execute((ServerCommandSource)commandContext.getSource(), MessageArgumentType.getMessage(commandContext, "message")))));
        commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tm").redirect(literalCommandNode));
    }

    private static int execute(ServerCommandSource serverCommandSource, Text text) throws CommandSyntaxException {
        Entity entity = serverCommandSource.getEntityOrThrow();
        Team team = (Team)entity.getScoreboardTeam();
        if (team == null) {
            throw NO_TEAM_EXCEPTION.create();
        }
        Consumer<Style> consumer = style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.type.team.hover", new Object[0]))).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
        Text text2 = team.getFormattedName().styled(consumer);
        for (Text text3 : text2.getSiblings()) {
            text3.styled(consumer);
        }
        List<ServerPlayerEntity> list = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayerList();
        for (ServerPlayerEntity serverPlayerEntity : list) {
            if (serverPlayerEntity == entity) {
                serverPlayerEntity.sendMessage(new TranslatableText("chat.type.team.sent", text2, serverCommandSource.getDisplayName(), text.deepCopy()));
                continue;
            }
            if (serverPlayerEntity.getScoreboardTeam() != team) continue;
            serverPlayerEntity.sendMessage(new TranslatableText("chat.type.team.text", text2, serverCommandSource.getDisplayName(), text.deepCopy()));
        }
        return list.size();
    }
}

