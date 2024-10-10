package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TeleportCommand {
	private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.teleport.invalidPosition")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
			CommandManager.literal("teleport")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("location", Vec3ArgumentType.vec3())
						.executes(
							context -> execute(
									context.getSource(),
									Collections.singleton(context.getSource().getEntityOrThrow()),
									context.getSource().getWorld(),
									Vec3ArgumentType.getPosArgument(context, "location"),
									null,
									null
								)
						)
				)
				.then(
					CommandManager.argument("destination", EntityArgumentType.entity())
						.executes(
							context -> execute(
									context.getSource(), Collections.singleton(context.getSource().getEntityOrThrow()), EntityArgumentType.getEntity(context, "destination")
								)
						)
				)
				.then(
					CommandManager.argument("targets", EntityArgumentType.entities())
						.then(
							CommandManager.argument("location", Vec3ArgumentType.vec3())
								.executes(
									context -> execute(
											context.getSource(),
											EntityArgumentType.getEntities(context, "targets"),
											context.getSource().getWorld(),
											Vec3ArgumentType.getPosArgument(context, "location"),
											null,
											null
										)
								)
								.then(
									CommandManager.argument("rotation", RotationArgumentType.rotation())
										.executes(
											context -> execute(
													context.getSource(),
													EntityArgumentType.getEntities(context, "targets"),
													context.getSource().getWorld(),
													Vec3ArgumentType.getPosArgument(context, "location"),
													RotationArgumentType.getRotation(context, "rotation"),
													null
												)
										)
								)
								.then(
									CommandManager.literal("facing")
										.then(
											CommandManager.literal("entity")
												.then(
													CommandManager.argument("facingEntity", EntityArgumentType.entity())
														.executes(
															context -> execute(
																	context.getSource(),
																	EntityArgumentType.getEntities(context, "targets"),
																	context.getSource().getWorld(),
																	Vec3ArgumentType.getPosArgument(context, "location"),
																	null,
																	new LookTarget.LookAtEntity(EntityArgumentType.getEntity(context, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.FEET)
																)
														)
														.then(
															CommandManager.argument("facingAnchor", EntityAnchorArgumentType.entityAnchor())
																.executes(
																	context -> execute(
																			context.getSource(),
																			EntityArgumentType.getEntities(context, "targets"),
																			context.getSource().getWorld(),
																			Vec3ArgumentType.getPosArgument(context, "location"),
																			null,
																			new LookTarget.LookAtEntity(
																				EntityArgumentType.getEntity(context, "facingEntity"), EntityAnchorArgumentType.getEntityAnchor(context, "facingAnchor")
																			)
																		)
																)
														)
												)
										)
										.then(
											CommandManager.argument("facingLocation", Vec3ArgumentType.vec3())
												.executes(
													context -> execute(
															context.getSource(),
															EntityArgumentType.getEntities(context, "targets"),
															context.getSource().getWorld(),
															Vec3ArgumentType.getPosArgument(context, "location"),
															null,
															new LookTarget.LookAtPosition(Vec3ArgumentType.getVec3(context, "facingLocation"))
														)
												)
										)
								)
						)
						.then(
							CommandManager.argument("destination", EntityArgumentType.entity())
								.executes(
									context -> execute(context.getSource(), EntityArgumentType.getEntities(context, "targets"), EntityArgumentType.getEntity(context, "destination"))
								)
						)
				)
		);
		dispatcher.register(CommandManager.literal("tp").requires(source -> source.hasPermissionLevel(2)).redirect(literalCommandNode));
	}

	private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Entity destination) throws CommandSyntaxException {
		for (Entity entity : targets) {
			teleport(
				source,
				entity,
				(ServerWorld)destination.getWorld(),
				destination.getX(),
				destination.getY(),
				destination.getZ(),
				EnumSet.noneOf(PositionFlag.class),
				destination.getYaw(),
				destination.getPitch(),
				null
			);
		}

		if (targets.size() == 1) {
			source.sendFeedback(
				() -> Text.translatable("commands.teleport.success.entity.single", ((Entity)targets.iterator().next()).getDisplayName(), destination.getDisplayName()),
				true
			);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.teleport.success.entity.multiple", targets.size(), destination.getDisplayName()), true);
		}

		return targets.size();
	}

	private static int execute(
		ServerCommandSource source,
		Collection<? extends Entity> targets,
		ServerWorld world,
		PosArgument location,
		@Nullable PosArgument rotation,
		@Nullable LookTarget facingLocation
	) throws CommandSyntaxException {
		Vec3d vec3d = location.getPos(source);
		Vec2f vec2f = rotation == null ? null : rotation.getRotation(source);

		for (Entity entity : targets) {
			Set<PositionFlag> set = getFlags(location, rotation, entity.getWorld().getRegistryKey() == world.getRegistryKey());
			if (vec2f == null) {
				teleport(source, entity, world, vec3d.x, vec3d.y, vec3d.z, set, entity.getYaw(), entity.getPitch(), facingLocation);
			} else {
				teleport(source, entity, world, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, facingLocation);
			}
		}

		if (targets.size() == 1) {
			source.sendFeedback(
				() -> Text.translatable(
						"commands.teleport.success.location.single",
						((Entity)targets.iterator().next()).getDisplayName(),
						formatFloat(vec3d.x),
						formatFloat(vec3d.y),
						formatFloat(vec3d.z)
					),
				true
			);
		} else {
			source.sendFeedback(
				() -> Text.translatable("commands.teleport.success.location.multiple", targets.size(), formatFloat(vec3d.x), formatFloat(vec3d.y), formatFloat(vec3d.z)),
				true
			);
		}

		return targets.size();
	}

	private static Set<PositionFlag> getFlags(PosArgument pos, @Nullable PosArgument rotation, boolean sameDimension) {
		Set<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
		if (pos.isXRelative()) {
			set.add(PositionFlag.DELTA_X);
			if (sameDimension) {
				set.add(PositionFlag.X);
			}
		}

		if (pos.isYRelative()) {
			set.add(PositionFlag.DELTA_Y);
			if (sameDimension) {
				set.add(PositionFlag.Y);
			}
		}

		if (pos.isZRelative()) {
			set.add(PositionFlag.DELTA_Z);
			if (sameDimension) {
				set.add(PositionFlag.Z);
			}
		}

		if (rotation == null || rotation.isXRelative()) {
			set.add(PositionFlag.X_ROT);
		}

		if (rotation == null || rotation.isYRelative()) {
			set.add(PositionFlag.Y_ROT);
		}

		return set;
	}

	private static String formatFloat(double d) {
		return String.format(Locale.ROOT, "%f", d);
	}

	private static void teleport(
		ServerCommandSource source,
		Entity target,
		ServerWorld world,
		double x,
		double y,
		double z,
		Set<PositionFlag> movementFlags,
		float yaw,
		float pitch,
		@Nullable LookTarget facingLocation
	) throws CommandSyntaxException {
		BlockPos blockPos = BlockPos.ofFloored(x, y, z);
		if (!World.isValid(blockPos)) {
			throw INVALID_POSITION_EXCEPTION.create();
		} else {
			double d = movementFlags.contains(PositionFlag.X) ? x - target.getX() : x;
			double e = movementFlags.contains(PositionFlag.Y) ? y - target.getY() : y;
			double f = movementFlags.contains(PositionFlag.Z) ? z - target.getZ() : z;
			float g = movementFlags.contains(PositionFlag.Y_ROT) ? yaw - target.getYaw() : yaw;
			float h = movementFlags.contains(PositionFlag.X_ROT) ? pitch - target.getPitch() : pitch;
			float i = MathHelper.wrapDegrees(g);
			float j = MathHelper.wrapDegrees(h);
			if (target.teleport(world, d, e, f, movementFlags, i, j, true)) {
				if (facingLocation != null) {
					facingLocation.look(source, target);
				}

				if (!(target instanceof LivingEntity livingEntity) || !livingEntity.isGliding()) {
					target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
					target.setOnGround(true);
				}

				if (target instanceof PathAwareEntity pathAwareEntity) {
					pathAwareEntity.getNavigation().stop();
				}
			}
		}
	}
}
