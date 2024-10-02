package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

public class RotateCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("rotate")
				.then(
					CommandManager.argument("target", EntityArgumentType.entity())
						.then(
							CommandManager.argument("rotation", RotationArgumentType.rotation())
								.executes(
									context -> rotateToPos(context.getSource(), EntityArgumentType.getEntity(context, "target"), RotationArgumentType.getRotation(context, "rotation"))
								)
						)
						.then(
							CommandManager.literal("facing")
								.then(
									CommandManager.literal("entity")
										.then(
											CommandManager.argument("facingEntity", EntityArgumentType.entity())
												.executes(
													context -> rotateFacingLookTarget(
															context.getSource(),
															EntityArgumentType.getEntity(context, "target"),
															new LookTarget.LookAtEntity(EntityArgumentType.getEntity(context, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.FEET)
														)
												)
												.then(
													CommandManager.argument("facingAnchor", EntityAnchorArgumentType.entityAnchor())
														.executes(
															context -> rotateFacingLookTarget(
																	context.getSource(),
																	EntityArgumentType.getEntity(context, "target"),
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
											context -> rotateFacingLookTarget(
													context.getSource(),
													EntityArgumentType.getEntity(context, "target"),
													new LookTarget.LookAtPosition(Vec3ArgumentType.getVec3(context, "facingLocation"))
												)
										)
								)
						)
				)
		);
	}

	private static int rotateToPos(ServerCommandSource source, Entity entity, PosArgument pos) {
		Vec2f vec2f = pos.getRotation(source);
		entity.rotate(vec2f.y, vec2f.x);
		source.sendFeedback(() -> Text.translatable("commands.rotate.success", entity.getDisplayName()), true);
		return 1;
	}

	private static int rotateFacingLookTarget(ServerCommandSource source, Entity entity, LookTarget lookTarget) {
		lookTarget.look(source, entity);
		source.sendFeedback(() -> Text.translatable("commands.rotate.success", entity.getDisplayName()), true);
		return 1;
	}
}
