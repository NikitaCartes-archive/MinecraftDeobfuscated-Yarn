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
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
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
									DefaultPosArgument.zero(),
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
																	new TeleportCommand.LookTarget(EntityArgumentType.getEntity(context, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.FEET)
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
																			new TeleportCommand.LookTarget(
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
															new TeleportCommand.LookTarget(Vec3ArgumentType.getVec3(context, "facingLocation"))
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
				Text.translatable("commands.teleport.success.entity.single", ((Entity)targets.iterator().next()).getDisplayName(), destination.getDisplayName()), true
			);
		} else {
			source.sendFeedback(Text.translatable("commands.teleport.success.entity.multiple", targets.size(), destination.getDisplayName()), true);
		}

		return targets.size();
	}

	private static int execute(
		ServerCommandSource source,
		Collection<? extends Entity> targets,
		ServerWorld world,
		PosArgument location,
		@Nullable PosArgument rotation,
		@Nullable TeleportCommand.LookTarget facingLocation
	) throws CommandSyntaxException {
		Vec3d vec3d = location.toAbsolutePos(source);
		Vec2f vec2f = rotation == null ? null : rotation.toAbsoluteRotation(source);
		Set<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
		if (location.isXRelative()) {
			set.add(PositionFlag.X);
		}

		if (location.isYRelative()) {
			set.add(PositionFlag.Y);
		}

		if (location.isZRelative()) {
			set.add(PositionFlag.Z);
		}

		if (rotation == null) {
			set.add(PositionFlag.X_ROT);
			set.add(PositionFlag.Y_ROT);
		} else {
			if (rotation.isXRelative()) {
				set.add(PositionFlag.X_ROT);
			}

			if (rotation.isYRelative()) {
				set.add(PositionFlag.Y_ROT);
			}
		}

		for (Entity entity : targets) {
			if (rotation == null) {
				teleport(source, entity, world, vec3d.x, vec3d.y, vec3d.z, set, entity.getYaw(), entity.getPitch(), facingLocation);
			} else {
				teleport(source, entity, world, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, facingLocation);
			}
		}

		if (targets.size() == 1) {
			source.sendFeedback(
				Text.translatable(
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
				Text.translatable("commands.teleport.success.location.multiple", targets.size(), formatFloat(vec3d.x), formatFloat(vec3d.y), formatFloat(vec3d.z)), true
			);
		}

		return targets.size();
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
		@Nullable TeleportCommand.LookTarget facingLocation
	) throws CommandSyntaxException {
		BlockPos blockPos = BlockPos.ofFloored(x, y, z);
		if (!World.isValid(blockPos)) {
			throw INVALID_POSITION_EXCEPTION.create();
		} else {
			float f = MathHelper.wrapDegrees(yaw);
			float g = MathHelper.wrapDegrees(pitch);
			if (target.teleport(world, x, y, z, movementFlags, f, g)) {
				if (facingLocation != null) {
					facingLocation.look(source, target);
				}

				if (!(target instanceof LivingEntity livingEntity) || !livingEntity.isFallFlying()) {
					target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
					target.setOnGround(true);
				}

				if (target instanceof PathAwareEntity pathAwareEntity) {
					pathAwareEntity.getNavigation().stop();
				}
			}
		}
	}

	static class LookTarget {
		private final Vec3d targetPos;
		private final Entity target;
		private final EntityAnchorArgumentType.EntityAnchor targetAnchor;

		public LookTarget(Entity target, EntityAnchorArgumentType.EntityAnchor targetAnchor) {
			this.target = target;
			this.targetAnchor = targetAnchor;
			this.targetPos = targetAnchor.positionAt(target);
		}

		public LookTarget(Vec3d targetPos) {
			this.target = null;
			this.targetPos = targetPos;
			this.targetAnchor = null;
		}

		public void look(ServerCommandSource source, Entity entity) {
			if (this.target != null) {
				if (entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity).lookAtEntity(source.getEntityAnchor(), this.target, this.targetAnchor);
				} else {
					entity.lookAt(source.getEntityAnchor(), this.targetPos);
				}
			} else {
				entity.lookAt(source.getEntityAnchor(), this.targetPos);
			}
		}
	}
}
