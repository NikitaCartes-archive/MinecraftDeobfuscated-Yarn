package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.command.arguments.Vec2ArgumentType;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
	private static final SimpleCommandExceptionType CENTER_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.worldborder.center.failed")
	);
	private static final SimpleCommandExceptionType SET_FAILED_NOCHANGE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.worldborder.set.failed.nochange")
	);
	private static final SimpleCommandExceptionType SET_FAILED_SMALL_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.worldborder.set.failed.small.")
	);
	private static final SimpleCommandExceptionType SET_FAILED_BIG_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.worldborder.set.failed.big.")
	);
	private static final SimpleCommandExceptionType WARNING_TIME_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.worldborder.warning.time.failed")
	);
	private static final SimpleCommandExceptionType WARNING_DISTANCE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.worldborder.warning.distance.failed")
	);
	private static final SimpleCommandExceptionType DAMAGE_BUFFER_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.worldborder.damage.buffer.failed")
	);
	private static final SimpleCommandExceptionType DAMAGE_AMOUNT_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.worldborder.damage.amount.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("worldborder")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("add")
						.then(
							ServerCommandManager.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F))
								.executes(
									commandContext -> method_13854(
											commandContext.getSource(),
											commandContext.getSource().getWorld().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat(commandContext, "distance"),
											0L
										)
								)
								.then(
									ServerCommandManager.argument("time", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_13854(
													commandContext.getSource(),
													commandContext.getSource().getWorld().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat(commandContext, "distance"),
													commandContext.getSource().getWorld().getWorldBorder().getTargetRemainingTime()
														+ (long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("set")
						.then(
							ServerCommandManager.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F))
								.executes(commandContext -> method_13854(commandContext.getSource(), (double)FloatArgumentType.getFloat(commandContext, "distance"), 0L))
								.then(
									ServerCommandManager.argument("time", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_13854(
													commandContext.getSource(),
													(double)FloatArgumentType.getFloat(commandContext, "distance"),
													(long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("center")
						.then(
							ServerCommandManager.argument("pos", Vec2ArgumentType.create())
								.executes(commandContext -> method_13869(commandContext.getSource(), Vec2ArgumentType.getVec2Argument(commandContext, "pos")))
						)
				)
				.then(
					ServerCommandManager.literal("damage")
						.then(
							ServerCommandManager.literal("amount")
								.then(
									ServerCommandManager.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F))
										.executes(commandContext -> method_13863(commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "damagePerBlock")))
								)
						)
						.then(
							ServerCommandManager.literal("buffer")
								.then(
									ServerCommandManager.argument("distance", FloatArgumentType.floatArg(0.0F))
										.executes(commandContext -> method_13865(commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance")))
								)
						)
				)
				.then(ServerCommandManager.literal("get").executes(commandContext -> method_13868(commandContext.getSource())))
				.then(
					ServerCommandManager.literal("warning")
						.then(
							ServerCommandManager.literal("distance")
								.then(
									ServerCommandManager.argument("distance", IntegerArgumentType.integer(0))
										.executes(commandContext -> method_13859(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "distance")))
								)
						)
						.then(
							ServerCommandManager.literal("time")
								.then(
									ServerCommandManager.argument("time", IntegerArgumentType.integer(0))
										.executes(commandContext -> method_13856(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
								)
						)
				)
		);
	}

	private static int method_13865(ServerCommandSource serverCommandSource, float f) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
		if (worldBorder.getSafeZone() == (double)f) {
			throw DAMAGE_BUFFER_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setSafeZone((double)f);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", f)), true);
			return (int)f;
		}
	}

	private static int method_13863(ServerCommandSource serverCommandSource, float f) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
		if (worldBorder.getDamagePerBlock() == (double)f) {
			throw DAMAGE_AMOUNT_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setDamagePerBlock((double)f);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", f)), true);
			return (int)f;
		}
	}

	private static int method_13856(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
		if (worldBorder.getWarningTime() == i) {
			throw WARNING_TIME_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setWarningTime(i);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.worldborder.warning.time.success", i), true);
			return i;
		}
	}

	private static int method_13859(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
		if (worldBorder.getWarningBlocks() == i) {
			throw WARNING_DISTANCE_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setWarningBlocks(i);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.worldborder.warning.distance.success", i), true);
			return i;
		}
	}

	private static int method_13868(ServerCommandSource serverCommandSource) {
		double d = serverCommandSource.getWorld().getWorldBorder().getSize();
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d)), false);
		return MathHelper.floor(d + 0.5);
	}

	private static int method_13869(ServerCommandSource serverCommandSource, Vec2f vec2f) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
		if (worldBorder.getCenterX() == (double)vec2f.x && worldBorder.getCenterZ() == (double)vec2f.y) {
			throw CENTER_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setCenter((double)vec2f.x, (double)vec2f.y);
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", vec2f.x), String.format("%.2f", vec2f.y)), true
			);
			return 0;
		}
	}

	private static int method_13854(ServerCommandSource serverCommandSource, double d, long l) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().getWorldBorder();
		double e = worldBorder.getSize();
		if (e == d) {
			throw SET_FAILED_NOCHANGE_EXCEPTION.create();
		} else if (d < 1.0) {
			throw SET_FAILED_SMALL_EXCEPTION.create();
		} else if (d > 6.0E7) {
			throw SET_FAILED_BIG_EXCEPTION.create();
		} else {
			if (l > 0L) {
				worldBorder.interpolateSize(e, d, l);
				if (d > e) {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true
					);
				} else {
					serverCommandSource.sendFeedback(
						new TranslatableTextComponent("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true
					);
				}
			} else {
				worldBorder.setSize(d);
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", d)), true);
			}

			return (int)(d - e);
		}
	}
}
