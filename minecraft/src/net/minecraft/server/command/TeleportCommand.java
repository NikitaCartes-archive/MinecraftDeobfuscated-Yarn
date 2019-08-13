package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.command.arguments.DefaultPosArgument;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class TeleportCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			CommandManager.literal("teleport")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.entities())
						.then(
							CommandManager.argument("location", Vec3ArgumentType.vec3())
								.executes(
									commandContext -> execute(
											commandContext.getSource(),
											EntityArgumentType.getEntities(commandContext, "targets"),
											commandContext.getSource().getWorld(),
											Vec3ArgumentType.getPosArgument(commandContext, "location"),
											null,
											null
										)
								)
								.then(
									CommandManager.argument("rotation", RotationArgumentType.rotation())
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													EntityArgumentType.getEntities(commandContext, "targets"),
													commandContext.getSource().getWorld(),
													Vec3ArgumentType.getPosArgument(commandContext, "location"),
													RotationArgumentType.getRotation(commandContext, "rotation"),
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
															commandContext -> execute(
																	commandContext.getSource(),
																	EntityArgumentType.getEntities(commandContext, "targets"),
																	commandContext.getSource().getWorld(),
																	Vec3ArgumentType.getPosArgument(commandContext, "location"),
																	null,
																	new TeleportCommand.LookTarget(EntityArgumentType.getEntity(commandContext, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.field_9853)
																)
														)
														.then(
															CommandManager.argument("facingAnchor", EntityAnchorArgumentType.entityAnchor())
																.executes(
																	commandContext -> execute(
																			commandContext.getSource(),
																			EntityArgumentType.getEntities(commandContext, "targets"),
																			commandContext.getSource().getWorld(),
																			Vec3ArgumentType.getPosArgument(commandContext, "location"),
																			null,
																			new TeleportCommand.LookTarget(
																				EntityArgumentType.getEntity(commandContext, "facingEntity"), EntityAnchorArgumentType.getEntityAnchor(commandContext, "facingAnchor")
																			)
																		)
																)
														)
												)
										)
										.then(
											CommandManager.argument("facingLocation", Vec3ArgumentType.vec3())
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															EntityArgumentType.getEntities(commandContext, "targets"),
															commandContext.getSource().getWorld(),
															Vec3ArgumentType.getPosArgument(commandContext, "location"),
															null,
															new TeleportCommand.LookTarget(Vec3ArgumentType.getVec3(commandContext, "facingLocation"))
														)
												)
										)
								)
						)
						.then(
							CommandManager.argument("destination", EntityArgumentType.entity())
								.executes(
									commandContext -> execute(
											commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), EntityArgumentType.getEntity(commandContext, "destination")
										)
								)
						)
				)
				.then(
					CommandManager.argument("location", Vec3ArgumentType.vec3())
						.executes(
							commandContext -> execute(
									commandContext.getSource(),
									Collections.singleton(commandContext.getSource().getEntityOrThrow()),
									commandContext.getSource().getWorld(),
									Vec3ArgumentType.getPosArgument(commandContext, "location"),
									DefaultPosArgument.zero(),
									null
								)
						)
				)
				.then(
					CommandManager.argument("destination", EntityArgumentType.entity())
						.executes(
							commandContext -> execute(
									commandContext.getSource(),
									Collections.singleton(commandContext.getSource().getEntityOrThrow()),
									EntityArgumentType.getEntity(commandContext, "destination")
								)
						)
				)
		);
		commandDispatcher.register(
			CommandManager.literal("tp").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).redirect(literalCommandNode)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, Entity entity) {
		for (Entity entity2 : collection) {
			teleport(
				serverCommandSource,
				entity2,
				(ServerWorld)entity.world,
				entity.x,
				entity.y,
				entity.z,
				EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class),
				entity.yaw,
				entity.pitch,
				null
			);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.teleport.success.entity.single", ((Entity)collection.iterator().next()).getDisplayName(), entity.getDisplayName()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.teleport.success.entity.multiple", collection.size(), entity.getDisplayName()), true);
		}

		return collection.size();
	}

	private static int execute(
		ServerCommandSource serverCommandSource,
		Collection<? extends Entity> collection,
		ServerWorld serverWorld,
		PosArgument posArgument,
		@Nullable PosArgument posArgument2,
		@Nullable TeleportCommand.LookTarget lookTarget
	) throws CommandSyntaxException {
		Vec3d vec3d = posArgument.toAbsolutePos(serverCommandSource);
		Vec2f vec2f = posArgument2 == null ? null : posArgument2.toAbsoluteRotation(serverCommandSource);
		Set<PlayerPositionLookS2CPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);
		if (posArgument.isXRelative()) {
			set.add(PlayerPositionLookS2CPacket.Flag.field_12400);
		}

		if (posArgument.isYRelative()) {
			set.add(PlayerPositionLookS2CPacket.Flag.field_12398);
		}

		if (posArgument.isZRelative()) {
			set.add(PlayerPositionLookS2CPacket.Flag.field_12403);
		}

		if (posArgument2 == null) {
			set.add(PlayerPositionLookS2CPacket.Flag.field_12397);
			set.add(PlayerPositionLookS2CPacket.Flag.field_12401);
		} else {
			if (posArgument2.isXRelative()) {
				set.add(PlayerPositionLookS2CPacket.Flag.field_12397);
			}

			if (posArgument2.isYRelative()) {
				set.add(PlayerPositionLookS2CPacket.Flag.field_12401);
			}
		}

		for (Entity entity : collection) {
			if (posArgument2 == null) {
				teleport(serverCommandSource, entity, serverWorld, vec3d.x, vec3d.y, vec3d.z, set, entity.yaw, entity.pitch, lookTarget);
			} else {
				teleport(serverCommandSource, entity, serverWorld, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, lookTarget);
			}
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.teleport.success.location.single", ((Entity)collection.iterator().next()).getDisplayName(), vec3d.x, vec3d.y, vec3d.z), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.teleport.success.location.multiple", collection.size(), vec3d.x, vec3d.y, vec3d.z), true);
		}

		return collection.size();
	}

	private static void teleport(
		ServerCommandSource serverCommandSource,
		Entity entity,
		ServerWorld serverWorld,
		double d,
		double e,
		double f,
		Set<PlayerPositionLookS2CPacket.Flag> set,
		float g,
		float h,
		@Nullable TeleportCommand.LookTarget lookTarget
	) {
		if (entity instanceof ServerPlayerEntity) {
			ChunkPos chunkPos = new ChunkPos(new BlockPos(d, e, f));
			serverWorld.method_14178().addTicket(ChunkTicketType.field_19347, chunkPos, 1, entity.getEntityId());
			entity.stopRiding();
			if (((ServerPlayerEntity)entity).isSleeping()) {
				((ServerPlayerEntity)entity).wakeUp(true, true, false);
			}

			if (serverWorld == entity.world) {
				((ServerPlayerEntity)entity).networkHandler.teleportRequest(d, e, f, g, h, set);
			} else {
				((ServerPlayerEntity)entity).teleport(serverWorld, d, e, f, g, h);
			}

			entity.setHeadYaw(g);
		} else {
			float i = MathHelper.wrapDegrees(g);
			float j = MathHelper.wrapDegrees(h);
			j = MathHelper.clamp(j, -90.0F, 90.0F);
			if (serverWorld == entity.world) {
				entity.setPositionAndAngles(d, e, f, i, j);
				entity.setHeadYaw(i);
			} else {
				entity.detach();
				entity.dimension = serverWorld.dimension.getType();
				Entity entity2 = entity;
				entity = entity.getType().create(serverWorld);
				if (entity == null) {
					return;
				}

				entity.copyFrom(entity2);
				entity.setPositionAndAngles(d, e, f, i, j);
				entity.setHeadYaw(i);
				serverWorld.method_18769(entity);
				entity2.removed = true;
			}
		}

		if (lookTarget != null) {
			lookTarget.look(serverCommandSource, entity);
		}

		if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).isFallFlying()) {
			entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0));
			entity.onGround = true;
		}
	}

	static class LookTarget {
		private final Vec3d targetPos;
		private final Entity targetEntity;
		private final EntityAnchorArgumentType.EntityAnchor targetEntityAnchor;

		public LookTarget(Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor) {
			this.targetEntity = entity;
			this.targetEntityAnchor = entityAnchor;
			this.targetPos = entityAnchor.positionAt(entity);
		}

		public LookTarget(Vec3d vec3d) {
			this.targetEntity = null;
			this.targetPos = vec3d;
			this.targetEntityAnchor = null;
		}

		public void look(ServerCommandSource serverCommandSource, Entity entity) {
			if (this.targetEntity != null) {
				if (entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity).method_14222(serverCommandSource.getEntityAnchor(), this.targetEntity, this.targetEntityAnchor);
				} else {
					entity.lookAt(serverCommandSource.getEntityAnchor(), this.targetPos);
				}
			} else {
				entity.lookAt(serverCommandSource.getEntityAnchor(), this.targetPos);
			}
		}
	}
}
