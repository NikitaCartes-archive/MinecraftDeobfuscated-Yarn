package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("spreadplayers")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("center", Vec2ArgumentType.vec2())
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
																	commandContext.getSource().getWorld().getTopHeightLimit(),
																	BoolArgumentType.getBool(commandContext, "respectTeams"),
																	EntityArgumentType.getEntities(commandContext, "targets")
																)
														)
												)
										)
										.then(
											CommandManager.literal("under")
												.then(
													CommandManager.argument("maxHeight", IntegerArgumentType.integer(0))
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
																					IntegerArgumentType.getInteger(commandContext, "maxHeight"),
																					BoolArgumentType.getBool(commandContext, "respectTeams"),
																					EntityArgumentType.getEntities(commandContext, "targets")
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int execute(
		ServerCommandSource source, Vec2f center, float spreadDistance, float maxRange, int i, boolean bl, Collection<? extends Entity> collection
	) throws CommandSyntaxException {
		Random random = new Random();
		double d = (double)(center.x - maxRange);
		double e = (double)(center.y - maxRange);
		double f = (double)(center.x + maxRange);
		double g = (double)(center.y + maxRange);
		SpreadPlayersCommand.Pile[] piles = makePiles(random, bl ? getPileCountRespectingTeams(collection) : collection.size(), d, e, f, g);
		spread(center, (double)spreadDistance, source.getWorld(), random, d, e, f, g, i, piles, bl);
		double h = getMinDistance(collection, source.getWorld(), piles, i, bl);
		source.sendFeedback(
			new TranslatableText(
				"commands.spreadplayers.success." + (bl ? "teams" : "entities"), piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", h)
			),
			true
		);
		return piles.length;
	}

	private static int getPileCountRespectingTeams(Collection<? extends Entity> entities) {
		Set<AbstractTeam> set = Sets.<AbstractTeam>newHashSet();

		for (Entity entity : entities) {
			if (entity instanceof PlayerEntity) {
				set.add(entity.getScoreboardTeam());
			} else {
				set.add(null);
			}
		}

		return set.size();
	}

	private static void spread(
		Vec2f center,
		double spreadDistance,
		ServerWorld world,
		Random random,
		double minX,
		double minZ,
		double maxX,
		double maxZ,
		int i,
		SpreadPlayersCommand.Pile[] piles,
		boolean bl
	) throws CommandSyntaxException {
		boolean bl2 = true;
		double d = Float.MAX_VALUE;

		int j;
		for (j = 0; j < 10000 && bl2; j++) {
			bl2 = false;
			d = Float.MAX_VALUE;

			for (int k = 0; k < piles.length; k++) {
				SpreadPlayersCommand.Pile pile = piles[k];
				int l = 0;
				SpreadPlayersCommand.Pile pile2 = new SpreadPlayersCommand.Pile();

				for (int m = 0; m < piles.length; m++) {
					if (k != m) {
						SpreadPlayersCommand.Pile pile3 = piles[m];
						double e = pile.getDistance(pile3);
						d = Math.min(e, d);
						if (e < spreadDistance) {
							l++;
							pile2.x = pile2.x + (pile3.x - pile.x);
							pile2.z = pile2.z + (pile3.z - pile.z);
						}
					}
				}

				if (l > 0) {
					pile2.x = pile2.x / (double)l;
					pile2.z = pile2.z / (double)l;
					double f = (double)pile2.absolute();
					if (f > 0.0) {
						pile2.normalize();
						pile.subtract(pile2);
					} else {
						pile.setPileLocation(random, minX, minZ, maxX, maxZ);
					}

					bl2 = true;
				}

				if (pile.clamp(minX, minZ, maxX, maxZ)) {
					bl2 = true;
				}
			}

			if (!bl2) {
				for (SpreadPlayersCommand.Pile pile2 : piles) {
					if (!pile2.isSafe(world, i)) {
						pile2.setPileLocation(random, minX, minZ, maxX, maxZ);
						bl2 = true;
					}
				}
			}
		}

		if (d == Float.MAX_VALUE) {
			d = 0.0;
		}

		if (j >= 10000) {
			if (bl) {
				throw FAILED_TEAMS_EXCEPTION.create(piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", d));
			} else {
				throw FAILED_ENTITIES_EXCEPTION.create(piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", d));
			}
		}
	}

	private static double getMinDistance(Collection<? extends Entity> entities, ServerWorld world, SpreadPlayersCommand.Pile[] piles, int i, boolean bl) {
		double d = 0.0;
		int j = 0;
		Map<AbstractTeam, SpreadPlayersCommand.Pile> map = Maps.<AbstractTeam, SpreadPlayersCommand.Pile>newHashMap();

		for (Entity entity : entities) {
			SpreadPlayersCommand.Pile pile;
			if (bl) {
				AbstractTeam abstractTeam = entity instanceof PlayerEntity ? entity.getScoreboardTeam() : null;
				if (!map.containsKey(abstractTeam)) {
					map.put(abstractTeam, piles[j++]);
				}

				pile = (SpreadPlayersCommand.Pile)map.get(abstractTeam);
			} else {
				pile = piles[j++];
			}

			entity.teleport((double)MathHelper.floor(pile.x) + 0.5, (double)pile.getY(world, i), (double)MathHelper.floor(pile.z) + 0.5);
			double e = Double.MAX_VALUE;

			for (SpreadPlayersCommand.Pile pile2 : piles) {
				if (pile != pile2) {
					double f = pile.getDistance(pile2);
					e = Math.min(f, e);
				}
			}

			d += e;
		}

		return entities.size() < 2 ? 0.0 : d / (double)entities.size();
	}

	private static SpreadPlayersCommand.Pile[] makePiles(Random random, int count, double minX, double minZ, double maxX, double maxZ) {
		SpreadPlayersCommand.Pile[] piles = new SpreadPlayersCommand.Pile[count];

		for (int i = 0; i < piles.length; i++) {
			SpreadPlayersCommand.Pile pile = new SpreadPlayersCommand.Pile();
			pile.setPileLocation(random, minX, minZ, maxX, maxZ);
			piles[i] = pile;
		}

		return piles;
	}

	static class Pile {
		private double x;
		private double z;

		private Pile() {
		}

		double getDistance(SpreadPlayersCommand.Pile other) {
			double d = this.x - other.x;
			double e = this.z - other.z;
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

		public void subtract(SpreadPlayersCommand.Pile other) {
			this.x = this.x - other.x;
			this.z = this.z - other.z;
		}

		public boolean clamp(double minX, double minZ, double maxX, double maxZ) {
			boolean bl = false;
			if (this.x < minX) {
				this.x = minX;
				bl = true;
			} else if (this.x > maxX) {
				this.x = maxX;
				bl = true;
			}

			if (this.z < minZ) {
				this.z = minZ;
				bl = true;
			} else if (this.z > maxZ) {
				this.z = maxZ;
				bl = true;
			}

			return bl;
		}

		public int getY(BlockView blockView, int i) {
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.x, (double)(i + 1), this.z);
			boolean bl = blockView.getBlockState(mutable).isAir();
			mutable.move(Direction.DOWN);
			boolean bl2 = blockView.getBlockState(mutable).isAir();

			while (mutable.getY() > blockView.getBottomSectionLimit()) {
				mutable.move(Direction.DOWN);
				boolean bl3 = blockView.getBlockState(mutable).isAir();
				if (!bl3 && bl2 && bl) {
					return mutable.getY() + 1;
				}

				bl = bl2;
				bl2 = bl3;
			}

			return i + 1;
		}

		public boolean isSafe(BlockView world, int i) {
			BlockPos blockPos = new BlockPos(this.x, (double)(this.getY(world, i) - 1), this.z);
			BlockState blockState = world.getBlockState(blockPos);
			Material material = blockState.getMaterial();
			return blockPos.getY() < i && !material.isLiquid() && material != Material.FIRE;
		}

		public void setPileLocation(Random random, double minX, double minZ, double maxX, double maxZ) {
			this.x = MathHelper.nextDouble(random, minX, maxX);
			this.z = MathHelper.nextDouble(random, minZ, maxZ);
		}
	}
}
