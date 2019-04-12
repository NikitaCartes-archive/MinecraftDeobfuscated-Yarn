package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.event.HoverEvent;

public class TeammsgCommand {
	private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.teammsg.failed.noteam")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			CommandManager.literal("teammsg")
				.then(
					CommandManager.argument("message", MessageArgumentType.create())
						.executes(commandContext -> execute(commandContext.getSource(), MessageArgumentType.getMessage(commandContext, "message")))
				)
		);
		commandDispatcher.register(CommandManager.literal("tm").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource serverCommandSource, TextComponent textComponent) throws CommandSyntaxException {
		Entity entity = serverCommandSource.getEntityOrThrow();
		Team team = (Team)entity.getScoreboardTeam();
		if (team == null) {
			throw NO_TEAM_EXCEPTION.create();
		} else {
			Consumer<Style> consumer = style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableTextComponent("chat.type.team.hover")))
					.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
			TextComponent textComponent2 = team.getFormattedName().modifyStyle(consumer);

			for (TextComponent textComponent3 : textComponent2.getSiblings()) {
				textComponent3.modifyStyle(consumer);
			}

			List<ServerPlayerEntity> list = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayerList();

			for (ServerPlayerEntity serverPlayerEntity : list) {
				if (serverPlayerEntity == entity) {
					serverPlayerEntity.appendCommandFeedback(
						new TranslatableTextComponent("chat.type.team.sent", textComponent2, serverCommandSource.getDisplayName(), textComponent.copy())
					);
				} else if (serverPlayerEntity.getScoreboardTeam() == team) {
					serverPlayerEntity.appendCommandFeedback(
						new TranslatableTextComponent("chat.type.team.text", textComponent2, serverCommandSource.getDisplayName(), textComponent.copy())
					);
				}
			}

			return list.size();
		}
	}
}
