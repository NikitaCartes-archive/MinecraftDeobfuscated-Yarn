package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class CloneCommand {
	private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.overlap"));
	private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.clone.toobig", object, object2)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.failed"));
	public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = cachedBlockPosition -> !cachedBlockPosition.getBlockState().isAir();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("clone")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("begin", BlockPosArgumentType.blockPos())
						.then(
							CommandManager.argument("end", BlockPosArgumentType.blockPos())
								.then(
									CommandManager.argument("destination", BlockPosArgumentType.blockPos())
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
													cachedBlockPosition -> true,
													CloneCommand.Mode.NORMAL
												)
										)
										.then(
											CommandManager.literal("replace")
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
															cachedBlockPosition -> true,
															CloneCommand.Mode.NORMAL
														)
												)
												.then(
													CommandManager.literal("force")
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																	cachedBlockPosition -> true,
																	CloneCommand.Mode.FORCE
																)
														)
												)
												.then(
													CommandManager.literal("move")
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																	cachedBlockPosition -> true,
																	CloneCommand.Mode.MOVE
																)
														)
												)
												.then(
													CommandManager.literal("normal")
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																	cachedBlockPosition -> true,
																	CloneCommand.Mode.NORMAL
																)
														)
												)
										)
										.then(
											CommandManager.literal("masked")
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
															IS_AIR_PREDICATE,
															CloneCommand.Mode.NORMAL
														)
												)
												.then(
													CommandManager.literal("force")
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																	IS_AIR_PREDICATE,
																	CloneCommand.Mode.FORCE
																)
														)
												)
												.then(
													CommandManager.literal("move")
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																	IS_AIR_PREDICATE,
																	CloneCommand.Mode.MOVE
																)
														)
												)
												.then(
													CommandManager.literal("normal")
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																	IS_AIR_PREDICATE,
																	CloneCommand.Mode.NORMAL
																)
														)
												)
										)
										.then(
											CommandManager.literal("filtered")
												.then(
													CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate())
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																	BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"),
																	CloneCommand.Mode.NORMAL
																)
														)
														.then(
															CommandManager.literal("force")
																.executes(
																	commandContext -> execute(
																			commandContext.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																			BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"),
																			CloneCommand.Mode.FORCE
																		)
																)
														)
														.then(
															CommandManager.literal("move")
																.executes(
																	commandContext -> execute(
																			commandContext.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																			BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"),
																			CloneCommand.Mode.MOVE
																		)
																)
														)
														.then(
															CommandManager.literal("normal")
																.executes(
																	commandContext -> execute(
																			commandContext.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																			BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"),
																			CloneCommand.Mode.NORMAL
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
		ServerCommandSource source, BlockPos begin, BlockPos end, BlockPos destination, Predicate<CachedBlockPosition> filter, CloneCommand.Mode mode
	) throws CommandSyntaxException {
		BlockBox blockBox = BlockBox.create(begin, end);
		BlockPos blockPos = destination.add(blockBox.getDimensions());
		BlockBox blockBox2 = BlockBox.create(destination, blockPos);
		if (!mode.allowsOverlap() && blockBox2.intersects(blockBox)) {
			throw OVERLAP_EXCEPTION.create();
		} else {
			int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
			if (i > 32768) {
				throw TOO_BIG_EXCEPTION.create(32768, i);
			} else {
				ServerWorld serverWorld = source.getWorld();
				if (serverWorld.isRegionLoaded(begin, end) && serverWorld.isRegionLoaded(destination, blockPos)) {
					List<CloneCommand.BlockInfo> list = Lists.<CloneCommand.BlockInfo>newArrayList();
					List<CloneCommand.BlockInfo> list2 = Lists.<CloneCommand.BlockInfo>newArrayList();
					List<CloneCommand.BlockInfo> list3 = Lists.<CloneCommand.BlockInfo>newArrayList();
					Deque<BlockPos> deque = Lists.<BlockPos>newLinkedList();
					BlockPos blockPos2 = new BlockPos(blockBox2.minX - blockBox.minX, blockBox2.minY - blockBox.minY, blockBox2.minZ - blockBox.minZ);

					for (int j = blockBox.minZ; j <= blockBox.maxZ; j++) {
						for (int k = blockBox.minY; k <= blockBox.maxY; k++) {
							for (int l = blockBox.minX; l <= blockBox.maxX; l++) {
								BlockPos blockPos3 = new BlockPos(l, k, j);
								BlockPos blockPos4 = blockPos3.add(blockPos2);
								CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos3, false);
								BlockState blockState = cachedBlockPosition.getBlockState();
								if (filter.test(cachedBlockPosition)) {
									BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos3);
									if (blockEntity != null) {
										CompoundTag compoundTag = blockEntity.writeNbt(new CompoundTag());
										list2.add(new CloneCommand.BlockInfo(blockPos4, blockState, compoundTag));
										deque.addLast(blockPos3);
									} else if (!blockState.isOpaqueFullCube(serverWorld, blockPos3) && !blockState.isFullCube(serverWorld, blockPos3)) {
										list3.add(new CloneCommand.BlockInfo(blockPos4, blockState, null));
										deque.addFirst(blockPos3);
									} else {
										list.add(new CloneCommand.BlockInfo(blockPos4, blockState, null));
										deque.addLast(blockPos3);
									}
								}
							}
						}
					}

					if (mode == CloneCommand.Mode.MOVE) {
						for (BlockPos blockPos5 : deque) {
							BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos5);
							Clearable.clear(blockEntity2);
							serverWorld.setBlockState(blockPos5, Blocks.BARRIER.getDefaultState(), 2);
						}

						for (BlockPos blockPos5 : deque) {
							serverWorld.setBlockState(blockPos5, Blocks.AIR.getDefaultState(), 3);
						}
					}

					List<CloneCommand.BlockInfo> list4 = Lists.<CloneCommand.BlockInfo>newArrayList();
					list4.addAll(list);
					list4.addAll(list2);
					list4.addAll(list3);
					List<CloneCommand.BlockInfo> list5 = Lists.reverse(list4);

					for (CloneCommand.BlockInfo blockInfo : list5) {
						BlockEntity blockEntity3 = serverWorld.getBlockEntity(blockInfo.pos);
						Clearable.clear(blockEntity3);
						serverWorld.setBlockState(blockInfo.pos, Blocks.BARRIER.getDefaultState(), 2);
					}

					int lx = 0;

					for (CloneCommand.BlockInfo blockInfo2 : list4) {
						if (serverWorld.setBlockState(blockInfo2.pos, blockInfo2.state, 2)) {
							lx++;
						}
					}

					for (CloneCommand.BlockInfo blockInfo2x : list2) {
						BlockEntity blockEntity4 = serverWorld.getBlockEntity(blockInfo2x.pos);
						if (blockInfo2x.blockEntityTag != null && blockEntity4 != null) {
							blockInfo2x.blockEntityTag.putInt("x", blockInfo2x.pos.getX());
							blockInfo2x.blockEntityTag.putInt("y", blockInfo2x.pos.getY());
							blockInfo2x.blockEntityTag.putInt("z", blockInfo2x.pos.getZ());
							blockEntity4.readNbt(blockInfo2x.blockEntityTag);
							blockEntity4.markDirty();
						}

						serverWorld.setBlockState(blockInfo2x.pos, blockInfo2x.state, 2);
					}

					for (CloneCommand.BlockInfo blockInfo2x : list5) {
						serverWorld.updateNeighbors(blockInfo2x.pos, blockInfo2x.state.getBlock());
					}

					serverWorld.getBlockTickScheduler().copyScheduledTicks(blockBox, blockPos2);
					if (lx == 0) {
						throw FAILED_EXCEPTION.create();
					} else {
						source.sendFeedback(new TranslatableText("commands.clone.success", lx), true);
						return lx;
					}
				} else {
					throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
				}
			}
		}
	}

	static class BlockInfo {
		public final BlockPos pos;
		public final BlockState state;
		@Nullable
		public final CompoundTag blockEntityTag;

		public BlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag blockEntityTag) {
			this.pos = pos;
			this.state = state;
			this.blockEntityTag = blockEntityTag;
		}
	}

	static enum Mode {
		FORCE(true),
		MOVE(true),
		NORMAL(false);

		private final boolean allowsOverlap;

		private Mode(boolean allowsOverlap) {
			this.allowsOverlap = allowsOverlap;
		}

		public boolean allowsOverlap() {
			return this.allowsOverlap;
		}
	}
}
