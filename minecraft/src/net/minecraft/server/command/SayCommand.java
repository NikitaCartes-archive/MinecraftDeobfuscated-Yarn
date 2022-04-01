package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class SayCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("say")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
					Text text = MessageArgumentType.getMessage(context, "message");
					Text text2 = new TranslatableText("chat.type.announcement", context.getSource().getDisplayName(), text);
					Entity entity = context.getSource().getEntity();
					if (entity != null) {
						context.getSource().getServer().getPlayerManager().broadcast(text2, MessageType.CHAT, entity.getUuid());
					} else {
						context.getSource().getServer().getPlayerManager().broadcast(text2, MessageType.SYSTEM, Util.NIL_UUID);
					}

					return 1;
				}))
		);
	}
}
