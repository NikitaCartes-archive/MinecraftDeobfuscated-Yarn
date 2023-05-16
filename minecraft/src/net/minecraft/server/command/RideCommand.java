package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;

public class RideCommand {
	private static final DynamicCommandExceptionType NOT_RIDING_EXCEPTION = new DynamicCommandExceptionType(
		entity -> Text.translatable("commands.ride.not_riding", entity)
	);
	private static final Dynamic2CommandExceptionType ALREADY_RIDING_EXCEPTION = new Dynamic2CommandExceptionType(
		(rider, vehicle) -> Text.translatable("commands.ride.already_riding", rider, vehicle)
	);
	private static final Dynamic2CommandExceptionType GENERIC_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType(
		(rider, vehicle) -> Text.translatable("commands.ride.mount.failure.generic", rider, vehicle)
	);
	private static final SimpleCommandExceptionType CANT_RIDE_PLAYERS_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.ride.mount.failure.cant_ride_players")
	);
	private static final SimpleCommandExceptionType RIDE_LOOP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ride.mount.failure.loop"));
	private static final SimpleCommandExceptionType WRONG_DIMENSION_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.ride.mount.failure.wrong_dimension")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("ride")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("target", EntityArgumentType.entity())
						.then(
							CommandManager.literal("mount")
								.then(
									CommandManager.argument("vehicle", EntityArgumentType.entity())
										.executes(
											context -> executeMount(context.getSource(), EntityArgumentType.getEntity(context, "target"), EntityArgumentType.getEntity(context, "vehicle"))
										)
								)
						)
						.then(CommandManager.literal("dismount").executes(context -> executeDismount(context.getSource(), EntityArgumentType.getEntity(context, "target"))))
				)
		);
	}

	private static int executeMount(ServerCommandSource source, Entity rider, Entity vehicle) throws CommandSyntaxException {
		Entity entity = rider.getVehicle();
		if (entity != null) {
			throw ALREADY_RIDING_EXCEPTION.create(rider.getDisplayName(), entity.getDisplayName());
		} else if (vehicle.getType() == EntityType.PLAYER) {
			throw CANT_RIDE_PLAYERS_EXCEPTION.create();
		} else if (rider.streamSelfAndPassengers().anyMatch(passenger -> passenger == vehicle)) {
			throw RIDE_LOOP_EXCEPTION.create();
		} else if (rider.getWorld() != vehicle.getWorld()) {
			throw WRONG_DIMENSION_EXCEPTION.create();
		} else if (!rider.startRiding(vehicle, true)) {
			throw GENERIC_FAILURE_EXCEPTION.create(rider.getDisplayName(), vehicle.getDisplayName());
		} else {
			source.sendFeedback(() -> Text.translatable("commands.ride.mount.success", rider.getDisplayName(), vehicle.getDisplayName()), true);
			return 1;
		}
	}

	private static int executeDismount(ServerCommandSource source, Entity rider) throws CommandSyntaxException {
		Entity entity = rider.getVehicle();
		if (entity == null) {
			throw NOT_RIDING_EXCEPTION.create(rider.getDisplayName());
		} else {
			rider.stopRiding();
			source.sendFeedback(() -> Text.translatable("commands.ride.dismount.success", rider.getDisplayName(), entity.getDisplayName()), true);
			return 1;
		}
	}
}
