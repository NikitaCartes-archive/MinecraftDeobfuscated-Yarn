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
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class SpectateCommand {
    private static final SimpleCommandExceptionType SPECTATE_SELF_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.spectate.self"));
    private static final DynamicCommandExceptionType NOT_SPECTATOR_EXCEPTION = new DynamicCommandExceptionType(playerName -> Text.translatable("commands.spectate.not_spectator", playerName));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spectate").requires(source -> source.hasPermissionLevel(2))).executes(context -> SpectateCommand.execute((ServerCommandSource)context.getSource(), null, ((ServerCommandSource)context.getSource()).getPlayer()))).then(((RequiredArgumentBuilder)CommandManager.argument("target", EntityArgumentType.entity()).executes(context -> SpectateCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), ((ServerCommandSource)context.getSource()).getPlayer()))).then(CommandManager.argument("player", EntityArgumentType.player()).executes(context -> SpectateCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), EntityArgumentType.getPlayer(context, "player"))))));
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
            source.sendFeedback(Text.translatable("commands.spectate.success.started", entity.getDisplayName()), false);
        } else {
            source.sendFeedback(Text.translatable("commands.spectate.success.stopped"), false);
        }
        return 1;
    }
}

