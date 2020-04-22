package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TeammsgCommand {
	private static final Style field_24380 = Style.EMPTY
		.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.type.team.hover")))
		.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
	private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.teammsg.failed.noteam"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
			CommandManager.literal("teammsg")
				.then(
					CommandManager.argument("message", MessageArgumentType.message())
						.executes(commandContext -> execute(commandContext.getSource(), MessageArgumentType.getMessage(commandContext, "message")))
				)
		);
		dispatcher.register(CommandManager.literal("tm").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource source, Text message) throws CommandSyntaxException {
		Entity entity = source.getEntityOrThrow();
		Team team = (Team)entity.getScoreboardTeam();
		if (team == null) {
			throw NO_TEAM_EXCEPTION.create();
		} else {
			Text text = team.getFormattedName().fillStyle(field_24380);
			List<ServerPlayerEntity> list = source.getMinecraftServer().getPlayerManager().getPlayerList();

			for (ServerPlayerEntity serverPlayerEntity : list) {
				if (serverPlayerEntity == entity) {
					serverPlayerEntity.sendSystemMessage(new TranslatableText("chat.type.team.sent", text, source.getDisplayName(), message));
				} else if (serverPlayerEntity.getScoreboardTeam() == team) {
					serverPlayerEntity.sendSystemMessage(new TranslatableText("chat.type.team.text", text, source.getDisplayName(), message));
				}
			}

			return list.size();
		}
	}
}
