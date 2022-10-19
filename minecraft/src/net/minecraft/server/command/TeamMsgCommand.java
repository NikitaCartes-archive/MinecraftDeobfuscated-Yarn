package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
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
			CommandManager.literal("teammsg")
				.then(
					CommandManager.argument("message", MessageArgumentType.message())
						.executes(
							context -> {
								ServerCommandSource serverCommandSource = context.getSource();
								Entity entity = serverCommandSource.getEntityOrThrow();
								Team team = (Team)entity.getScoreboardTeam();
								if (team == null) {
									throw NO_TEAM_EXCEPTION.create();
								} else {
									List<ServerPlayerEntity> list = serverCommandSource.getServer()
										.getPlayerManager()
										.getPlayerList()
										.stream()
										.filter(player -> player == entity || player.getScoreboardTeam() == team)
										.toList();
									if (!list.isEmpty()) {
										MessageArgumentType.getSignedMessage(context, "message", message -> execute(serverCommandSource, entity, team, list, message));
									}

									return list.size();
								}
							}
						)
				)
		);
		dispatcher.register(CommandManager.literal("tm").redirect(literalCommandNode));
	}

	private static void execute(ServerCommandSource source, Entity entity, Team team, List<ServerPlayerEntity> recipients, SignedMessage message) {
		Text text = team.getFormattedName().fillStyle(STYLE);
		MessageType.Parameters parameters = MessageType.params(MessageType.TEAM_MSG_COMMAND_INCOMING, source).withTargetName(text);
		MessageType.Parameters parameters2 = MessageType.params(MessageType.TEAM_MSG_COMMAND_OUTGOING, source).withTargetName(text);
		SentMessage sentMessage = SentMessage.of(message);
		boolean bl = false;

		for (ServerPlayerEntity serverPlayerEntity : recipients) {
			MessageType.Parameters parameters3 = serverPlayerEntity == entity ? parameters2 : parameters;
			boolean bl2 = source.shouldFilterText(serverPlayerEntity);
			serverPlayerEntity.sendChatMessage(sentMessage, bl2, parameters3);
			bl |= bl2 && message.isFullyFiltered();
		}

		if (bl) {
			source.sendMessage(PlayerManager.FILTERED_FULL_TEXT);
		}
	}
}
