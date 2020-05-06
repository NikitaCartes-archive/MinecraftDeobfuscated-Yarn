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
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TeamMsgCommand {
    private static final Style STYLE = Style.EMPTY.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.type.team.hover"))).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
    private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.teammsg.failed.noteam"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("teammsg").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", MessageArgumentType.message()).executes(commandContext -> TeamMsgCommand.execute((ServerCommandSource)commandContext.getSource(), MessageArgumentType.getMessage(commandContext, "message")))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tm").redirect(literalCommandNode));
    }

    private static int execute(ServerCommandSource source, Text message) throws CommandSyntaxException {
        Entity entity = source.getEntityOrThrow();
        Team team = (Team)entity.getScoreboardTeam();
        if (team == null) {
            throw NO_TEAM_EXCEPTION.create();
        }
        MutableText text = team.getFormattedName().fillStyle(STYLE);
        List<ServerPlayerEntity> list = source.getMinecraftServer().getPlayerManager().getPlayerList();
        for (ServerPlayerEntity serverPlayerEntity : list) {
            if (serverPlayerEntity == entity) {
                serverPlayerEntity.sendSystemMessage(new TranslatableText("chat.type.team.sent", text, source.getDisplayName(), message));
                continue;
            }
            if (serverPlayerEntity.getScoreboardTeam() != team) continue;
            serverPlayerEntity.sendSystemMessage(new TranslatableText("chat.type.team.text", text, source.getDisplayName(), message));
        }
        return list.size();
    }
}

