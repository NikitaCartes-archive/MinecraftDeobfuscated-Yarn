package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;

public class class_3158 {
	private static final SimpleCommandExceptionType field_13775 = new SimpleCommandExceptionType(new class_2588("commands.worldborder.center.failed"));
	private static final SimpleCommandExceptionType field_13780 = new SimpleCommandExceptionType(new class_2588("commands.worldborder.set.failed.nochange"));
	private static final SimpleCommandExceptionType field_13776 = new SimpleCommandExceptionType(new class_2588("commands.worldborder.set.failed.small."));
	private static final SimpleCommandExceptionType field_13779 = new SimpleCommandExceptionType(new class_2588("commands.worldborder.set.failed.big."));
	private static final SimpleCommandExceptionType field_13773 = new SimpleCommandExceptionType(new class_2588("commands.worldborder.warning.time.failed"));
	private static final SimpleCommandExceptionType field_13777 = new SimpleCommandExceptionType(new class_2588("commands.worldborder.warning.distance.failed"));
	private static final SimpleCommandExceptionType field_13778 = new SimpleCommandExceptionType(new class_2588("commands.worldborder.damage.buffer.failed"));
	private static final SimpleCommandExceptionType field_13774 = new SimpleCommandExceptionType(new class_2588("commands.worldborder.damage.amount.failed"));

	public static void method_13858(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("worldborder")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9247("add")
						.then(
							class_2170.method_9244("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F))
								.executes(
									commandContext -> method_13854(
											commandContext.getSource(),
											commandContext.getSource().method_9225().method_8621().method_11965() + (double)FloatArgumentType.getFloat(commandContext, "distance"),
											0L
										)
								)
								.then(
									class_2170.method_9244("time", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_13854(
													commandContext.getSource(),
													commandContext.getSource().method_9225().method_8621().method_11965() + (double)FloatArgumentType.getFloat(commandContext, "distance"),
													commandContext.getSource().method_9225().method_8621().method_11962() + (long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L
												)
										)
								)
						)
				)
				.then(
					class_2170.method_9247("set")
						.then(
							class_2170.method_9244("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F))
								.executes(commandContext -> method_13854(commandContext.getSource(), (double)FloatArgumentType.getFloat(commandContext, "distance"), 0L))
								.then(
									class_2170.method_9244("time", IntegerArgumentType.integer(0))
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
					class_2170.method_9247("center")
						.then(
							class_2170.method_9244("pos", class_2274.method_9723())
								.executes(commandContext -> method_13869(commandContext.getSource(), class_2274.method_9724(commandContext, "pos")))
						)
				)
				.then(
					class_2170.method_9247("damage")
						.then(
							class_2170.method_9247("amount")
								.then(
									class_2170.method_9244("damagePerBlock", FloatArgumentType.floatArg(0.0F))
										.executes(commandContext -> method_13863(commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "damagePerBlock")))
								)
						)
						.then(
							class_2170.method_9247("buffer")
								.then(
									class_2170.method_9244("distance", FloatArgumentType.floatArg(0.0F))
										.executes(commandContext -> method_13865(commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance")))
								)
						)
				)
				.then(class_2170.method_9247("get").executes(commandContext -> method_13868(commandContext.getSource())))
				.then(
					class_2170.method_9247("warning")
						.then(
							class_2170.method_9247("distance")
								.then(
									class_2170.method_9244("distance", IntegerArgumentType.integer(0))
										.executes(commandContext -> method_13859(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "distance")))
								)
						)
						.then(
							class_2170.method_9247("time")
								.then(
									class_2170.method_9244("time", IntegerArgumentType.integer(0))
										.executes(commandContext -> method_13856(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
								)
						)
				)
		);
	}

	private static int method_13865(class_2168 arg, float f) throws CommandSyntaxException {
		class_2784 lv = arg.method_9225().method_8621();
		if (lv.method_11971() == (double)f) {
			throw field_13778.create();
		} else {
			lv.method_11981((double)f);
			arg.method_9226(new class_2588("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", f)), true);
			return (int)f;
		}
	}

	private static int method_13863(class_2168 arg, float f) throws CommandSyntaxException {
		class_2784 lv = arg.method_9225().method_8621();
		if (lv.method_11953() == (double)f) {
			throw field_13774.create();
		} else {
			lv.method_11955((double)f);
			arg.method_9226(new class_2588("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", f)), true);
			return (int)f;
		}
	}

	private static int method_13856(class_2168 arg, int i) throws CommandSyntaxException {
		class_2784 lv = arg.method_9225().method_8621();
		if (lv.method_11956() == i) {
			throw field_13773.create();
		} else {
			lv.method_11975(i);
			arg.method_9226(new class_2588("commands.worldborder.warning.time.success", i), true);
			return i;
		}
	}

	private static int method_13859(class_2168 arg, int i) throws CommandSyntaxException {
		class_2784 lv = arg.method_9225().method_8621();
		if (lv.method_11972() == i) {
			throw field_13777.create();
		} else {
			lv.method_11967(i);
			arg.method_9226(new class_2588("commands.worldborder.warning.distance.success", i), true);
			return i;
		}
	}

	private static int method_13868(class_2168 arg) {
		double d = arg.method_9225().method_8621().method_11965();
		arg.method_9226(new class_2588("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d)), false);
		return class_3532.method_15357(d + 0.5);
	}

	private static int method_13869(class_2168 arg, class_241 arg2) throws CommandSyntaxException {
		class_2784 lv = arg.method_9225().method_8621();
		if (lv.method_11964() == (double)arg2.field_1343 && lv.method_11980() == (double)arg2.field_1342) {
			throw field_13775.create();
		} else {
			lv.method_11978((double)arg2.field_1343, (double)arg2.field_1342);
			arg.method_9226(
				new class_2588("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", arg2.field_1343), String.format("%.2f", arg2.field_1342)), true
			);
			return 0;
		}
	}

	private static int method_13854(class_2168 arg, double d, long l) throws CommandSyntaxException {
		class_2784 lv = arg.method_9225().method_8621();
		double e = lv.method_11965();
		if (e == d) {
			throw field_13780.create();
		} else if (d < 1.0) {
			throw field_13776.create();
		} else if (d > 6.0E7) {
			throw field_13779.create();
		} else {
			if (l > 0L) {
				lv.method_11957(e, d, l);
				if (d > e) {
					arg.method_9226(new class_2588("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
				} else {
					arg.method_9226(new class_2588("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
				}
			} else {
				lv.method_11969(d);
				arg.method_9226(new class_2588("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", d)), true);
			}

			return (int)(d - e);
		}
	}
}
