package net.minecraft.server.command;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.ColumnPosArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.dimension.DimensionType;

public class ForceLoadCommand {
	private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.forceload.toobig", object, object2)
	);
	private static final Dynamic2CommandExceptionType QUERY_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.forceload.query.failure", object, object2)
	);
	private static final SimpleCommandExceptionType ADDED_FAILURE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.forceload.added.failure")
	);
	private static final SimpleCommandExceptionType REMOVED_FAILURE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.forceload.removed.failure")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("forceload")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("add")
						.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
						.then(
							CommandManager.argument("from", ColumnPosArgumentType.create())
								.executes(
									commandContext -> executeChange(
											commandContext.getSource(),
											ColumnPosArgumentType.getColumnPos(commandContext, "from"),
											ColumnPosArgumentType.getColumnPos(commandContext, "from"),
											true
										)
								)
								.then(
									CommandManager.argument("to", ColumnPosArgumentType.create())
										.executes(
											commandContext -> executeChange(
													commandContext.getSource(),
													ColumnPosArgumentType.getColumnPos(commandContext, "from"),
													ColumnPosArgumentType.getColumnPos(commandContext, "to"),
													true
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("remove")
						.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
						.then(
							CommandManager.argument("from", ColumnPosArgumentType.create())
								.executes(
									commandContext -> executeChange(
											commandContext.getSource(),
											ColumnPosArgumentType.getColumnPos(commandContext, "from"),
											ColumnPosArgumentType.getColumnPos(commandContext, "from"),
											false
										)
								)
								.then(
									CommandManager.argument("to", ColumnPosArgumentType.create())
										.executes(
											commandContext -> executeChange(
													commandContext.getSource(),
													ColumnPosArgumentType.getColumnPos(commandContext, "from"),
													ColumnPosArgumentType.getColumnPos(commandContext, "to"),
													false
												)
										)
								)
						)
						.then(CommandManager.literal("all").executes(commandContext -> executeRemoveAll(commandContext.getSource())))
				)
				.then(
					CommandManager.literal("query")
						.executes(commandContext -> executeQuery(commandContext.getSource()))
						.then(
							CommandManager.argument("pos", ColumnPosArgumentType.create())
								.executes(commandContext -> executeQuery(commandContext.getSource(), ColumnPosArgumentType.getColumnPos(commandContext, "pos")))
						)
				)
		);
	}

	private static int executeQuery(ServerCommandSource serverCommandSource, ColumnPos columnPos) throws CommandSyntaxException {
		ChunkPos chunkPos = new ChunkPos(columnPos.x >> 4, columnPos.z >> 4);
		DimensionType dimensionType = serverCommandSource.getWorld().method_8597().method_12460();
		boolean bl = serverCommandSource.getMinecraftServer().getWorld(dimensionType).getForcedChunks().contains(chunkPos.toLong());
		if (bl) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.forceload.query.success", chunkPos, dimensionType), false);
			return 1;
		} else {
			throw QUERY_FAILURE_EXCEPTION.create(chunkPos, dimensionType);
		}
	}

	private static int executeQuery(ServerCommandSource serverCommandSource) {
		DimensionType dimensionType = serverCommandSource.getWorld().method_8597().method_12460();
		LongSet longSet = serverCommandSource.getMinecraftServer().getWorld(dimensionType).getForcedChunks();
		int i = longSet.size();
		if (i > 0) {
			String string = Joiner.on(", ").join(longSet.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
			if (i == 1) {
				serverCommandSource.sendFeedback(new TranslatableText("commands.forceload.list.single", dimensionType, string), false);
			} else {
				serverCommandSource.sendFeedback(new TranslatableText("commands.forceload.list.multiple", i, dimensionType, string), false);
			}
		} else {
			serverCommandSource.sendError(new TranslatableText("commands.forceload.added.none", dimensionType));
		}

		return i;
	}

	private static int executeRemoveAll(ServerCommandSource serverCommandSource) {
		DimensionType dimensionType = serverCommandSource.getWorld().method_8597().method_12460();
		ServerWorld serverWorld = serverCommandSource.getMinecraftServer().getWorld(dimensionType);
		LongSet longSet = serverWorld.getForcedChunks();
		longSet.forEach(l -> serverWorld.setChunkForced(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), false));
		serverCommandSource.sendFeedback(new TranslatableText("commands.forceload.removed.all", dimensionType), true);
		return 0;
	}

	private static int executeChange(ServerCommandSource serverCommandSource, ColumnPos columnPos, ColumnPos columnPos2, boolean bl) throws CommandSyntaxException {
		int i = Math.min(columnPos.x, columnPos2.x);
		int j = Math.min(columnPos.z, columnPos2.z);
		int k = Math.max(columnPos.x, columnPos2.x);
		int l = Math.max(columnPos.z, columnPos2.z);
		if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
			int m = i >> 4;
			int n = j >> 4;
			int o = k >> 4;
			int p = l >> 4;
			long q = ((long)(o - m) + 1L) * ((long)(p - n) + 1L);
			if (q > 256L) {
				throw TOOBIG_EXCEPTION.create(256, q);
			} else {
				DimensionType dimensionType = serverCommandSource.getWorld().method_8597().method_12460();
				ServerWorld serverWorld = serverCommandSource.getMinecraftServer().getWorld(dimensionType);
				ChunkPos chunkPos = null;
				int r = 0;

				for (int s = m; s <= o; s++) {
					for (int t = n; t <= p; t++) {
						boolean bl2 = serverWorld.setChunkForced(s, t, bl);
						if (bl2) {
							r++;
							if (chunkPos == null) {
								chunkPos = new ChunkPos(s, t);
							}
						}
					}
				}

				if (r == 0) {
					throw (bl ? ADDED_FAILURE_EXCEPTION : REMOVED_FAILURE_EXCEPTION).create();
				} else {
					if (r == 1) {
						serverCommandSource.sendFeedback(new TranslatableText("commands.forceload." + (bl ? "added" : "removed") + ".single", chunkPos, dimensionType), true);
					} else {
						ChunkPos chunkPos2 = new ChunkPos(m, n);
						ChunkPos chunkPos3 = new ChunkPos(o, p);
						serverCommandSource.sendFeedback(
							new TranslatableText("commands.forceload." + (bl ? "added" : "removed") + ".multiple", r, dimensionType, chunkPos2, chunkPos3), true
						);
					}

					return r;
				}
			}
		} else {
			throw BlockPosArgumentType.OUT_OF_WORLD_EXCEPTION.create();
		}
	}
}
