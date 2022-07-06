package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Team;
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
						.executes(context -> execute(context.getSource(), MessageArgumentType.getSignedMessage(context, "message")))
				)
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
			MessageSender messageSender = source.getChatMessageSender().withTargetName(text);
			List<ServerPlayerEntity> list = source.getServer()
				.getPlayerManager()
				.getPlayerList()
				.stream()
				.filter(serverPlayerEntity -> serverPlayerEntity == entity || serverPlayerEntity.getScoreboardTeam() == team)
				.toList();
			if (list.isEmpty()) {
				return 0;
			} else {
				signedMessage.decorate(source)
					.thenAcceptAsync(
						decoratedMessage -> {
							for (ServerPlayerEntity serverPlayerEntity : list) {
								if (serverPlayerEntity == entity) {
									serverPlayerEntity.sendMessage(
										Text.translatable("chat.type.team.sent", text, source.getDisplayName(), ((SignedMessage)decoratedMessage.raw()).getContent())
									);
								} else {
									SignedMessage signedMessagex = (SignedMessage)decoratedMessage.getFilterableFor(source, serverPlayerEntity);
									if (signedMessagex != null) {
										serverPlayerEntity.sendChatMessage(signedMessagex, messageSender, MessageType.TEAM_MSG_COMMAND);
									}
								}
							}
						},
						source.getServer()
					);
				return list.size();
			}
		}
	}
}
