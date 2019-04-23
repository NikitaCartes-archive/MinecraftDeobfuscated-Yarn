/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class StopSoundCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        RequiredArgumentBuilder requiredArgumentBuilder = (RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> StopSoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), null, null))).then(CommandManager.literal("*").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("sound", IdentifierArgumentType.create()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes(commandContext -> StopSoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), null, IdentifierArgumentType.getIdentifier(commandContext, "sound")))));
        for (SoundCategory soundCategory : SoundCategory.values()) {
            requiredArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(soundCategory.getName()).executes(commandContext -> StopSoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), soundCategory, null))).then(CommandManager.argument("sound", IdentifierArgumentType.create()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes(commandContext -> StopSoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), soundCategory, IdentifierArgumentType.getIdentifier(commandContext, "sound")))));
        }
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("stopsound").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(requiredArgumentBuilder));
    }

    private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, @Nullable SoundCategory soundCategory, @Nullable Identifier identifier) {
        StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(identifier, soundCategory);
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            serverPlayerEntity.networkHandler.sendPacket(stopSoundS2CPacket);
        }
        if (soundCategory != null) {
            if (identifier != null) {
                serverCommandSource.sendFeedback(new TranslatableComponent("commands.stopsound.success.source.sound", identifier, soundCategory.getName()), true);
            } else {
                serverCommandSource.sendFeedback(new TranslatableComponent("commands.stopsound.success.source.any", soundCategory.getName()), true);
            }
        } else if (identifier != null) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.stopsound.success.sourceless.sound", identifier), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.stopsound.success.sourceless.any", new Object[0]), true);
        }
        return collection.size();
    }
}

