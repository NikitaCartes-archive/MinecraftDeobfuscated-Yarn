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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class ParticleCommand {
	private static final SimpleCommandExceptionType FAILED_EXCPETION = new SimpleCommandExceptionType(new TranslatableText("commands.particle.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("particle")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("name", ParticleArgumentType.create())
						.executes(
							commandContext -> execute(
									commandContext.getSource(),
									ParticleArgumentType.getParticle(commandContext, "name"),
									commandContext.getSource().method_9222(),
									Vec3d.ZERO,
									0.0F,
									0,
									false,
									commandContext.getSource().getMinecraftServer().getPlayerManager().getPlayerList()
								)
						)
						.then(
							CommandManager.argument("pos", Vec3ArgumentType.create())
								.executes(
									commandContext -> execute(
											commandContext.getSource(),
											ParticleArgumentType.getParticle(commandContext, "name"),
											Vec3ArgumentType.getVec3(commandContext, "pos"),
											Vec3d.ZERO,
											0.0F,
											0,
											false,
											commandContext.getSource().getMinecraftServer().getPlayerManager().getPlayerList()
										)
								)
								.then(
									CommandManager.argument("delta", Vec3ArgumentType.create(false))
										.then(
											CommandManager.argument("speed", FloatArgumentType.floatArg(0.0F))
												.then(
													CommandManager.argument("count", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	ParticleArgumentType.getParticle(commandContext, "name"),
																	Vec3ArgumentType.getVec3(commandContext, "pos"),
																	Vec3ArgumentType.getVec3(commandContext, "delta"),
																	FloatArgumentType.getFloat(commandContext, "speed"),
																	IntegerArgumentType.getInteger(commandContext, "count"),
																	false,
																	commandContext.getSource().getMinecraftServer().getPlayerManager().getPlayerList()
																)
														)
														.then(
															CommandManager.literal("force")
																.executes(
																	commandContext -> execute(
																			commandContext.getSource(),
																			ParticleArgumentType.getParticle(commandContext, "name"),
																			Vec3ArgumentType.getVec3(commandContext, "pos"),
																			Vec3ArgumentType.getVec3(commandContext, "delta"),
																			FloatArgumentType.getFloat(commandContext, "speed"),
																			IntegerArgumentType.getInteger(commandContext, "count"),
																			true,
																			commandContext.getSource().getMinecraftServer().getPlayerManager().getPlayerList()
																		)
																)
																.then(
																	CommandManager.argument("viewers", EntityArgumentType.players())
																		.executes(
																			commandContext -> execute(
																					commandContext.getSource(),
																					ParticleArgumentType.getParticle(commandContext, "name"),
																					Vec3ArgumentType.getVec3(commandContext, "pos"),
																					Vec3ArgumentType.getVec3(commandContext, "delta"),
																					FloatArgumentType.getFloat(commandContext, "speed"),
																					IntegerArgumentType.getInteger(commandContext, "count"),
																					true,
																					EntityArgumentType.getPlayers(commandContext, "viewers")
																				)
																		)
																)
														)
														.then(
															CommandManager.literal("normal")
																.executes(
																	commandContext -> execute(
																			commandContext.getSource(),
																			ParticleArgumentType.getParticle(commandContext, "name"),
																			Vec3ArgumentType.getVec3(commandContext, "pos"),
																			Vec3ArgumentType.getVec3(commandContext, "delta"),
																			FloatArgumentType.getFloat(commandContext, "speed"),
																			IntegerArgumentType.getInteger(commandContext, "count"),
																			false,
																			commandContext.getSource().getMinecraftServer().getPlayerManager().getPlayerList()
																		)
																)
																.then(
																	CommandManager.argument("viewers", EntityArgumentType.players())
																		.executes(
																			commandContext -> execute(
																					commandContext.getSource(),
																					ParticleArgumentType.getParticle(commandContext, "name"),
																					Vec3ArgumentType.getVec3(commandContext, "pos"),
																					Vec3ArgumentType.getVec3(commandContext, "delta"),
																					FloatArgumentType.getFloat(commandContext, "speed"),
																					IntegerArgumentType.getInteger(commandContext, "count"),
																					false,
																					EntityArgumentType.getPlayers(commandContext, "viewers")
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

	private static int execute(
		ServerCommandSource serverCommandSource,
		ParticleEffect particleEffect,
		Vec3d vec3d,
		Vec3d vec3d2,
		float f,
		int i,
		boolean bl,
		Collection<ServerPlayerEntity> collection
	) throws CommandSyntaxException {
		int j = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			if (serverCommandSource.getWorld()
				.spawnParticles(serverPlayerEntity, particleEffect, bl, vec3d.x, vec3d.y, vec3d.z, i, vec3d2.x, vec3d2.y, vec3d2.z, (double)f)) {
				j++;
			}
		}

		if (j == 0) {
			throw FAILED_EXCPETION.create();
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.particle.success", Registry.PARTICLE_TYPE.getId((ParticleType<? extends ParticleEffect>)particleEffect.getType()).toString()),
				true
			);
			return j;
		}
	}
}
