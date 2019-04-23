package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraft.server.network.ServerPlayerEntity;

public class TeammsgCommand {
	private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.teammsg.failed.noteam"));

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

	private static int execute(ServerCommandSource serverCommandSource, Component component) throws CommandSyntaxException {
		Entity entity = serverCommandSource.getEntityOrThrow();
		Team team = (Team)entity.getScoreboardTeam();
		if (team == null) {
			throw NO_TEAM_EXCEPTION.create();
		} else {
			Consumer<Style> consumer = style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new TranslatableComponent("chat.type.team.hover")))
					.setClickEvent(new ClickEvent(ClickEvent.Action.field_11745, "/teammsg "));
			Component component2 = team.getFormattedName().modifyStyle(consumer);

			for (Component component3 : component2.getSiblings()) {
				component3.modifyStyle(consumer);
			}

			List<ServerPlayerEntity> list = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayerList();

			for (ServerPlayerEntity serverPlayerEntity : list) {
				if (serverPlayerEntity == entity) {
					serverPlayerEntity.sendMessage(new TranslatableComponent("chat.type.team.sent", component2, serverCommandSource.getDisplayName(), component.copy()));
				} else if (serverPlayerEntity.getScoreboardTeam() == team) {
					serverPlayerEntity.sendMessage(new TranslatableComponent("chat.type.team.text", component2, serverCommandSource.getDisplayName(), component.copy()));
				}
			}

			return list.size();
		}
	}
}
