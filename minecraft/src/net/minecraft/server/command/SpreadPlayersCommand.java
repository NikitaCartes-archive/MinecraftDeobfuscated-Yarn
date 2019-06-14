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
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;

public class SpreadPlayersCommand {
	private static final Dynamic4CommandExceptionType FAILED_TEAMS_EXCEPTION = new Dynamic4CommandExceptionType(
		(object, object2, object3, object4) -> new TranslatableText("commands.spreadplayers.failed.teams", object, object2, object3, object4)
	);
	private static final Dynamic4CommandExceptionType FAILED_ENTITIES_EXCEPTION = new Dynamic4CommandExceptionType(
		(object, object2, object3, object4) -> new TranslatableText("commands.spreadplayers.failed.entities", object, object2, object3, object4)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("spreadplayers")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("center", Vec2ArgumentType.create())
						.then(
							CommandManager.argument("spreadDistance", FloatArgumentType.floatArg(0.0F))
								.then(
									CommandManager.argument("maxRange", FloatArgumentType.floatArg(1.0F))
										.then(
											CommandManager.argument("respectTeams", BoolArgumentType.bool())
												.then(
													CommandManager.argument("targets", EntityArgumentType.entities())
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	Vec2ArgumentType.getVec2(commandContext, "center"),
																	FloatArgumentType.getFloat(commandContext, "spreadDistance"),
																	FloatArgumentType.getFloat(commandContext, "maxRange"),
																	BoolArgumentType.getBool(commandContext, "respectTeams"),
																	EntityArgumentType.getEntities(commandContext, "targets")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Vec2f vec2f, float f, float g, boolean bl, Collection<? extends Entity> collection) throws CommandSyntaxException {
		Random random = new Random();
		double d = (double)(vec2f.x - g);
		double e = (double)(vec2f.y - g);
		double h = (double)(vec2f.x + g);
		double i = (double)(vec2f.y + g);
		SpreadPlayersCommand.Pile[] piles = makePiles(random, bl ? getPileCountRespectingTeams(collection) : collection.size(), d, e, h, i);
		spread(vec2f, (double)f, serverCommandSource.getWorld(), random, d, e, h, i, piles, bl);
		double j = getMinimumDistance(collection, serverCommandSource.getWorld(), piles, bl);
		serverCommandSource.sendFeedback(
			new TranslatableText("commands.spreadplayers.success." + (bl ? "teams" : "entities"), piles.length, vec2f.x, vec2f.y, String.format(Locale.ROOT, "%.2f", j)),
			true
		);
		return piles.length;
	}

	private static int getPileCountRespectingTeams(Collection<? extends Entity> collection) {
		Set<AbstractTeam> set = Sets.<AbstractTeam>newHashSet();

		for (Entity entity : collection) {
			if (entity instanceof PlayerEntity) {
				set.add(entity.method_5781());
			} else {
				set.add(null);
			}
		}

		return set.size();
	}

	private static void spread(
		Vec2f vec2f, double d, ServerWorld serverWorld, Random random, double e, double f, double g, double h, SpreadPlayersCommand.Pile[] piles, boolean bl
	) throws CommandSyntaxException {
		boolean bl2 = true;
		double i = Float.MAX_VALUE;

		int j;
		for (j = 0; j < 10000 && bl2; j++) {
			bl2 = false;
			i = Float.MAX_VALUE;

			for (int k = 0; k < piles.length; k++) {
				SpreadPlayersCommand.Pile pile = piles[k];
				int l = 0;
				SpreadPlayersCommand.Pile pile2 = new SpreadPlayersCommand.Pile();

				for (int m = 0; m < piles.length; m++) {
					if (k != m) {
						SpreadPlayersCommand.Pile pile3 = piles[m];
						double n = pile.getDistance(pile3);
						i = Math.min(n, i);
						if (n < d) {
							l++;
							pile2.x = pile2.x + (pile3.x - pile.x);
							pile2.z = pile2.z + (pile3.z - pile.z);
						}
					}
				}

				if (l > 0) {
					pile2.x = pile2.x / (double)l;
					pile2.z = pile2.z / (double)l;
					double o = (double)pile2.absolute();
					if (o > 0.0) {
						pile2.normalize();
						pile.subtract(pile2);
					} else {
						pile.setPileLocation(random, e, f, g, h);
					}

					bl2 = true;
				}

				if (pile.clamp(e, f, g, h)) {
					bl2 = true;
				}
			}

			if (!bl2) {
				for (SpreadPlayersCommand.Pile pile2 : piles) {
					if (!pile2.isSafe(serverWorld)) {
						pile2.setPileLocation(random, e, f, g, h);
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
				throw FAILED_TEAMS_EXCEPTION.create(piles.length, vec2f.x, vec2f.y, String.format(Locale.ROOT, "%.2f", i));
			} else {
				throw FAILED_ENTITIES_EXCEPTION.create(piles.length, vec2f.x, vec2f.y, String.format(Locale.ROOT, "%.2f", i));
			}
		}
	}

	private static double getMinimumDistance(Collection<? extends Entity> collection, ServerWorld serverWorld, SpreadPlayersCommand.Pile[] piles, boolean bl) {
		double d = 0.0;
		int i = 0;
		Map<AbstractTeam, SpreadPlayersCommand.Pile> map = Maps.<AbstractTeam, SpreadPlayersCommand.Pile>newHashMap();

		for (Entity entity : collection) {
			SpreadPlayersCommand.Pile pile;
			if (bl) {
				AbstractTeam abstractTeam = entity instanceof PlayerEntity ? entity.method_5781() : null;
				if (!map.containsKey(abstractTeam)) {
					map.put(abstractTeam, piles[i++]);
				}

				pile = (SpreadPlayersCommand.Pile)map.get(abstractTeam);
			} else {
				pile = piles[i++];
			}

			entity.method_20620((double)((float)MathHelper.floor(pile.x) + 0.5F), (double)pile.getY(serverWorld), (double)MathHelper.floor(pile.z) + 0.5);
			double e = Double.MAX_VALUE;

			for (SpreadPlayersCommand.Pile pile2 : piles) {
				if (pile != pile2) {
					double f = pile.getDistance(pile2);
					e = Math.min(f, e);
				}
			}

			d += e;
		}

		return collection.size() < 2 ? 0.0 : d / (double)collection.size();
	}

	private static SpreadPlayersCommand.Pile[] makePiles(Random random, int i, double d, double e, double f, double g) {
		SpreadPlayersCommand.Pile[] piles = new SpreadPlayersCommand.Pile[i];

		for (int j = 0; j < piles.length; j++) {
			SpreadPlayersCommand.Pile pile = new SpreadPlayersCommand.Pile();
			pile.setPileLocation(random, d, e, f, g);
			piles[j] = pile;
		}

		return piles;
	}

	static class Pile {
		private double x;
		private double z;

		double getDistance(SpreadPlayersCommand.Pile pile) {
			double d = this.x - pile.x;
			double e = this.z - pile.z;
			return Math.sqrt(d * d + e * e);
		}

		void normalize() {
			double d = (double)this.absolute();
			this.x /= d;
			this.z /= d;
		}

		float absolute() {
			return MathHelper.sqrt(this.x * this.x + this.z * this.z);
		}

		public void subtract(SpreadPlayersCommand.Pile pile) {
			this.x = this.x - pile.x;
			this.z = this.z - pile.z;
		}

		public boolean clamp(double d, double e, double f, double g) {
			boolean bl = false;
			if (this.x < d) {
				this.x = d;
				bl = true;
			} else if (this.x > f) {
				this.x = f;
				bl = true;
			}

			if (this.z < e) {
				this.z = e;
				bl = true;
			} else if (this.z > g) {
				this.z = g;
				bl = true;
			}

			return bl;
		}

		public int getY(BlockView blockView) {
			BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);

			while (blockPos.getY() > 0) {
				blockPos = blockPos.down();
				if (!blockView.method_8320(blockPos).isAir()) {
					return blockPos.getY() + 1;
				}
			}

			return 257;
		}

		public boolean isSafe(BlockView blockView) {
			BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);

			while (blockPos.getY() > 0) {
				blockPos = blockPos.down();
				BlockState blockState = blockView.method_8320(blockPos);
				if (!blockState.isAir()) {
					Material material = blockState.method_11620();
					return !material.isLiquid() && material != Material.FIRE;
				}
			}

			return false;
		}

		public void setPileLocation(Random random, double d, double e, double f, double g) {
			this.x = MathHelper.nextDouble(random, d, f);
			this.z = MathHelper.nextDouble(random, e, g);
		}
	}
}
