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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;

public class ForceLoadCommand {
	private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("commands.forceload.toobig", object, object2)
	);
	private static final Dynamic2CommandExceptionType QUERY_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("commands.forceload.query.failure", object, object2)
	);
	private static final SimpleCommandExceptionType ADDED_FAILURE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.forceload.added.failure")
	);
	private static final SimpleCommandExceptionType REMOVED_FAILURE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.forceload.removed.failure")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("forceload")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("add")
						.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
						.then(
							ServerCommandManager.argument("from", ColumnPosArgumentType.create())
								.executes(
									commandContext -> setForceLoaded(
											commandContext.getSource(),
											ColumnPosArgumentType.parseSimple(commandContext, "from"),
											ColumnPosArgumentType.parseSimple(commandContext, "from"),
											true
										)
								)
								.then(
									ServerCommandManager.argument("to", ColumnPosArgumentType.create())
										.executes(
											commandContext -> setForceLoaded(
													commandContext.getSource(),
													ColumnPosArgumentType.parseSimple(commandContext, "from"),
													ColumnPosArgumentType.parseSimple(commandContext, "to"),
													true
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("remove")
						.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
						.then(
							ServerCommandManager.argument("from", ColumnPosArgumentType.create())
								.executes(
									commandContext -> setForceLoaded(
											commandContext.getSource(),
											ColumnPosArgumentType.parseSimple(commandContext, "from"),
											ColumnPosArgumentType.parseSimple(commandContext, "from"),
											false
										)
								)
								.then(
									ServerCommandManager.argument("to", ColumnPosArgumentType.create())
										.executes(
											commandContext -> setForceLoaded(
													commandContext.getSource(),
													ColumnPosArgumentType.parseSimple(commandContext, "from"),
													ColumnPosArgumentType.parseSimple(commandContext, "to"),
													false
												)
										)
								)
						)
						.then(ServerCommandManager.literal("all").executes(commandContext -> clearAllForceLoaded(commandContext.getSource())))
				)
				.then(
					ServerCommandManager.literal("query")
						.executes(commandContext -> method_13373(commandContext.getSource()))
						.then(
							ServerCommandManager.argument("pos", ColumnPosArgumentType.create())
								.executes(commandContext -> method_13374(commandContext.getSource(), ColumnPosArgumentType.parseSimple(commandContext, "pos")))
						)
				)
		);
	}

	private static int method_13374(ServerCommandSource serverCommandSource, ColumnPosArgumentType.SimpleColumnPos simpleColumnPos) throws CommandSyntaxException {
		ChunkPos chunkPos = new ChunkPos(simpleColumnPos.x >> 4, simpleColumnPos.z >> 4);
		DimensionType dimensionType = serverCommandSource.getWorld().getDimension().getType();
		boolean bl = serverCommandSource.getMinecraftServer().getWorld(dimensionType).getForcedChunks().contains(chunkPos.toLong());
		if (bl) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.forceload.query.success", chunkPos, dimensionType), false);
			return 1;
		} else {
			throw QUERY_FAILURE_EXCEPTION.create(chunkPos, dimensionType);
		}
	}

	private static int method_13373(ServerCommandSource serverCommandSource) {
		DimensionType dimensionType = serverCommandSource.getWorld().getDimension().getType();
		LongSet longSet = serverCommandSource.getMinecraftServer().getWorld(dimensionType).getForcedChunks();
		int i = longSet.size();
		if (i > 0) {
			String string = Joiner.on(", ").join(longSet.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
			if (i == 1) {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.forceload.list.single", dimensionType, string), false);
			} else {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.forceload.list.multiple", i, dimensionType, string), false);
			}
		} else {
			serverCommandSource.sendError(new TranslatableTextComponent("commands.forceload.added.none", dimensionType));
		}

		return i;
	}

	private static int clearAllForceLoaded(ServerCommandSource serverCommandSource) {
		DimensionType dimensionType = serverCommandSource.getWorld().getDimension().getType();
		ServerWorld serverWorld = serverCommandSource.getMinecraftServer().getWorld(dimensionType);
		LongSet longSet = serverWorld.getForcedChunks();
		longSet.forEach(l -> serverWorld.setChunkForced(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), false));
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.forceload.removed.all", dimensionType), true);
		return 0;
	}

	private static int setForceLoaded(
		ServerCommandSource serverCommandSource,
		ColumnPosArgumentType.SimpleColumnPos simpleColumnPos,
		ColumnPosArgumentType.SimpleColumnPos simpleColumnPos2,
		boolean bl
	) throws CommandSyntaxException {
		int i = Math.min(simpleColumnPos.x, simpleColumnPos2.x);
		int j = Math.min(simpleColumnPos.z, simpleColumnPos2.z);
		int k = Math.max(simpleColumnPos.x, simpleColumnPos2.x);
		int l = Math.max(simpleColumnPos.z, simpleColumnPos2.z);
		if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
			int m = i >> 4;
			int n = j >> 4;
			int o = k >> 4;
			int p = l >> 4;
			long q = ((long)(o - m) + 1L) * ((long)(p - n) + 1L);
			if (q > 256L) {
				throw TOOBIG_EXCEPTION.create(256, q);
			} else {
				DimensionType dimensionType = serverCommandSource.getWorld().getDimension().getType();
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
						serverCommandSource.sendFeedback(
							new TranslatableTextComponent("commands.forceload." + (bl ? "added" : "removed") + ".single", chunkPos, dimensionType), true
						);
					} else {
						ChunkPos chunkPos2 = new ChunkPos(m, n);
						ChunkPos chunkPos3 = new ChunkPos(o, p);
						serverCommandSource.sendFeedback(
							new TranslatableTextComponent("commands.forceload." + (bl ? "added" : "removed") + ".multiple", r, dimensionType, chunkPos2, chunkPos3), true
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
