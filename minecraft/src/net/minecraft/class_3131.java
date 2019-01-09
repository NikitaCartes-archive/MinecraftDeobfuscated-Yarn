package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class class_3131 {
	private static final Dynamic4CommandExceptionType field_13734 = new Dynamic4CommandExceptionType(
		(object, object2, object3, object4) -> new class_2588("commands.spreadplayers.failed.teams", object, object2, object3, object4)
	);
	private static final Dynamic4CommandExceptionType field_13735 = new Dynamic4CommandExceptionType(
		(object, object2, object3, object4) -> new class_2588("commands.spreadplayers.failed.entities", object, object2, object3, object4)
	);

	public static void method_13654(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("spreadplayers")
				.requires(arg -> arg.method_9259(2))
				.then(
					class_2170.method_9244("center", class_2274.method_9723())
						.then(
							class_2170.method_9244("spreadDistance", FloatArgumentType.floatArg(0.0F))
								.then(
									class_2170.method_9244("maxRange", FloatArgumentType.floatArg(1.0F))
										.then(
											class_2170.method_9244("respectTeams", BoolArgumentType.bool())
												.then(
													class_2170.method_9244("targets", class_2186.method_9306())
														.executes(
															commandContext -> method_13656(
																	commandContext.getSource(),
																	class_2274.method_9724(commandContext, "center"),
																	FloatArgumentType.getFloat(commandContext, "spreadDistance"),
																	FloatArgumentType.getFloat(commandContext, "maxRange"),
																	BoolArgumentType.getBool(commandContext, "respectTeams"),
																	class_2186.method_9317(commandContext, "targets")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13656(class_2168 arg, class_241 arg2, float f, float g, boolean bl, Collection<? extends class_1297> collection) throws CommandSyntaxException {
		Random random = new Random();
		double d = (double)(arg2.field_1343 - g);
		double e = (double)(arg2.field_1342 - g);
		double h = (double)(arg2.field_1343 + g);
		double i = (double)(arg2.field_1342 + g);
		class_3131.class_3132[] lvs = method_13653(random, bl ? method_13652(collection) : collection.size(), d, e, h, i);
		method_13661(arg2, (double)f, arg.method_9225(), random, d, e, h, i, lvs, bl);
		double j = method_13657(collection, arg.method_9225(), lvs, bl);
		arg.method_9226(
			new class_2588(
				"commands.spreadplayers.success." + (bl ? "teams" : "entities"), lvs.length, arg2.field_1343, arg2.field_1342, String.format(Locale.ROOT, "%.2f", j)
			),
			true
		);
		return lvs.length;
	}

	private static int method_13652(Collection<? extends class_1297> collection) {
		Set<class_270> set = Sets.<class_270>newHashSet();

		for (class_1297 lv : collection) {
			if (lv instanceof class_1657) {
				set.add(lv.method_5781());
			} else {
				set.add(null);
			}
		}

		return set.size();
	}

	private static void method_13661(
		class_241 arg, double d, class_3218 arg2, Random random, double e, double f, double g, double h, class_3131.class_3132[] args, boolean bl
	) throws CommandSyntaxException {
		boolean bl2 = true;
		double i = Float.MAX_VALUE;

		int j;
		for (j = 0; j < 10000 && bl2; j++) {
			bl2 = false;
			i = Float.MAX_VALUE;

			for (int k = 0; k < args.length; k++) {
				class_3131.class_3132 lv = args[k];
				int l = 0;
				class_3131.class_3132 lv2 = new class_3131.class_3132();

				for (int m = 0; m < args.length; m++) {
					if (k != m) {
						class_3131.class_3132 lv3 = args[m];
						double n = lv.method_13665(lv3);
						i = Math.min(n, i);
						if (n < d) {
							l++;
							lv2.field_13737 = lv2.field_13737 + (lv3.field_13737 - lv.field_13737);
							lv2.field_13736 = lv2.field_13736 + (lv3.field_13736 - lv.field_13736);
						}
					}
				}

				if (l > 0) {
					lv2.field_13737 = lv2.field_13737 / (double)l;
					lv2.field_13736 = lv2.field_13736 / (double)l;
					double o = (double)lv2.method_13668();
					if (o > 0.0) {
						lv2.method_13671();
						lv.method_13670(lv2);
					} else {
						lv.method_13667(random, e, f, g, h);
					}

					bl2 = true;
				}

				if (lv.method_13666(e, f, g, h)) {
					bl2 = true;
				}
			}

			if (!bl2) {
				for (class_3131.class_3132 lv2 : args) {
					if (!lv2.method_13662(arg2)) {
						lv2.method_13667(random, e, f, g, h);
						bl2 = true;
					}
				}
			}
		}

		if (i == Float.MAX_VALUE) {
			i = 0.0;
		}

		if (j >= 10000) {
			if (bl) {
				throw field_13734.create(args.length, arg.field_1343, arg.field_1342, String.format(Locale.ROOT, "%.2f", i));
			} else {
				throw field_13735.create(args.length, arg.field_1343, arg.field_1342, String.format(Locale.ROOT, "%.2f", i));
			}
		}
	}

	private static double method_13657(Collection<? extends class_1297> collection, class_3218 arg, class_3131.class_3132[] args, boolean bl) {
		double d = 0.0;
		int i = 0;
		Map<class_270, class_3131.class_3132> map = Maps.<class_270, class_3131.class_3132>newHashMap();

		for (class_1297 lv : collection) {
			class_3131.class_3132 lv3;
			if (bl) {
				class_270 lv2 = lv instanceof class_1657 ? lv.method_5781() : null;
				if (!map.containsKey(lv2)) {
					map.put(lv2, args[i++]);
				}

				lv3 = (class_3131.class_3132)map.get(lv2);
			} else {
				lv3 = args[i++];
			}

			lv.method_5859(
				(double)((float)class_3532.method_15357(lv3.field_13737) + 0.5F), (double)lv3.method_13669(arg), (double)class_3532.method_15357(lv3.field_13736) + 0.5
			);
			double e = Double.MAX_VALUE;

			for (class_3131.class_3132 lv4 : args) {
				if (lv3 != lv4) {
					double f = lv3.method_13665(lv4);
					e = Math.min(f, e);
				}
			}

			d += e;
		}

		return collection.size() < 2 ? 0.0 : d / (double)collection.size();
	}

	private static class_3131.class_3132[] method_13653(Random random, int i, double d, double e, double f, double g) {
		class_3131.class_3132[] lvs = new class_3131.class_3132[i];

		for (int j = 0; j < lvs.length; j++) {
			class_3131.class_3132 lv = new class_3131.class_3132();
			lv.method_13667(random, d, e, f, g);
			lvs[j] = lv;
		}

		return lvs;
	}

	static class class_3132 {
		private double field_13737;
		private double field_13736;

		double method_13665(class_3131.class_3132 arg) {
			double d = this.field_13737 - arg.field_13737;
			double e = this.field_13736 - arg.field_13736;
			return Math.sqrt(d * d + e * e);
		}

		void method_13671() {
			double d = (double)this.method_13668();
			this.field_13737 /= d;
			this.field_13736 /= d;
		}

		float method_13668() {
			return class_3532.method_15368(this.field_13737 * this.field_13737 + this.field_13736 * this.field_13736);
		}

		public void method_13670(class_3131.class_3132 arg) {
			this.field_13737 = this.field_13737 - arg.field_13737;
			this.field_13736 = this.field_13736 - arg.field_13736;
		}

		public boolean method_13666(double d, double e, double f, double g) {
			boolean bl = false;
			if (this.field_13737 < d) {
				this.field_13737 = d;
				bl = true;
			} else if (this.field_13737 > f) {
				this.field_13737 = f;
				bl = true;
			}

			if (this.field_13736 < e) {
				this.field_13736 = e;
				bl = true;
			} else if (this.field_13736 > g) {
				this.field_13736 = g;
				bl = true;
			}

			return bl;
		}

		public int method_13669(class_1922 arg) {
			class_2338 lv = new class_2338(this.field_13737, 256.0, this.field_13736);

			while (lv.method_10264() > 0) {
				lv = lv.method_10074();
				if (!arg.method_8320(lv).method_11588()) {
					return lv.method_10264() + 1;
				}
			}

			return 257;
		}

		public boolean method_13662(class_1922 arg) {
			class_2338 lv = new class_2338(this.field_13737, 256.0, this.field_13736);

			while (lv.method_10264() > 0) {
				lv = lv.method_10074();
				class_2680 lv2 = arg.method_8320(lv);
				if (!lv2.method_11588()) {
					class_3614 lv3 = lv2.method_11620();
					return !lv3.method_15797() && lv3 != class_3614.field_15943;
				}
			}

			return false;
		}

		public void method_13667(Random random, double d, double e, double f, double g) {
			this.field_13737 = class_3532.method_15366(random, d, f);
			this.field_13736 = class_3532.method_15366(random, e, g);
		}
	}
}
