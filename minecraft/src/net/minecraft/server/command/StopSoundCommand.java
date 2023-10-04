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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StopSoundCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		RequiredArgumentBuilder<ServerCommandSource, EntitySelector> requiredArgumentBuilder = CommandManager.argument("targets", EntityArgumentType.players())
			.executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), null, null))
			.then(
				CommandManager.literal("*")
					.then(
						CommandManager.argument("sound", IdentifierArgumentType.identifier())
							.suggests(SuggestionProviders.AVAILABLE_SOUNDS)
							.executes(
								context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), null, IdentifierArgumentType.getIdentifier(context, "sound"))
							)
					)
			);

		for (SoundCategory soundCategory : SoundCategory.values()) {
			requiredArgumentBuilder.then(
				CommandManager.literal(soundCategory.getName())
					.executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), soundCategory, null))
					.then(
						CommandManager.argument("sound", IdentifierArgumentType.identifier())
							.suggests(SuggestionProviders.AVAILABLE_SOUNDS)
							.executes(
								context -> execute(
										context.getSource(), EntityArgumentType.getPlayers(context, "targets"), soundCategory, IdentifierArgumentType.getIdentifier(context, "sound")
									)
							)
					)
			);
		}

		dispatcher.register(CommandManager.literal("stopsound").requires(source -> source.hasPermissionLevel(2)).then(requiredArgumentBuilder));
	}

	private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, @Nullable SoundCategory category, @Nullable Identifier sound) {
		StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(sound, category);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(stopSoundS2CPacket);
		}

		if (category != null) {
			if (sound != null) {
				source.sendFeedback(() -> Text.translatable("commands.stopsound.success.source.sound", Text.of(sound), category.getName()), true);
			} else {
				source.sendFeedback(() -> Text.translatable("commands.stopsound.success.source.any", category.getName()), true);
			}
		} else if (sound != null) {
			source.sendFeedback(() -> Text.translatable("commands.stopsound.success.sourceless.sound", Text.of(sound)), true);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.stopsound.success.sourceless.any"), true);
		}

		return targets.size();
	}
}
