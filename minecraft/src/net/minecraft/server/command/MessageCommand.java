package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class MessageCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
			CommandManager.literal("msg")
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("message", MessageArgumentType.message())
								.executes(
									commandContext -> execute(
											commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), MessageArgumentType.getMessage(commandContext, "message")
										)
								)
						)
				)
		);
		dispatcher.register(CommandManager.literal("tell").redirect(literalCommandNode));
		dispatcher.register(CommandManager.literal("w").redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text message) {
		UUID uUID = source.getEntity() == null ? Util.NIL_UUID : source.getEntity().getUuid();
		Entity entity = source.getEntity();
		Consumer<Text> consumer;
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
			consumer = text2 -> serverPlayerEntity.sendSystemMessage(
					new TranslatableText("commands.message.display.outgoing", text2, message).formatted(new Formatting[]{Formatting.field_1080, Formatting.field_1056}),
					serverPlayerEntity.getUuid()
				);
		} else {
			consumer = text2 -> source.sendFeedback(
					new TranslatableText("commands.message.display.outgoing", text2, message).formatted(new Formatting[]{Formatting.field_1080, Formatting.field_1056}), false
				);
		}

		for (ServerPlayerEntity serverPlayerEntity2 : targets) {
			consumer.accept(serverPlayerEntity2.getDisplayName());
			serverPlayerEntity2.sendSystemMessage(
				new TranslatableText("commands.message.display.incoming", source.getDisplayName(), message)
					.formatted(new Formatting[]{Formatting.field_1080, Formatting.field_1056}),
				uUID
			);
		}

		return targets.size();
	}
}
