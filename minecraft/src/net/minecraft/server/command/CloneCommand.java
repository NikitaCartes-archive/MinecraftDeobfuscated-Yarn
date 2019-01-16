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
import net.minecraft.class_3829;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;

public class CloneCommand {
	private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.clone.overlap"));
	private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("commands.clone.toobig", object, object2)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.clone.failed"));
	public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = cachedBlockPosition -> !cachedBlockPosition.getBlockState().isAir();

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("clone")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("begin", BlockPosArgumentType.create())
						.then(
							ServerCommandManager.argument("end", BlockPosArgumentType.create())
								.then(
									ServerCommandManager.argument("destination", BlockPosArgumentType.create())
										.executes(
											commandContext -> method_13090(
													commandContext.getSource(),
													BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
													BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
													BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
													cachedBlockPosition -> true,
													CloneCommand.class_3025.field_13499
												)
										)
										.then(
											ServerCommandManager.literal("replace")
												.executes(
													commandContext -> method_13090(
															commandContext.getSource(),
															BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
															BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
															BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
															cachedBlockPosition -> true,
															CloneCommand.class_3025.field_13499
														)
												)
												.then(
													ServerCommandManager.literal("force")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																	cachedBlockPosition -> true,
																	CloneCommand.class_3025.field_13497
																)
														)
												)
												.then(
													ServerCommandManager.literal("move")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																	cachedBlockPosition -> true,
																	CloneCommand.class_3025.field_13500
																)
														)
												)
												.then(
													ServerCommandManager.literal("normal")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																	cachedBlockPosition -> true,
																	CloneCommand.class_3025.field_13499
																)
														)
												)
										)
										.then(
											ServerCommandManager.literal("masked")
												.executes(
													commandContext -> method_13090(
															commandContext.getSource(),
															BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
															BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
															BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
															IS_AIR_PREDICATE,
															CloneCommand.class_3025.field_13499
														)
												)
												.then(
													ServerCommandManager.literal("force")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																	IS_AIR_PREDICATE,
																	CloneCommand.class_3025.field_13497
																)
														)
												)
												.then(
													ServerCommandManager.literal("move")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																	IS_AIR_PREDICATE,
																	CloneCommand.class_3025.field_13500
																)
														)
												)
												.then(
													ServerCommandManager.literal("normal")
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																	IS_AIR_PREDICATE,
																	CloneCommand.class_3025.field_13499
																)
														)
												)
										)
										.then(
											ServerCommandManager.literal("filtered")
												.then(
													ServerCommandManager.argument("filter", BlockPredicateArgumentType.create())
														.executes(
															commandContext -> method_13090(
																	commandContext.getSource(),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																	BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																	BlockPredicateArgumentType.getPredicateArgument(commandContext, "filter"),
																	CloneCommand.class_3025.field_13499
																)
														)
														.then(
															ServerCommandManager.literal("force")
																.executes(
																	commandContext -> method_13090(
																			commandContext.getSource(),
																			BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																			BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																			BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																			BlockPredicateArgumentType.getPredicateArgument(commandContext, "filter"),
																			CloneCommand.class_3025.field_13497
																		)
																)
														)
														.then(
															ServerCommandManager.literal("move")
																.executes(
																	commandContext -> method_13090(
																			commandContext.getSource(),
																			BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																			BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																			BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																			BlockPredicateArgumentType.getPredicateArgument(commandContext, "filter"),
																			CloneCommand.class_3025.field_13500
																		)
																)
														)
														.then(
															ServerCommandManager.literal("normal")
																.executes(
																	commandContext -> method_13090(
																			commandContext.getSource(),
																			BlockPosArgumentType.getValidPosArgument(commandContext, "begin"),
																			BlockPosArgumentType.getValidPosArgument(commandContext, "end"),
																			BlockPosArgumentType.getValidPosArgument(commandContext, "destination"),
																			BlockPredicateArgumentType.getPredicateArgument(commandContext, "filter"),
																			CloneCommand.class_3025.field_13499
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

	private static int method_13090(
		ServerCommandSource serverCommandSource,
		BlockPos blockPos,
		BlockPos blockPos2,
		BlockPos blockPos3,
		Predicate<CachedBlockPosition> predicate,
		CloneCommand.class_3025 arg
	) throws CommandSyntaxException {
		MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(blockPos, blockPos2);
		BlockPos blockPos4 = blockPos3.add(mutableIntBoundingBox.getSize());
		MutableIntBoundingBox mutableIntBoundingBox2 = new MutableIntBoundingBox(blockPos3, blockPos4);
		if (!arg.method_13109() && mutableIntBoundingBox2.intersects(mutableIntBoundingBox)) {
			throw OVERLAP_EXCEPTION.create();
		} else {
			int i = mutableIntBoundingBox.getBlockCountX() * mutableIntBoundingBox.getBlockCountY() * mutableIntBoundingBox.getBlockCountZ();
			if (i > 32768) {
				throw TOOBIG_EXCEPTION.create(32768, i);
			} else {
				ServerWorld serverWorld = serverCommandSource.getWorld();
				if (serverWorld.isAreaLoaded(blockPos, blockPos2) && serverWorld.isAreaLoaded(blockPos3, blockPos4)) {
					List<CloneCommand.class_3024> list = Lists.<CloneCommand.class_3024>newArrayList();
					List<CloneCommand.class_3024> list2 = Lists.<CloneCommand.class_3024>newArrayList();
					List<CloneCommand.class_3024> list3 = Lists.<CloneCommand.class_3024>newArrayList();
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
										list2.add(new CloneCommand.class_3024(blockPos7, blockState, compoundTag));
										deque.addLast(blockPos6);
									} else if (!blockState.isFullOpaque(serverWorld, blockPos6) && !blockState.method_11604(serverWorld, blockPos6)) {
										list3.add(new CloneCommand.class_3024(blockPos7, blockState, null));
										deque.addFirst(blockPos6);
									} else {
										list.add(new CloneCommand.class_3024(blockPos7, blockState, null));
										deque.addLast(blockPos6);
									}
								}
							}
						}
					}

					if (arg == CloneCommand.class_3025.field_13500) {
						for (BlockPos blockPos8 : deque) {
							BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos8);
							class_3829.method_16825(blockEntity2);
							serverWorld.setBlockState(blockPos8, Blocks.field_10499.getDefaultState(), 2);
						}

						for (BlockPos blockPos8 : deque) {
							serverWorld.setBlockState(blockPos8, Blocks.field_10124.getDefaultState(), 3);
						}
					}

					List<CloneCommand.class_3024> list4 = Lists.<CloneCommand.class_3024>newArrayList();
					list4.addAll(list);
					list4.addAll(list2);
					list4.addAll(list3);
					List<CloneCommand.class_3024> list5 = Lists.reverse(list4);

					for (CloneCommand.class_3024 lv : list5) {
						BlockEntity blockEntity3 = serverWorld.getBlockEntity(lv.field_13496);
						class_3829.method_16825(blockEntity3);
						serverWorld.setBlockState(lv.field_13496, Blocks.field_10499.getDefaultState(), 2);
					}

					int lx = 0;

					for (CloneCommand.class_3024 lv2 : list4) {
						if (serverWorld.setBlockState(lv2.field_13496, lv2.field_13495, 2)) {
							lx++;
						}
					}

					for (CloneCommand.class_3024 lv2x : list2) {
						BlockEntity blockEntity4 = serverWorld.getBlockEntity(lv2x.field_13496);
						if (lv2x.field_13494 != null && blockEntity4 != null) {
							lv2x.field_13494.putInt("x", lv2x.field_13496.getX());
							lv2x.field_13494.putInt("y", lv2x.field_13496.getY());
							lv2x.field_13494.putInt("z", lv2x.field_13496.getZ());
							blockEntity4.fromTag(lv2x.field_13494);
							blockEntity4.markDirty();
						}

						serverWorld.setBlockState(lv2x.field_13496, lv2x.field_13495, 2);
					}

					for (CloneCommand.class_3024 lv2x : list5) {
						serverWorld.updateNeighbors(lv2x.field_13496, lv2x.field_13495.getBlock());
					}

					serverWorld.getBlockTickScheduler().method_8666(mutableIntBoundingBox, blockPos5);
					if (lx == 0) {
						throw FAILED_EXCEPTION.create();
					} else {
						serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.clone.success", lx), true);
						return lx;
					}
				} else {
					throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
				}
			}
		}
	}

	static class class_3024 {
		public final BlockPos field_13496;
		public final BlockState field_13495;
		@Nullable
		public final CompoundTag field_13494;

		public class_3024(BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
			this.field_13496 = blockPos;
			this.field_13495 = blockState;
			this.field_13494 = compoundTag;
		}
	}

	static enum class_3025 {
		field_13497(true),
		field_13500(true),
		field_13499(false);

		private final boolean field_13498;

		private class_3025(boolean bl) {
			this.field_13498 = bl;
		}

		public boolean method_13109() {
			return this.field_13498;
		}
	}
}
