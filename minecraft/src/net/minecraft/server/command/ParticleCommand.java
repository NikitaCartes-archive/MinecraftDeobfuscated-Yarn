package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ParticleArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.particle.Particle;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class ParticleCommand {
	private static final SimpleCommandExceptionType FAILED_EXCPETION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.particle.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("particle")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("name", ParticleArgumentType.create())
						.executes(
							commandContext -> method_13491(
									commandContext.getSource(),
									ParticleArgumentType.getParticleArgument(commandContext, "name"),
									commandContext.getSource().getPosition(),
									Vec3d.ZERO,
									0.0F,
									0,
									false,
									commandContext.getSource().getMinecraftServer().getConfigurationManager().getPlayerList()
								)
						)
						.then(
							ServerCommandManager.argument("pos", Vec3ArgumentType.create())
								.executes(
									commandContext -> method_13491(
											commandContext.getSource(),
											ParticleArgumentType.getParticleArgument(commandContext, "name"),
											Vec3ArgumentType.getVec3Argument(commandContext, "pos"),
											Vec3d.ZERO,
											0.0F,
											0,
											false,
											commandContext.getSource().getMinecraftServer().getConfigurationManager().getPlayerList()
										)
								)
								.then(
									ServerCommandManager.argument("delta", Vec3ArgumentType.create(false))
										.then(
											ServerCommandManager.argument("speed", FloatArgumentType.floatArg(0.0F))
												.then(
													ServerCommandManager.argument("count", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_13491(
																	commandContext.getSource(),
																	ParticleArgumentType.getParticleArgument(commandContext, "name"),
																	Vec3ArgumentType.getVec3Argument(commandContext, "pos"),
																	Vec3ArgumentType.getVec3Argument(commandContext, "delta"),
																	FloatArgumentType.getFloat(commandContext, "speed"),
																	IntegerArgumentType.getInteger(commandContext, "count"),
																	false,
																	commandContext.getSource().getMinecraftServer().getConfigurationManager().getPlayerList()
																)
														)
														.then(
															ServerCommandManager.literal("force")
																.executes(
																	commandContext -> method_13491(
																			commandContext.getSource(),
																			ParticleArgumentType.getParticleArgument(commandContext, "name"),
																			Vec3ArgumentType.getVec3Argument(commandContext, "pos"),
																			Vec3ArgumentType.getVec3Argument(commandContext, "delta"),
																			FloatArgumentType.getFloat(commandContext, "speed"),
																			IntegerArgumentType.getInteger(commandContext, "count"),
																			true,
																			commandContext.getSource().getMinecraftServer().getConfigurationManager().getPlayerList()
																		)
																)
																.then(
																	ServerCommandManager.argument("viewers", EntityArgumentType.method_9308())
																		.executes(
																			commandContext -> method_13491(
																					commandContext.getSource(),
																					ParticleArgumentType.getParticleArgument(commandContext, "name"),
																					Vec3ArgumentType.getVec3Argument(commandContext, "pos"),
																					Vec3ArgumentType.getVec3Argument(commandContext, "delta"),
																					FloatArgumentType.getFloat(commandContext, "speed"),
																					IntegerArgumentType.getInteger(commandContext, "count"),
																					true,
																					EntityArgumentType.method_9312(commandContext, "viewers")
																				)
																		)
																)
														)
														.then(
															ServerCommandManager.literal("normal")
																.executes(
																	commandContext -> method_13491(
																			commandContext.getSource(),
																			ParticleArgumentType.getParticleArgument(commandContext, "name"),
																			Vec3ArgumentType.getVec3Argument(commandContext, "pos"),
																			Vec3ArgumentType.getVec3Argument(commandContext, "delta"),
																			FloatArgumentType.getFloat(commandContext, "speed"),
																			IntegerArgumentType.getInteger(commandContext, "count"),
																			false,
																			commandContext.getSource().getMinecraftServer().getConfigurationManager().getPlayerList()
																		)
																)
																.then(
																	ServerCommandManager.argument("viewers", EntityArgumentType.method_9308())
																		.executes(
																			commandContext -> method_13491(
																					commandContext.getSource(),
																					ParticleArgumentType.getParticleArgument(commandContext, "name"),
																					Vec3ArgumentType.getVec3Argument(commandContext, "pos"),
																					Vec3ArgumentType.getVec3Argument(commandContext, "delta"),
																					FloatArgumentType.getFloat(commandContext, "speed"),
																					IntegerArgumentType.getInteger(commandContext, "count"),
																					false,
																					EntityArgumentType.method_9312(commandContext, "viewers")
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13491(
		ServerCommandSource serverCommandSource, Particle particle, Vec3d vec3d, Vec3d vec3d2, float f, int i, boolean bl, Collection<ServerPlayerEntity> collection
	) throws CommandSyntaxException {
		int j = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			if (serverCommandSource.getWorld().method_14166(serverPlayerEntity, particle, bl, vec3d.x, vec3d.y, vec3d.z, i, vec3d2.x, vec3d2.y, vec3d2.z, (double)f)) {
				j++;
			}
		}

		if (j == 0) {
			throw FAILED_EXCPETION.create();
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent(
					"commands.particle.success", Registry.PARTICLE_TYPE.getId((ParticleType<? extends Particle>)particle.getParticleType()).toString()
				),
				true
			);
			return j;
		}
	}
}
