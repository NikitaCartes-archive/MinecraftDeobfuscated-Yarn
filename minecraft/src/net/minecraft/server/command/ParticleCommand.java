package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class ParticleCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.particle.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(
			CommandManager.literal("particle")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("name", ParticleEffectArgumentType.particleEffect(registryAccess))
						.executes(
							context -> execute(
									context.getSource(),
									ParticleEffectArgumentType.getParticle(context, "name"),
									context.getSource().getPosition(),
									Vec3d.ZERO,
									0.0F,
									0,
									false,
									context.getSource().getServer().getPlayerManager().getPlayerList()
								)
						)
						.then(
							CommandManager.argument("pos", Vec3ArgumentType.vec3())
								.executes(
									context -> execute(
											context.getSource(),
											ParticleEffectArgumentType.getParticle(context, "name"),
											Vec3ArgumentType.getVec3(context, "pos"),
											Vec3d.ZERO,
											0.0F,
											0,
											false,
											context.getSource().getServer().getPlayerManager().getPlayerList()
										)
								)
								.then(
									CommandManager.argument("delta", Vec3ArgumentType.vec3(false))
										.then(
											CommandManager.argument("speed", FloatArgumentType.floatArg(0.0F))
												.then(
													CommandManager.argument("count", IntegerArgumentType.integer(0))
														.executes(
															context -> execute(
																	context.getSource(),
																	ParticleEffectArgumentType.getParticle(context, "name"),
																	Vec3ArgumentType.getVec3(context, "pos"),
																	Vec3ArgumentType.getVec3(context, "delta"),
																	FloatArgumentType.getFloat(context, "speed"),
																	IntegerArgumentType.getInteger(context, "count"),
																	false,
																	context.getSource().getServer().getPlayerManager().getPlayerList()
																)
														)
														.then(
															CommandManager.literal("force")
																.executes(
																	context -> execute(
																			context.getSource(),
																			ParticleEffectArgumentType.getParticle(context, "name"),
																			Vec3ArgumentType.getVec3(context, "pos"),
																			Vec3ArgumentType.getVec3(context, "delta"),
																			FloatArgumentType.getFloat(context, "speed"),
																			IntegerArgumentType.getInteger(context, "count"),
																			true,
																			context.getSource().getServer().getPlayerManager().getPlayerList()
																		)
																)
																.then(
																	CommandManager.argument("viewers", EntityArgumentType.players())
																		.executes(
																			context -> execute(
																					context.getSource(),
																					ParticleEffectArgumentType.getParticle(context, "name"),
																					Vec3ArgumentType.getVec3(context, "pos"),
																					Vec3ArgumentType.getVec3(context, "delta"),
																					FloatArgumentType.getFloat(context, "speed"),
																					IntegerArgumentType.getInteger(context, "count"),
																					true,
																					EntityArgumentType.getPlayers(context, "viewers")
																				)
																		)
																)
														)
														.then(
															CommandManager.literal("normal")
																.executes(
																	context -> execute(
																			context.getSource(),
																			ParticleEffectArgumentType.getParticle(context, "name"),
																			Vec3ArgumentType.getVec3(context, "pos"),
																			Vec3ArgumentType.getVec3(context, "delta"),
																			FloatArgumentType.getFloat(context, "speed"),
																			IntegerArgumentType.getInteger(context, "count"),
																			false,
																			context.getSource().getServer().getPlayerManager().getPlayerList()
																		)
																)
																.then(
																	CommandManager.argument("viewers", EntityArgumentType.players())
																		.executes(
																			context -> execute(
																					context.getSource(),
																					ParticleEffectArgumentType.getParticle(context, "name"),
																					Vec3ArgumentType.getVec3(context, "pos"),
																					Vec3ArgumentType.getVec3(context, "delta"),
																					FloatArgumentType.getFloat(context, "speed"),
																					IntegerArgumentType.getInteger(context, "count"),
																					false,
																					EntityArgumentType.getPlayers(context, "viewers")
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
		ServerCommandSource source, ParticleEffect parameters, Vec3d pos, Vec3d delta, float speed, int count, boolean force, Collection<ServerPlayerEntity> viewers
	) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : viewers) {
			if (source.getWorld().spawnParticles(serverPlayerEntity, parameters, force, pos.x, pos.y, pos.z, count, delta.x, delta.y, delta.z, (double)speed)) {
				i++;
			}
		}

		if (i == 0) {
			throw FAILED_EXCEPTION.create();
		} else {
			source.sendFeedback(() -> Text.translatable("commands.particle.success", Registries.PARTICLE_TYPE.getId(parameters.getType()).toString()), true);
			return i;
		}
	}
}
