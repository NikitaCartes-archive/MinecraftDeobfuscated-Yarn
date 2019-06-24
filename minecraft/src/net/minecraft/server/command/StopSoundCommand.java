package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class StopSoundCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		RequiredArgumentBuilder<ServerCommandSource, EntitySelector> requiredArgumentBuilder = CommandManager.argument("targets", EntityArgumentType.players())
			.executes(commandContext -> execute(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), null, null))
			.then(
				CommandManager.literal("*")
					.then(
						CommandManager.argument("sound", IdentifierArgumentType.identifier())
							.suggests(SuggestionProviders.AVAILABLE_SOUNDS)
							.executes(
								commandContext -> execute(
										commandContext.getSource(),
										EntityArgumentType.getPlayers(commandContext, "targets"),
										null,
										IdentifierArgumentType.getIdentifier(commandContext, "sound")
									)
							)
					)
			);

		for (SoundCategory soundCategory : SoundCategory.values()) {
			requiredArgumentBuilder.then(
				CommandManager.literal(soundCategory.getName())
					.executes(commandContext -> execute(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), soundCategory, null))
					.then(
						CommandManager.argument("sound", IdentifierArgumentType.identifier())
							.suggests(SuggestionProviders.AVAILABLE_SOUNDS)
							.executes(
								commandContext -> execute(
										commandContext.getSource(),
										EntityArgumentType.getPlayers(commandContext, "targets"),
										soundCategory,
										IdentifierArgumentType.getIdentifier(commandContext, "sound")
									)
							)
					)
			);
		}

		commandDispatcher.register(
			CommandManager.literal("stopsound").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(requiredArgumentBuilder)
		);
	}

	private static int execute(
		ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, @Nullable SoundCategory soundCategory, @Nullable Identifier identifier
	) {
		StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(identifier, soundCategory);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(stopSoundS2CPacket);
		}

		if (soundCategory != null) {
			if (identifier != null) {
				serverCommandSource.sendFeedback(new TranslatableText("commands.stopsound.success.source.sound", identifier, soundCategory.getName()), true);
			} else {
				serverCommandSource.sendFeedback(new TranslatableText("commands.stopsound.success.source.any", soundCategory.getName()), true);
			}
		} else if (identifier != null) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.stopsound.success.sourceless.sound", identifier), true);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.stopsound.success.sourceless.any"), true);
		}

		return collection.size();
	}
}
