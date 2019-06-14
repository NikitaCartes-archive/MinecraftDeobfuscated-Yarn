package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.command.arguments.Vec2ArgumentType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
	private static final SimpleCommandExceptionType CENTER_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.center.failed")
	);
	private static final SimpleCommandExceptionType SET_FAILED_NOCHANGE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.set.failed.nochange")
	);
	private static final SimpleCommandExceptionType SET_FAILED_SMALL_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.set.failed.small.")
	);
	private static final SimpleCommandExceptionType SET_FAILED_BIG_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.set.failed.big.")
	);
	private static final SimpleCommandExceptionType WARNING_TIME_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.warning.time.failed")
	);
	private static final SimpleCommandExceptionType WARNING_DISTANCE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.warning.distance.failed")
	);
	private static final SimpleCommandExceptionType DAMAGE_BUFFER_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.damage.buffer.failed")
	);
	private static final SimpleCommandExceptionType DAMAGE_AMOUNT_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.damage.amount.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("worldborder")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("add")
						.then(
							CommandManager.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F))
								.executes(
									commandContext -> executeSet(
											commandContext.getSource(),
											commandContext.getSource().getWorld().method_8621().getSize() + (double)FloatArgumentType.getFloat(commandContext, "distance"),
											0L
										)
								)
								.then(
									CommandManager.argument("time", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> executeSet(
													commandContext.getSource(),
													commandContext.getSource().getWorld().method_8621().getSize() + (double)FloatArgumentType.getFloat(commandContext, "distance"),
													commandContext.getSource().getWorld().method_8621().getTargetRemainingTime()
														+ (long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("set")
						.then(
							CommandManager.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F))
								.executes(commandContext -> executeSet(commandContext.getSource(), (double)FloatArgumentType.getFloat(commandContext, "distance"), 0L))
								.then(
									CommandManager.argument("time", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> executeSet(
													commandContext.getSource(),
													(double)FloatArgumentType.getFloat(commandContext, "distance"),
													(long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("center")
						.then(
							CommandManager.argument("pos", Vec2ArgumentType.create())
								.executes(commandContext -> executeCenter(commandContext.getSource(), Vec2ArgumentType.getVec2(commandContext, "pos")))
						)
				)
				.then(
					CommandManager.literal("damage")
						.then(
							CommandManager.literal("amount")
								.then(
									CommandManager.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F))
										.executes(commandContext -> executeDamage(commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "damagePerBlock")))
								)
						)
						.then(
							CommandManager.literal("buffer")
								.then(
									CommandManager.argument("distance", FloatArgumentType.floatArg(0.0F))
										.executes(commandContext -> executeBuffer(commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance")))
								)
						)
				)
				.then(CommandManager.literal("get").executes(commandContext -> executeGet(commandContext.getSource())))
				.then(
					CommandManager.literal("warning")
						.then(
							CommandManager.literal("distance")
								.then(
									CommandManager.argument("distance", IntegerArgumentType.integer(0))
										.executes(commandContext -> executeWarningDistance(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "distance")))
								)
						)
						.then(
							CommandManager.literal("time")
								.then(
									CommandManager.argument("time", IntegerArgumentType.integer(0))
										.executes(commandContext -> executeWarningTime(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
								)
						)
				)
		);
	}

	private static int executeBuffer(ServerCommandSource serverCommandSource, float f) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().method_8621();
		if (worldBorder.getBuffer() == (double)f) {
			throw DAMAGE_BUFFER_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setBuffer((double)f);
			serverCommandSource.sendFeedback(new TranslatableText("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", f)), true);
			return (int)f;
		}
	}

	private static int executeDamage(ServerCommandSource serverCommandSource, float f) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().method_8621();
		if (worldBorder.getDamagePerBlock() == (double)f) {
			throw DAMAGE_AMOUNT_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setDamagePerBlock((double)f);
			serverCommandSource.sendFeedback(new TranslatableText("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", f)), true);
			return (int)f;
		}
	}

	private static int executeWarningTime(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().method_8621();
		if (worldBorder.getWarningTime() == i) {
			throw WARNING_TIME_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setWarningTime(i);
			serverCommandSource.sendFeedback(new TranslatableText("commands.worldborder.warning.time.success", i), true);
			return i;
		}
	}

	private static int executeWarningDistance(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().method_8621();
		if (worldBorder.getWarningBlocks() == i) {
			throw WARNING_DISTANCE_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setWarningBlocks(i);
			serverCommandSource.sendFeedback(new TranslatableText("commands.worldborder.warning.distance.success", i), true);
			return i;
		}
	}

	private static int executeGet(ServerCommandSource serverCommandSource) {
		double d = serverCommandSource.getWorld().method_8621().getSize();
		serverCommandSource.sendFeedback(new TranslatableText("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d)), false);
		return MathHelper.floor(d + 0.5);
	}

	private static int executeCenter(ServerCommandSource serverCommandSource, Vec2f vec2f) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().method_8621();
		if (worldBorder.getCenterX() == (double)vec2f.x && worldBorder.getCenterZ() == (double)vec2f.y) {
			throw CENTER_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setCenter((double)vec2f.x, (double)vec2f.y);
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", vec2f.x), String.format("%.2f", vec2f.y)), true
			);
			return 0;
		}
	}

	private static int executeSet(ServerCommandSource serverCommandSource, double d, long l) throws CommandSyntaxException {
		WorldBorder worldBorder = serverCommandSource.getWorld().method_8621();
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
						new TranslatableText("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true
					);
				} else {
					serverCommandSource.sendFeedback(
						new TranslatableText("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true
					);
				}
			} else {
				worldBorder.setSize(d);
				serverCommandSource.sendFeedback(new TranslatableText("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", d)), true);
			}

			return (int)(d - e);
		}
	}
}
