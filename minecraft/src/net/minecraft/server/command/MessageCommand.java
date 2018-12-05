package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

public class MessageCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			ServerCommandManager.literal("msg")
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
						.then(
							ServerCommandManager.argument("message", MessageArgumentType.create())
								.executes(
									commandContext -> method_13462(
											commandContext.getSource(),
											EntityArgumentType.method_9312(commandContext, "targets"),
											MessageArgumentType.getMessageArgument(commandContext, "message")
										)
								)
						)
				)
		);
		commandDispatcher.register(ServerCommandManager.literal("tell").redirect(literalCommandNode));
		commandDispatcher.register(ServerCommandManager.literal("w").redirect(literalCommandNode));
	}

	private static int method_13462(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, TextComponent textComponent) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.appendCommandFeedback(
				new TranslatableTextComponent("commands.message.display.incoming", serverCommandSource.getDisplayName(), textComponent.clone())
					.applyFormat(new TextFormat[]{TextFormat.GRAY, TextFormat.ITALIC})
			);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.message.display.outgoing", serverPlayerEntity.getDisplayName(), textComponent.clone())
					.applyFormat(new TextFormat[]{TextFormat.GRAY, TextFormat.ITALIC}),
				false
			);
		}

		return collection.size();
	}
}
