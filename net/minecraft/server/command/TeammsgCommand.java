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
import java.util.concurrent.Executor;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageSourceProfile;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class TeamMsgCommand {
    private static final Style STYLE = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.type.team.hover"))).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
    private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.teammsg.failed.noteam"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("teammsg").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
            MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage(context, "message");
            try {
                return TeamMsgCommand.execute((ServerCommandSource)context.getSource(), signedMessage);
            } catch (Exception exception) {
                signedMessage.sendHeader((ServerCommandSource)context.getSource());
                throw exception;
            }
        })));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tm").redirect(literalCommandNode));
    }

    private static int execute(ServerCommandSource source, MessageArgumentType.SignedMessage signedMessage) throws CommandSyntaxException {
        Entity entity = source.getEntityOrThrow();
        Team team = (Team)entity.getScoreboardTeam();
        if (team == null) {
            throw NO_TEAM_EXCEPTION.create();
        }
        MutableText text = team.getFormattedName().fillStyle(STYLE);
        MessageSourceProfile messageSourceProfile = source.getMessageSourceProfile();
        MessageType.Parameters parameters = MessageType.params(MessageType.TEAM_MSG_COMMAND, source).withTargetName(text);
        List<ServerPlayerEntity> list = source.getServer().getPlayerManager().getPlayerList().stream().filter(player -> player == entity || player.getScoreboardTeam() == team).toList();
        signedMessage.decorate(source).thenAcceptAsync(decoratedMessage -> {
            FilteredMessage<SentMessage> filteredMessage = SentMessage.of(decoratedMessage, messageSourceProfile);
            for (ServerPlayerEntity serverPlayerEntity : list) {
                if (serverPlayerEntity == entity) {
                    serverPlayerEntity.sendMessage(Text.translatable("chat.type.team.sent", text, source.getDisplayName(), ((SignedMessage)decoratedMessage.raw()).getContent()));
                    continue;
                }
                SentMessage sentMessage = filteredMessage.getFilterableFor(source, serverPlayerEntity);
                if (sentMessage == null) continue;
                serverPlayerEntity.sendChatMessage(sentMessage, parameters);
            }
            filteredMessage.raw().afterPacketsSent(source.getServer().getPlayerManager());
        }, (Executor)source.getServer());
        return list.size();
    }
}

