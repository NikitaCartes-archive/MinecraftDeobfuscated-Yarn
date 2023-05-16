package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;

public class SpreadPlayersCommand {
	private static final int MAX_ATTEMPTS = 10000;
	private static final Dynamic4CommandExceptionType FAILED_TEAMS_EXCEPTION = new Dynamic4CommandExceptionType(
		(pilesCount, x, z, maxSpreadDistance) -> Text.translatable("commands.spreadplayers.failed.teams", pilesCount, x, z, maxSpreadDistance)
	);
	private static final Dynamic4CommandExceptionType FAILED_ENTITIES_EXCEPTION = new Dynamic4CommandExceptionType(
		(pilesCount, x, z, maxSpreadDistance) -> Text.translatable("commands.spreadplayers.failed.entities", pilesCount, x, z, maxSpreadDistance)
	);
	private static final Dynamic2CommandExceptionType INVALID_HEIGHT_EXCEPTION = new Dynamic2CommandExceptionType(
		(maxY, worldBottomY) -> Text.translatable("commands.spreadplayers.failed.invalid.height", maxY, worldBottomY)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("spreadplayers")
				.requires(source -> source.hasPermissionLevel(2))
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
															context -> execute(
																	context.getSource(),
																	Vec2ArgumentType.getVec2(context, "center"),
																	FloatArgumentType.getFloat(context, "spreadDistance"),
																	FloatArgumentType.getFloat(context, "maxRange"),
																	context.getSource().getWorld().getTopY(),
																	BoolArgumentType.getBool(context, "respectTeams"),
																	EntityArgumentType.getEntities(context, "targets")
																)
														)
												)
										)
										.then(
											CommandManager.literal("under")
												.then(
													CommandManager.argument("maxHeight", IntegerArgumentType.integer())
														.then(
															CommandManager.argument("respectTeams", BoolArgumentType.bool())
																.then(
																	CommandManager.argument("targets", EntityArgumentType.entities())
																		.executes(
																			context -> execute(
																					context.getSource(),
																					Vec2ArgumentType.getVec2(context, "center"),
																					FloatArgumentType.getFloat(context, "spreadDistance"),
																					FloatArgumentType.getFloat(context, "maxRange"),
																					IntegerArgumentType.getInteger(context, "maxHeight"),
																					BoolArgumentType.getBool(context, "respectTeams"),
																					EntityArgumentType.getEntities(context, "targets")
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
		ServerCommandSource source, Vec2f center, float spreadDistance, float maxRange, int maxY, boolean respectTeams, Collection<? extends Entity> players
	) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		int i = serverWorld.getBottomY();
		if (maxY < i) {
			throw INVALID_HEIGHT_EXCEPTION.create(maxY, i);
		} else {
			Random random = Random.create();
			double d = (double)(center.x - maxRange);
			double e = (double)(center.y - maxRange);
			double f = (double)(center.x + maxRange);
			double g = (double)(center.y + maxRange);
			SpreadPlayersCommand.Pile[] piles = makePiles(random, respectTeams ? getPileCountRespectingTeams(players) : players.size(), d, e, f, g);
			spread(center, (double)spreadDistance, serverWorld, random, d, e, f, g, maxY, piles, respectTeams);
			double h = getMinDistance(players, serverWorld, piles, maxY, respectTeams);
			source.sendFeedback(
				() -> Text.translatable(
						"commands.spreadplayers.success." + (respectTeams ? "teams" : "entities"), piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", h)
					),
				true
			);
			return piles.length;
		}
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
		int maxY,
		SpreadPlayersCommand.Pile[] piles,
		boolean respectTeams
	) throws CommandSyntaxException {
		boolean bl = true;
		double d = Float.MAX_VALUE;

		int i;
		for (i = 0; i < 10000 && bl; i++) {
			bl = false;
			d = Float.MAX_VALUE;

			for (int j = 0; j < piles.length; j++) {
				SpreadPlayersCommand.Pile pile = piles[j];
				int k = 0;
				SpreadPlayersCommand.Pile pile2 = new SpreadPlayersCommand.Pile();

				for (int l = 0; l < piles.length; l++) {
					if (j != l) {
						SpreadPlayersCommand.Pile pile3 = piles[l];
						double e = pile.getDistance(pile3);
						d = Math.min(e, d);
						if (e < spreadDistance) {
							k++;
							pile2.x = pile2.x + (pile3.x - pile.x);
							pile2.z = pile2.z + (pile3.z - pile.z);
						}
					}
				}

				if (k > 0) {
					pile2.x /= (double)k;
					pile2.z /= (double)k;
					double f = pile2.absolute();
					if (f > 0.0) {
						pile2.normalize();
						pile.subtract(pile2);
					} else {
						pile.setPileLocation(random, minX, minZ, maxX, maxZ);
					}

					bl = true;
				}

				if (pile.clamp(minX, minZ, maxX, maxZ)) {
					bl = true;
				}
			}

			if (!bl) {
				for (SpreadPlayersCommand.Pile pile2 : piles) {
					if (!pile2.isSafe(world, maxY)) {
						pile2.setPileLocation(random, minX, minZ, maxX, maxZ);
						bl = true;
					}
				}
			}
		}

		if (d == Float.MAX_VALUE) {
			d = 0.0;
		}

		if (i >= 10000) {
			if (respectTeams) {
				throw FAILED_TEAMS_EXCEPTION.create(piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", d));
			} else {
				throw FAILED_ENTITIES_EXCEPTION.create(piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", d));
			}
		}
	}

	private static double getMinDistance(
		Collection<? extends Entity> entities, ServerWorld world, SpreadPlayersCommand.Pile[] piles, int maxY, boolean respectTeams
	) {
		double d = 0.0;
		int i = 0;
		Map<AbstractTeam, SpreadPlayersCommand.Pile> map = Maps.<AbstractTeam, SpreadPlayersCommand.Pile>newHashMap();

		for (Entity entity : entities) {
			SpreadPlayersCommand.Pile pile;
			if (respectTeams) {
				AbstractTeam abstractTeam = entity instanceof PlayerEntity ? entity.getScoreboardTeam() : null;
				if (!map.containsKey(abstractTeam)) {
					map.put(abstractTeam, piles[i++]);
				}

				pile = (SpreadPlayersCommand.Pile)map.get(abstractTeam);
			} else {
				pile = piles[i++];
			}

			entity.teleport(
				world,
				(double)MathHelper.floor(pile.x) + 0.5,
				(double)pile.getY(world, maxY),
				(double)MathHelper.floor(pile.z) + 0.5,
				Set.of(),
				entity.getYaw(),
				entity.getPitch()
			);
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
		double x;
		double z;

		double getDistance(SpreadPlayersCommand.Pile other) {
			double d = this.x - other.x;
			double e = this.z - other.z;
			return Math.sqrt(d * d + e * e);
		}

		void normalize() {
			double d = this.absolute();
			this.x /= d;
			this.z /= d;
		}

		double absolute() {
			return Math.sqrt(this.x * this.x + this.z * this.z);
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

		public int getY(BlockView blockView, int maxY) {
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.x, (double)(maxY + 1), this.z);
			boolean bl = blockView.getBlockState(mutable).isAir();
			mutable.move(Direction.DOWN);
			boolean bl2 = blockView.getBlockState(mutable).isAir();

			while (mutable.getY() > blockView.getBottomY()) {
				mutable.move(Direction.DOWN);
				boolean bl3 = blockView.getBlockState(mutable).isAir();
				if (!bl3 && bl2 && bl) {
					return mutable.getY() + 1;
				}

				bl = bl2;
				bl2 = bl3;
			}

			return maxY + 1;
		}

		public boolean isSafe(BlockView world, int maxY) {
			BlockPos blockPos = BlockPos.ofFloored(this.x, (double)(this.getY(world, maxY) - 1), this.z);
			BlockState blockState = world.getBlockState(blockPos);
			return blockPos.getY() < maxY && !blockState.isLiquid() && !blockState.isIn(BlockTags.FIRE);
		}

		public void setPileLocation(Random random, double minX, double minZ, double maxX, double maxZ) {
			this.x = MathHelper.nextDouble(random, minX, maxX);
			this.z = MathHelper.nextDouble(random, minZ, maxZ);
		}
	}
}
