package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.ClickEvent;

public class SeedCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("seed")
				.requires(serverCommandSource -> serverCommandSource.getMinecraftServer().isSinglePlayer() || serverCommandSource.hasPermissionLevel(2))
				.executes(
					commandContext -> {
						long l = commandContext.getSource().method_9225().getSeed();
						TextComponent textComponent = TextFormatter.bracketed(
							new StringTextComponent(String.valueOf(l))
								.modifyStyle(
									style -> style.setColor(TextFormat.field_1060)
											.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(l)))
											.setInsertion(String.valueOf(l))
								)
						);
						commandContext.getSource().method_9226(new TranslatableTextComponent("commands.seed.success", textComponent), false);
						return (int)l;
					}
				)
		);
	}
}
