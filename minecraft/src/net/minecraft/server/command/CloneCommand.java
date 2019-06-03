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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;

public class CloneCommand {
	private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.overlap"));
	private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.clone.toobig", object, object2)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.failed"));
	public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = cachedBlockPosition -> !cachedBlockPosition.getBlockState().isAir();

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("clone")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("begin", BlockPosArgumentType.create())
						.then(
							CommandManager.argument("end", BlockPosArgumentType.create())
								.then(
									CommandManager.argument("destination", BlockPosArgumentType.create())
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
													cachedBlockPosition -> true,
													CloneCommand.Mode.field_13499
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
															CloneCommand.Mode.field_13499
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
																	CloneCommand.Mode.field_13497
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
																	CloneCommand.Mode.field_13500
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
																	CloneCommand.Mode.field_13499
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
															CloneCommand.Mode.field_13499
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
																	CloneCommand.Mode.field_13497
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
																	CloneCommand.Mode.field_13500
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
																	CloneCommand.Mode.field_13499
																)
														)
												)
										)
										.then(
											CommandManager.literal("filtered")
												.then(
													CommandManager.argument("filter", BlockPredicateArgumentType.create())
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																	BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"),
																	CloneCommand.Mode.field_13499
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
																			CloneCommand.Mode.field_13497
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
																			CloneCommand.Mode.field_13500
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
																			CloneCommand.Mode.field_13499
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
		ServerCommandSource serverCommandSource,
		BlockPos blockPos,
		BlockPos blockPos2,
		BlockPos blockPos3,
		Predicate<CachedBlockPosition> predicate,
		CloneCommand.Mode mode
	) throws CommandSyntaxException {
		MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(blockPos, blockPos2);
		BlockPos blockPos4 = blockPos3.add(mutableIntBoundingBox.getSize());
		MutableIntBoundingBox mutableIntBoundingBox2 = new MutableIntBoundingBox(blockPos3, blockPos4);
		if (!mode.allowsOverlap() && mutableIntBoundingBox2.intersects(mutableIntBoundingBox)) {
			throw OVERLAP_EXCEPTION.create();
		} else {
			int i = mutableIntBoundingBox.getBlockCountX() * mutableIntBoundingBox.getBlockCountY() * mutableIntBoundingBox.getBlockCountZ();
			if (i > 32768) {
				throw TOOBIG_EXCEPTION.create(32768, i);
			} else {
				ServerWorld serverWorld = serverCommandSource.getWorld();
				if (serverWorld.isAreaLoaded(blockPos, blockPos2) && serverWorld.isAreaLoaded(blockPos3, blockPos4)) {
					List<CloneCommand.BlockInfo> list = Lists.<CloneCommand.BlockInfo>newArrayList();
					List<CloneCommand.BlockInfo> list2 = Lists.<CloneCommand.BlockInfo>newArrayList();
					List<CloneCommand.BlockInfo> list3 = Lists.<CloneCommand.BlockInfo>newArrayList();
					Deque<BlockPos> deque = Lists.<BlockPos>newLinkedList();
					BlockPos blockPos5 = new BlockPos(
						mutableIntBoundingBox2.minX - mutableIntBoundingBox.minX,
						mutableIntBoundingBox2.minY - mutableIntBoundingBox.minY,
						mutableIntBoundingBox2.minZ - mutableIntBoundingBox.minZ
					);

					for (int j = mutableIntBoundingBox.minZ; j <= mutableIntBoundingBox.maxZ; j++) {
						for (int k = mutableIntBoundingBox.minY; k <= mutableIntBoundingBox.maxY; k++) {
							for (int l = mutableIntBoundingBox.minX; l <= mutableIntBoundingBox.maxX; l++) {
								BlockPos blockPos6 = new BlockPos(l, k, j);
								BlockPos blockPos7 = blockPos6.add(blockPos5);
								CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos6, false);
								BlockState blockState = cachedBlockPosition.getBlockState();
								if (predicate.test(cachedBlockPosition)) {
									BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos6);
									if (blockEntity != null) {
										CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
										list2.add(new CloneCommand.BlockInfo(blockPos7, blockState, compoundTag));
										deque.addLast(blockPos6);
									} else if (!blockState.isFullOpaque(serverWorld, blockPos6) && !Block.isShapeFullCube(blockState.getCollisionShape(serverWorld, blockPos6))) {
										list3.add(new CloneCommand.BlockInfo(blockPos7, blockState, null));
										deque.addFirst(blockPos6);
									} else {
										list.add(new CloneCommand.BlockInfo(blockPos7, blockState, null));
										deque.addLast(blockPos6);
									}
								}
							}
						}
					}

					if (mode == CloneCommand.Mode.field_13500) {
						for (BlockPos blockPos8 : deque) {
							BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos8);
							Clearable.clear(blockEntity2);
							serverWorld.setBlockState(blockPos8, Blocks.field_10499.getDefaultState(), 2);
						}

						for (BlockPos blockPos8 : deque) {
							serverWorld.setBlockState(blockPos8, Blocks.field_10124.getDefaultState(), 3);
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
						serverWorld.setBlockState(blockInfo.pos, Blocks.field_10499.getDefaultState(), 2);
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
							blockEntity4.fromTag(blockInfo2x.blockEntityTag);
							blockEntity4.markDirty();
						}

						serverWorld.setBlockState(blockInfo2x.pos, blockInfo2x.state, 2);
					}

					for (CloneCommand.BlockInfo blockInfo2x : list5) {
						serverWorld.updateNeighbors(blockInfo2x.pos, blockInfo2x.state.getBlock());
					}

					serverWorld.method_14196().copyScheduledTicks(mutableIntBoundingBox, blockPos5);
					if (lx == 0) {
						throw FAILED_EXCEPTION.create();
					} else {
						serverCommandSource.method_9226(new TranslatableText("commands.clone.success", lx), true);
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

		public BlockInfo(BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
			this.pos = blockPos;
			this.state = blockState;
			this.blockEntityTag = compoundTag;
		}
	}

	static enum Mode {
		field_13497(true),
		field_13500(true),
		field_13499(false);

		private final boolean allowsOverlap;

		private Mode(boolean bl) {
			this.allowsOverlap = bl;
		}

		public boolean allowsOverlap() {
			return this.allowsOverlap;
		}
	}
}
