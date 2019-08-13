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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TeammsgCommand {
	private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.teammsg.failed.noteam"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			CommandManager.literal("teammsg")
				.then(
					CommandManager.argument("message", MessageArgumentType.message())
						.executes(commandContext -> execute(commandContext.getSource(), MessageArgumentType.getMessage(commandContext, "message")))
				)
		);
		commandDispatcher.register(CommandManager.literal("tm").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource serverCommandSource, Text text) throws CommandSyntaxException {
		Entity entity = serverCommandSource.getEntityOrThrow();
		Team team = (Team)entity.getScoreboardTeam();
		if (team == null) {
			throw NO_TEAM_EXCEPTION.create();
		} else {
			Consumer<Style> consumer = style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new TranslatableText("chat.type.team.hover")))
					.setClickEvent(new ClickEvent(ClickEvent.Action.field_11745, "/teammsg "));
			Text text2 = team.getFormattedName().styled(consumer);

			for (Text text3 : text2.getSiblings()) {
				text3.styled(consumer);
			}

			List<ServerPlayerEntity> list = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayerList();

			for (ServerPlayerEntity serverPlayerEntity : list) {
				if (serverPlayerEntity == entity) {
					serverPlayerEntity.sendMessage(new TranslatableText("chat.type.team.sent", text2, serverCommandSource.getDisplayName(), text.deepCopy()));
				} else if (serverPlayerEntity.getScoreboardTeam() == team) {
					serverPlayerEntity.sendMessage(new TranslatableText("chat.type.team.text", text2, serverCommandSource.getDisplayName(), text.deepCopy()));
				}
			}

			return list.size();
		}
	}
}
