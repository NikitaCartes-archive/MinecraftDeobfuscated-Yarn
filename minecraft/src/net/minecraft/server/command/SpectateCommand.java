package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class SpectateCommand {
	private static final SimpleCommandExceptionType SPECTATE_SELF_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.spectate.self"));
	private static final DynamicCommandExceptionType NOT_SPECTATOR_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.spectate.not_spectator", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("spectate")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(commandContext -> execute(commandContext.getSource(), null, commandContext.getSource().getPlayer()))
				.then(
					CommandManager.argument("target", EntityArgumentType.entity())
						.executes(
							commandContext -> execute(commandContext.getSource(), EntityArgumentType.getEntity(commandContext, "target"), commandContext.getSource().getPlayer())
						)
						.then(
							CommandManager.argument("player", EntityArgumentType.player())
								.executes(
									commandContext -> execute(
											commandContext.getSource(), EntityArgumentType.getEntity(commandContext, "target"), EntityArgumentType.getPlayer(commandContext, "player")
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, @Nullable Entity entity, ServerPlayerEntity player) throws CommandSyntaxException {
		if (player == entity) {
			throw SPECTATE_SELF_EXCEPTION.create();
		} else if (player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
			throw NOT_SPECTATOR_EXCEPTION.create(player.getDisplayName());
		} else {
			player.setCameraEntity(entity);
			if (entity != null) {
				source.sendFeedback(new TranslatableText("commands.spectate.success.started", entity.getDisplayName()), false);
			} else {
				source.sendFeedback(new TranslatableText("commands.spectate.success.stopped"), false);
			}

			return 1;
		}
	}
}
