/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RideCommand {
    private static final DynamicCommandExceptionType NOT_RIDING_EXCEPTION = new DynamicCommandExceptionType(entity -> Text.translatable("commands.ride.not_riding", entity));
    private static final Dynamic2CommandExceptionType ALREADY_RIDING_EXCEPTION = new Dynamic2CommandExceptionType((rider, vehicle) -> Text.translatable("commands.ride.already_riding", rider, vehicle));
    private static final Dynamic2CommandExceptionType GENERIC_FAILURE_EXCPETION = new Dynamic2CommandExceptionType((rider, vehicle) -> Text.translatable("commands.ride.mount.failure.generic", rider, vehicle));
    private static final SimpleCommandExceptionType CANT_RIDE_PLAYERS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ride.mount.failure.cant_ride_players"));
    private static final SimpleCommandExceptionType RIDE_LOOP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ride.mount.failure.loop"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ride").requires(source -> source.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("target", EntityArgumentType.entity()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("mount").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("vehicle", EntityArgumentType.entity()).executes(context -> RideCommand.executeMount((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), EntityArgumentType.getEntity(context, "vehicle")))))).then(CommandManager.literal("dismount").executes(context -> RideCommand.executeDismount((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"))))));
    }

    private static int executeMount(ServerCommandSource source, Entity rider, Entity vehicle) throws CommandSyntaxException {
        Entity entity = rider.getVehicle();
        if (entity != null) {
            throw ALREADY_RIDING_EXCEPTION.create(rider.getDisplayName(), entity.getDisplayName());
        }
        if (vehicle.getType() == EntityType.PLAYER) {
            throw CANT_RIDE_PLAYERS_EXCEPTION.create();
        }
        if (rider.streamSelfAndPassengers().anyMatch(passager -> passager == vehicle)) {
            throw RIDE_LOOP_EXCEPTION.create();
        }
        if (!rider.startRiding(vehicle, true)) {
            throw GENERIC_FAILURE_EXCPETION.create(rider.getDisplayName(), vehicle.getDisplayName());
        }
        source.sendFeedback(Text.translatable("commands.ride.mount.success", rider.getDisplayName(), vehicle.getDisplayName()), true);
        return 1;
    }

    private static int executeDismount(ServerCommandSource source, Entity rider) throws CommandSyntaxException {
        Entity entity = rider.getVehicle();
        if (entity == null) {
            throw NOT_RIDING_EXCEPTION.create(rider.getDisplayName());
        }
        rider.stopRiding();
        source.sendFeedback(Text.translatable("commands.ride.dismount.success", rider.getDisplayName(), entity.getDisplayName()), true);
        return 1;
    }
}

