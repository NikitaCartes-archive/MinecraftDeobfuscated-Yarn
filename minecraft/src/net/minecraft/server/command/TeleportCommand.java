package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_2267;
import net.minecraft.class_2280;
import net.minecraft.client.network.packet.PlayerPositionLookClientPacket;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class TeleportCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			ServerCommandManager.literal("teleport")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities())
						.then(
							ServerCommandManager.argument("location", Vec3ArgumentType.create())
								.executes(
									commandContext -> method_13765(
											commandContext.getSource(),
											EntityArgumentType.method_9317(commandContext, "targets"),
											commandContext.getSource().getWorld(),
											Vec3ArgumentType.method_9734(commandContext, "location"),
											null,
											null
										)
								)
								.then(
									ServerCommandManager.argument("rotation", RotationArgumentType.create())
										.executes(
											commandContext -> method_13765(
													commandContext.getSource(),
													EntityArgumentType.method_9317(commandContext, "targets"),
													commandContext.getSource().getWorld(),
													Vec3ArgumentType.method_9734(commandContext, "location"),
													RotationArgumentType.getRotationArgument(commandContext, "rotation"),
													null
												)
										)
								)
								.then(
									ServerCommandManager.literal("facing")
										.then(
											ServerCommandManager.literal("entity")
												.then(
													ServerCommandManager.argument("facingEntity", EntityArgumentType.oneEntity())
														.executes(
															commandContext -> method_13765(
																	commandContext.getSource(),
																	EntityArgumentType.method_9317(commandContext, "targets"),
																	commandContext.getSource().getWorld(),
																	Vec3ArgumentType.method_9734(commandContext, "location"),
																	null,
																	new TeleportCommand.class_3144(
																		EntityArgumentType.method_9313(commandContext, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.field_9853
																	)
																)
														)
														.then(
															ServerCommandManager.argument("facingAnchor", EntityAnchorArgumentType.create())
																.executes(
																	commandContext -> method_13765(
																			commandContext.getSource(),
																			EntityArgumentType.method_9317(commandContext, "targets"),
																			commandContext.getSource().getWorld(),
																			Vec3ArgumentType.method_9734(commandContext, "location"),
																			null,
																			new TeleportCommand.class_3144(
																				EntityArgumentType.method_9313(commandContext, "facingEntity"), EntityAnchorArgumentType.getAnchorArgument(commandContext, "facingAnchor")
																			)
																		)
																)
														)
												)
										)
										.then(
											ServerCommandManager.argument("facingLocation", Vec3ArgumentType.create())
												.executes(
													commandContext -> method_13765(
															commandContext.getSource(),
															EntityArgumentType.method_9317(commandContext, "targets"),
															commandContext.getSource().getWorld(),
															Vec3ArgumentType.method_9734(commandContext, "location"),
															null,
															new TeleportCommand.class_3144(Vec3ArgumentType.getVec3Argument(commandContext, "facingLocation"))
														)
												)
										)
								)
						)
						.then(
							ServerCommandManager.argument("destination", EntityArgumentType.oneEntity())
								.executes(
									commandContext -> method_13771(
											commandContext.getSource(), EntityArgumentType.method_9317(commandContext, "targets"), EntityArgumentType.method_9313(commandContext, "destination")
										)
								)
						)
				)
				.then(
					ServerCommandManager.argument("location", Vec3ArgumentType.create())
						.executes(
							commandContext -> method_13765(
									commandContext.getSource(),
									Collections.singleton(commandContext.getSource().getEntityOrThrow()),
									commandContext.getSource().getWorld(),
									Vec3ArgumentType.method_9734(commandContext, "location"),
									class_2280.method_9751(),
									null
								)
						)
				)
				.then(
					ServerCommandManager.argument("destination", EntityArgumentType.oneEntity())
						.executes(
							commandContext -> method_13771(
									commandContext.getSource(),
									Collections.singleton(commandContext.getSource().getEntityOrThrow()),
									EntityArgumentType.method_9313(commandContext, "destination")
								)
						)
				)
		);
		commandDispatcher.register(
			ServerCommandManager.literal("tp").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).redirect(literalCommandNode)
		);
	}

	private static int method_13771(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, Entity entity) {
		for (Entity entity2 : collection) {
			method_13766(
				serverCommandSource,
				entity2,
				serverCommandSource.getWorld(),
				entity.x,
				entity.y,
				entity.z,
				EnumSet.noneOf(PlayerPositionLookClientPacket.Flag.class),
				entity.yaw,
				entity.pitch,
				null
			);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.teleport.success.entity.single", ((Entity)collection.iterator().next()).getDisplayName(), entity.getDisplayName()),
				true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.teleport.success.entity.multiple", collection.size(), entity.getDisplayName()), true
			);
		}

		return collection.size();
	}

	private static int method_13765(
		ServerCommandSource serverCommandSource,
		Collection<? extends Entity> collection,
		ServerWorld serverWorld,
		class_2267 arg,
		@Nullable class_2267 arg2,
		@Nullable TeleportCommand.class_3144 arg3
	) throws CommandSyntaxException {
		Vec3d vec3d = arg.method_9708(serverCommandSource);
		Vec2f vec2f = arg2 == null ? null : arg2.method_9709(serverCommandSource);
		Set<PlayerPositionLookClientPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookClientPacket.Flag.class);
		if (arg.method_9705()) {
			set.add(PlayerPositionLookClientPacket.Flag.X);
		}

		if (arg.method_9706()) {
			set.add(PlayerPositionLookClientPacket.Flag.Y);
		}

		if (arg.method_9707()) {
			set.add(PlayerPositionLookClientPacket.Flag.Z);
		}

		if (arg2 == null) {
			set.add(PlayerPositionLookClientPacket.Flag.X_ROT);
			set.add(PlayerPositionLookClientPacket.Flag.Y_ROT);
		} else {
			if (arg2.method_9705()) {
				set.add(PlayerPositionLookClientPacket.Flag.X_ROT);
			}

			if (arg2.method_9706()) {
				set.add(PlayerPositionLookClientPacket.Flag.Y_ROT);
			}
		}

		for (Entity entity : collection) {
			if (arg2 == null) {
				method_13766(serverCommandSource, entity, serverWorld, vec3d.x, vec3d.y, vec3d.z, set, entity.yaw, entity.pitch, arg3);
			} else {
				method_13766(serverCommandSource, entity, serverWorld, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, arg3);
			}
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent(
					"commands.teleport.success.location.single", ((Entity)collection.iterator().next()).getDisplayName(), vec3d.x, vec3d.y, vec3d.z
				),
				true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.teleport.success.location.multiple", collection.size(), vec3d.x, vec3d.y, vec3d.z), true
			);
		}

		return collection.size();
	}

	private static void method_13766(
		ServerCommandSource serverCommandSource,
		Entity entity,
		ServerWorld serverWorld,
		double d,
		double e,
		double f,
		Set<PlayerPositionLookClientPacket.Flag> set,
		float g,
		float h,
		@Nullable TeleportCommand.class_3144 arg
	) {
		if (entity instanceof ServerPlayerEntity) {
			entity.stopRiding();
			if (((ServerPlayerEntity)entity).isSleeping()) {
				((ServerPlayerEntity)entity).method_7358(true, true, false);
			}

			if (serverWorld == entity.world) {
				((ServerPlayerEntity)entity).networkHandler.method_14360(d, e, f, g, h, set);
			} else {
				((ServerPlayerEntity)entity).method_14251(serverWorld, d, e, f, g, h);
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
				ServerWorld serverWorld2 = (ServerWorld)entity.world;
				serverWorld2.removeEntity(entity);
				entity.dimension = serverWorld.dimension.getType();
				entity.invalid = false;
				Entity entity2 = entity;
				entity = entity.getType().create(serverWorld);
				if (entity == null) {
					return;
				}

				entity.method_5878(entity2);
				entity.setPositionAndAngles(d, e, f, i, j);
				entity.setHeadYaw(i);
				boolean bl = entity.field_5983;
				entity.field_5983 = true;
				serverWorld.spawnEntity(entity);
				entity.field_5983 = bl;
				serverWorld.method_8553(entity, false);
				entity2.invalid = true;
			}
		}

		if (arg != null) {
			arg.method_13772(serverCommandSource, entity);
		}

		if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).isFallFlying()) {
			entity.velocityY = 0.0;
			entity.onGround = true;
		}
	}

	static class class_3144 {
		private final Vec3d field_13760;
		private final Entity field_13758;
		private final EntityAnchorArgumentType.EntityAnchor field_13759;

		public class_3144(Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor) {
			this.field_13758 = entity;
			this.field_13759 = entityAnchor;
			this.field_13760 = entityAnchor.positionAt(entity);
		}

		public class_3144(Vec3d vec3d) {
			this.field_13758 = null;
			this.field_13760 = vec3d;
			this.field_13759 = null;
		}

		public void method_13772(ServerCommandSource serverCommandSource, Entity entity) {
			if (this.field_13758 != null) {
				if (entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity).method_14222(serverCommandSource.getEntityAnchor(), this.field_13758, this.field_13759);
				} else {
					entity.lookAt(serverCommandSource.getEntityAnchor(), this.field_13760);
				}
			} else {
				entity.lookAt(serverCommandSource.getEntityAnchor(), this.field_13760);
			}
		}
	}
}
