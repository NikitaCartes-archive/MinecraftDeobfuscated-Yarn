/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class SpectateCommand {
    private static final SimpleCommandExceptionType SPECTATE_SELF_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.spectate.self"));
    private static final DynamicCommandExceptionType NOT_SPECTATOR_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.spectate.not_spectator", object));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spectate").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> SpectateCommand.execute((ServerCommandSource)commandContext.getSource(), null, ((ServerCommandSource)commandContext.getSource()).getPlayer()))).then(((RequiredArgumentBuilder)CommandManager.argument("target", EntityArgumentType.entity()).executes(commandContext -> SpectateCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity(commandContext, "target"), ((ServerCommandSource)commandContext.getSource()).getPlayer()))).then(CommandManager.argument("player", EntityArgumentType.player()).executes(commandContext -> SpectateCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity(commandContext, "target"), EntityArgumentType.getPlayer(commandContext, "player"))))));
    }

    private static int execute(ServerCommandSource source, @Nullable Entity entity, ServerPlayerEntity player) throws CommandSyntaxException {
        if (player == entity) {
            throw SPECTATE_SELF_EXCEPTION.create();
        }
        if (player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
            throw NOT_SPECTATOR_EXCEPTION.create(player.getDisplayName());
        }
        player.setCameraEntity(entity);
        if (entity != null) {
            source.sendFeedback(new TranslatableText("commands.spectate.success.started", entity.getDisplayName()), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.spectate.success.stopped"), false);
        }
        return 1;
    }
}

