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
					ServerCommandManager.argument("targets", EntityArgumentType.multiplePlayer())
						.then(
							ServerCommandManager.argument("message", MessageArgumentType.create())
								.executes(
									commandContext -> method_13462(
											commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets"), MessageArgumentType.method_9339(commandContext, "message")
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
			serverPlayerEntity.method_9203(
				new TranslatableTextComponent("commands.message.display.incoming", serverCommandSource.method_9223(), textComponent.copy())
					.applyFormat(new TextFormat[]{TextFormat.field_1080, TextFormat.field_1056})
			);
			serverCommandSource.method_9226(
				new TranslatableTextComponent("commands.message.display.outgoing", serverPlayerEntity.method_5476(), textComponent.copy())
					.applyFormat(new TextFormat[]{TextFormat.field_1080, TextFormat.field_1056}),
				false
			);
		}

		return collection.size();
	}
}
