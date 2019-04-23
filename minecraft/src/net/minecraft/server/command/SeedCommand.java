package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class SeedCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("seed")
				.requires(serverCommandSource -> serverCommandSource.getMinecraftServer().isSinglePlayer() || serverCommandSource.hasPermissionLevel(2))
				.executes(
					commandContext -> {
						long l = commandContext.getSource().getWorld().getSeed();
						Component component = Components.bracketed(
							new TextComponent(String.valueOf(l))
								.modifyStyle(
									style -> style.setColor(ChatFormat.field_1060)
											.setClickEvent(new ClickEvent(ClickEvent.Action.field_11745, String.valueOf(l)))
											.setInsertion(String.valueOf(l))
								)
						);
						commandContext.getSource().sendFeedback(new TranslatableComponent("commands.seed.success", component), false);
						return (int)l;
					}
				)
		);
	}
}
