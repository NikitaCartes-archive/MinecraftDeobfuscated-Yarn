package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class TeamMsgCommand {
	private static final Style STYLE = Style.EMPTY
		.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.type.team.hover")))
		.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
	private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.teammsg.failed.noteam"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
			CommandManager.literal("teammsg").then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
				MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage(context, "message");

				try {
					return execute(context.getSource(), signedMessage);
				} catch (Exception var3) {
					signedMessage.sendHeader(context.getSource());
					throw var3;
				}
			}))
		);
		dispatcher.register(CommandManager.literal("tm").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource source, MessageArgumentType.SignedMessage signedMessage) throws CommandSyntaxException {
		Entity entity = source.getEntityOrThrow();
		Team team = (Team)entity.getScoreboardTeam();
		if (team == null) {
			throw NO_TEAM_EXCEPTION.create();
		} else {
			Text text = team.getFormattedName().fillStyle(STYLE);
			MessageType.Parameters parameters = MessageType.params(MessageType.TEAM_MSG_COMMAND_INCOMING, source).withTargetName(text);
			MessageType.Parameters parameters2 = MessageType.params(MessageType.TEAM_MSG_COMMAND_OUTGOING, source).withTargetName(text);
			List<ServerPlayerEntity> list = source.getServer()
				.getPlayerManager()
				.getPlayerList()
				.stream()
				.filter(player -> player == entity || player.getScoreboardTeam() == team)
				.toList();
			signedMessage.decorate(source, message -> {
				SentMessage sentMessage = SentMessage.of(message);
				boolean bl = message.isFullyFiltered();
				boolean bl2 = false;

				for (ServerPlayerEntity serverPlayerEntity : list) {
					MessageType.Parameters parameters3 = serverPlayerEntity == entity ? parameters2 : parameters;
					boolean bl3 = source.shouldFilterText(serverPlayerEntity);
					serverPlayerEntity.sendChatMessage(sentMessage, bl3, parameters3);
					bl2 |= bl && bl3 && serverPlayerEntity != entity;
				}

				if (bl2) {
					source.sendMessage(PlayerManager.FILTERED_FULL_TEXT);
				}

				sentMessage.afterPacketsSent(source.getServer().getPlayerManager());
			});
			return list.size();
		}
	}
}
