package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.StopSoundClientPacket;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ResourceLocationArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

public class StopSoundCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		RequiredArgumentBuilder<ServerCommandSource, EntitySelector> requiredArgumentBuilder = ServerCommandManager.argument(
				"targets", EntityArgumentType.multiplePlayer()
			)
			.executes(commandContext -> method_13685(commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets"), null, null))
			.then(
				ServerCommandManager.literal("*")
					.then(
						ServerCommandManager.argument("sound", ResourceLocationArgumentType.create())
							.suggests(SuggestionProviders.AVAILABLE_SOUNDS)
							.executes(
								commandContext -> method_13685(
										commandContext.getSource(),
										EntityArgumentType.method_9312(commandContext, "targets"),
										null,
										ResourceLocationArgumentType.getIdentifierArgument(commandContext, "sound")
									)
							)
					)
			);

		for (SoundCategory soundCategory : SoundCategory.values()) {
			requiredArgumentBuilder.then(
				ServerCommandManager.literal(soundCategory.getName())
					.executes(commandContext -> method_13685(commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets"), soundCategory, null))
					.then(
						ServerCommandManager.argument("sound", ResourceLocationArgumentType.create())
							.suggests(SuggestionProviders.AVAILABLE_SOUNDS)
							.executes(
								commandContext -> method_13685(
										commandContext.getSource(),
										EntityArgumentType.method_9312(commandContext, "targets"),
										soundCategory,
										ResourceLocationArgumentType.getIdentifierArgument(commandContext, "sound")
									)
							)
					)
			);
		}

		commandDispatcher.register(
			ServerCommandManager.literal("stopsound").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(requiredArgumentBuilder)
		);
	}

	private static int method_13685(
		ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, @Nullable SoundCategory soundCategory, @Nullable Identifier identifier
	) {
		StopSoundClientPacket stopSoundClientPacket = new StopSoundClientPacket(identifier, soundCategory);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(stopSoundClientPacket);
		}

		if (soundCategory != null) {
			if (identifier != null) {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.stopsound.success.source.sound", identifier, soundCategory.getName()), true);
			} else {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.stopsound.success.source.any", soundCategory.getName()), true);
			}
		} else if (identifier != null) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.stopsound.success.sourceless.sound", identifier), true);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.stopsound.success.sourceless.any"), true);
		}

		return collection.size();
	}
}
