package net.minecraft.server.command;

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
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.Vec2ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;

public class SpreadPlayersCommand {
	private static final Dynamic4CommandExceptionType FAILED_TEAMS_EXCEPTION = new Dynamic4CommandExceptionType(
		(object, object2, object3, object4) -> new TranslatableTextComponent("commands.spreadplayers.failed.teams", object, object2, object3, object4)
	);
	private static final Dynamic4CommandExceptionType FAILED_ENTITIES_EXCEPTION = new Dynamic4CommandExceptionType(
		(object, object2, object3, object4) -> new TranslatableTextComponent("commands.spreadplayers.failed.entities", object, object2, object3, object4)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("spreadplayers")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("center", Vec2ArgumentType.create())
						.then(
							ServerCommandManager.argument("spreadDistance", FloatArgumentType.floatArg(0.0F))
								.then(
									ServerCommandManager.argument("maxRange", FloatArgumentType.floatArg(1.0F))
										.then(
											ServerCommandManager.argument("respectTeams", BoolArgumentType.bool())
												.then(
													ServerCommandManager.argument("targets", EntityArgumentType.method_9306())
														.executes(
															commandContext -> method_13656(
																	commandContext.getSource(),
																	Vec2ArgumentType.getVec2Argument(commandContext, "center"),
																	FloatArgumentType.getFloat(commandContext, "spreadDistance"),
																	FloatArgumentType.getFloat(commandContext, "maxRange"),
																	BoolArgumentType.getBool(commandContext, "respectTeams"),
																	EntityArgumentType.method_9317(commandContext, "targets")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13656(ServerCommandSource serverCommandSource, Vec2f vec2f, float f, float g, boolean bl, Collection<? extends Entity> collection) throws CommandSyntaxException {
		Random random = new Random();
		double d = (double)(vec2f.x - g);
		double e = (double)(vec2f.y - g);
		double h = (double)(vec2f.x + g);
		double i = (double)(vec2f.y + g);
		SpreadPlayersCommand.class_3132[] lvs = method_13653(random, bl ? method_13652(collection) : collection.size(), d, e, h, i);
		method_13661(vec2f, (double)f, serverCommandSource.getWorld(), random, d, e, h, i, lvs, bl);
		double j = method_13657(collection, serverCommandSource.getWorld(), lvs, bl);
		serverCommandSource.sendFeedback(
			new TranslatableTextComponent(
				"commands.spreadplayers.success." + (bl ? "teams" : "entities"), lvs.length, vec2f.x, vec2f.y, String.format(Locale.ROOT, "%.2f", j)
			),
			true
		);
		return lvs.length;
	}

	private static int method_13652(Collection<? extends Entity> collection) {
		Set<AbstractScoreboardTeam> set = Sets.<AbstractScoreboardTeam>newHashSet();

		for (Entity entity : collection) {
			if (entity instanceof PlayerEntity) {
				set.add(entity.getScoreboardTeam());
			} else {
				set.add(null);
			}
		}

		return set.size();
	}

	private static void method_13661(
		Vec2f vec2f, double d, ServerWorld serverWorld, Random random, double e, double f, double g, double h, SpreadPlayersCommand.class_3132[] args, boolean bl
	) throws CommandSyntaxException {
		boolean bl2 = true;
		double i = Float.MAX_VALUE;

		int j;
		for (j = 0; j < 10000 && bl2; j++) {
			bl2 = false;
			i = Float.MAX_VALUE;

			for (int k = 0; k < args.length; k++) {
				SpreadPlayersCommand.class_3132 lv = args[k];
				int l = 0;
				SpreadPlayersCommand.class_3132 lv2 = new SpreadPlayersCommand.class_3132();

				for (int m = 0; m < args.length; m++) {
					if (k != m) {
						SpreadPlayersCommand.class_3132 lv3 = args[m];
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
				for (SpreadPlayersCommand.class_3132 lv2 : args) {
					if (!lv2.method_13662(serverWorld)) {
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
				throw FAILED_TEAMS_EXCEPTION.create(args.length, vec2f.x, vec2f.y, String.format(Locale.ROOT, "%.2f", i));
			} else {
				throw FAILED_ENTITIES_EXCEPTION.create(args.length, vec2f.x, vec2f.y, String.format(Locale.ROOT, "%.2f", i));
			}
		}
	}

	private static double method_13657(Collection<? extends Entity> collection, ServerWorld serverWorld, SpreadPlayersCommand.class_3132[] args, boolean bl) {
		double d = 0.0;
		int i = 0;
		Map<AbstractScoreboardTeam, SpreadPlayersCommand.class_3132> map = Maps.<AbstractScoreboardTeam, SpreadPlayersCommand.class_3132>newHashMap();

		for (Entity entity : collection) {
			SpreadPlayersCommand.class_3132 lv;
			if (bl) {
				AbstractScoreboardTeam abstractScoreboardTeam = entity instanceof PlayerEntity ? entity.getScoreboardTeam() : null;
				if (!map.containsKey(abstractScoreboardTeam)) {
					map.put(abstractScoreboardTeam, args[i++]);
				}

				lv = (SpreadPlayersCommand.class_3132)map.get(abstractScoreboardTeam);
			} else {
				lv = args[i++];
			}

			entity.method_5859(
				(double)((float)MathHelper.floor(lv.field_13737) + 0.5F), (double)lv.method_13669(serverWorld), (double)MathHelper.floor(lv.field_13736) + 0.5
			);
			double e = Double.MAX_VALUE;

			for (SpreadPlayersCommand.class_3132 lv2 : args) {
				if (lv != lv2) {
					double f = lv.method_13665(lv2);
					e = Math.min(f, e);
				}
			}

			d += e;
		}

		return collection.size() < 2 ? 0.0 : d / (double)collection.size();
	}

	private static SpreadPlayersCommand.class_3132[] method_13653(Random random, int i, double d, double e, double f, double g) {
		SpreadPlayersCommand.class_3132[] lvs = new SpreadPlayersCommand.class_3132[i];

		for (int j = 0; j < lvs.length; j++) {
			SpreadPlayersCommand.class_3132 lv = new SpreadPlayersCommand.class_3132();
			lv.method_13667(random, d, e, f, g);
			lvs[j] = lv;
		}

		return lvs;
	}

	static class class_3132 {
		private double field_13737;
		private double field_13736;

		double method_13665(SpreadPlayersCommand.class_3132 arg) {
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
			return MathHelper.sqrt(this.field_13737 * this.field_13737 + this.field_13736 * this.field_13736);
		}

		public void method_13670(SpreadPlayersCommand.class_3132 arg) {
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

		public int method_13669(BlockView blockView) {
			BlockPos blockPos = new BlockPos(this.field_13737, 256.0, this.field_13736);

			while (blockPos.getY() > 0) {
				blockPos = blockPos.down();
				if (!blockView.getBlockState(blockPos).isAir()) {
					return blockPos.getY() + 1;
				}
			}

			return 257;
		}

		public boolean method_13662(BlockView blockView) {
			BlockPos blockPos = new BlockPos(this.field_13737, 256.0, this.field_13736);

			while (blockPos.getY() > 0) {
				blockPos = blockPos.down();
				BlockState blockState = blockView.getBlockState(blockPos);
				if (!blockState.isAir()) {
					Material material = blockState.getMaterial();
					return !material.method_15797() && material != Material.FIRE;
				}
			}

			return false;
		}

		public void method_13667(Random random, double d, double e, double f, double g) {
			this.field_13737 = MathHelper.nextDouble(random, d, f);
			this.field_13736 = MathHelper.nextDouble(random, e, g);
		}
	}
}
