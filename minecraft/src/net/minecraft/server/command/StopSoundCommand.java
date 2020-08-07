package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class StopSoundCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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

		dispatcher.register(
			CommandManager.literal("stopsound").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(requiredArgumentBuilder)
		);
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, @Nullable SoundCategory category, @Nullable Identifier sound) {
		StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(sound, category);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(stopSoundS2CPacket);
		}

		if (category != null) {
			if (sound != null) {
				source.sendFeedback(new TranslatableText("commands.stopsound.success.source.sound", sound, category.getName()), true);
			} else {
				source.sendFeedback(new TranslatableText("commands.stopsound.success.source.any", category.getName()), true);
			}
		} else if (sound != null) {
			source.sendFeedback(new TranslatableText("commands.stopsound.success.sourceless.sound", sound), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.stopsound.success.sourceless.any"), true);
		}

		return targets.size();
	}
}
