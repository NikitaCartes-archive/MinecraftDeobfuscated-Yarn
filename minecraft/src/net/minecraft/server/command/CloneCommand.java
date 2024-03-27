package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
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
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;

public class CloneCommand {
	private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.clone.overlap"));
	private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(maxCount, count) -> Text.stringifiedTranslatable("commands.clone.toobig", maxCount, count)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.clone.failed"));
	public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = pos -> !pos.getBlockState().isAir();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
		dispatcher.register(
			CommandManager.literal("clone")
				.requires(source -> source.hasPermissionLevel(2))
				.then(createSourceArgs(commandRegistryAccess, context -> context.getSource().getWorld()))
				.then(
					CommandManager.literal("from")
						.then(
							CommandManager.argument("sourceDimension", DimensionArgumentType.dimension())
								.then(createSourceArgs(commandRegistryAccess, context -> DimensionArgumentType.getDimensionArgument(context, "sourceDimension")))
						)
				)
		);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> createSourceArgs(
		CommandRegistryAccess commandRegistryAccess, CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, ServerWorld> worldGetter
	) {
		return CommandManager.argument("begin", BlockPosArgumentType.blockPos())
			.then(
				CommandManager.argument("end", BlockPosArgumentType.blockPos())
					.then(createDestinationArgs(commandRegistryAccess, worldGetter, context -> context.getSource().getWorld()))
					.then(
						CommandManager.literal("to")
							.then(
								CommandManager.argument("targetDimension", DimensionArgumentType.dimension())
									.then(createDestinationArgs(commandRegistryAccess, worldGetter, context -> DimensionArgumentType.getDimensionArgument(context, "targetDimension")))
							)
					)
			);
	}

	private static CloneCommand.DimensionalPos createDimensionalPos(CommandContext<ServerCommandSource> context, ServerWorld world, String name) throws CommandSyntaxException {
		BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(context, world, name);
		return new CloneCommand.DimensionalPos(world, blockPos);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> createDestinationArgs(
		CommandRegistryAccess commandRegistryAccess,
		CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, ServerWorld> sourceWorldGetter,
		CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, ServerWorld> targetWorldGetter
	) {
		CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, CloneCommand.DimensionalPos> argumentGetter = context -> createDimensionalPos(
				context, sourceWorldGetter.apply(context), "begin"
			);
		CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, CloneCommand.DimensionalPos> argumentGetter2 = context -> createDimensionalPos(
				context, sourceWorldGetter.apply(context), "end"
			);
		CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, CloneCommand.DimensionalPos> argumentGetter3 = context -> createDimensionalPos(
				context, targetWorldGetter.apply(context), "destination"
			);
		return CommandManager.argument("destination", BlockPosArgumentType.blockPos())
			.executes(
				context -> execute(
						context.getSource(), argumentGetter.apply(context), argumentGetter2.apply(context), argumentGetter3.apply(context), pos -> true, CloneCommand.Mode.NORMAL
					)
			)
			.then(
				createModeArgs(
					argumentGetter,
					argumentGetter2,
					argumentGetter3,
					context -> cachedBlockPosition -> true,
					CommandManager.literal("replace")
						.executes(
							context -> execute(
									context.getSource(),
									argumentGetter.apply(context),
									argumentGetter2.apply(context),
									argumentGetter3.apply(context),
									pos -> true,
									CloneCommand.Mode.NORMAL
								)
						)
				)
			)
			.then(
				createModeArgs(
					argumentGetter,
					argumentGetter2,
					argumentGetter3,
					context -> IS_AIR_PREDICATE,
					CommandManager.literal("masked")
						.executes(
							context -> execute(
									context.getSource(),
									argumentGetter.apply(context),
									argumentGetter2.apply(context),
									argumentGetter3.apply(context),
									IS_AIR_PREDICATE,
									CloneCommand.Mode.NORMAL
								)
						)
				)
			)
			.then(
				CommandManager.literal("filtered")
					.then(
						createModeArgs(
							argumentGetter,
							argumentGetter2,
							argumentGetter3,
							context -> BlockPredicateArgumentType.getBlockPredicate(context, "filter"),
							CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate(commandRegistryAccess))
								.executes(
									context -> execute(
											context.getSource(),
											argumentGetter.apply(context),
											argumentGetter2.apply(context),
											argumentGetter3.apply(context),
											BlockPredicateArgumentType.getBlockPredicate(context, "filter"),
											CloneCommand.Mode.NORMAL
										)
								)
						)
					)
			);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> createModeArgs(
		CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, CloneCommand.DimensionalPos> beginPosGetter,
		CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, CloneCommand.DimensionalPos> endPosGetter,
		CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, CloneCommand.DimensionalPos> destinationPosGetter,
		CloneCommand.ArgumentGetter<CommandContext<ServerCommandSource>, Predicate<CachedBlockPosition>> filterGetter,
		ArgumentBuilder<ServerCommandSource, ?> builder
	) {
		return builder.then(
				CommandManager.literal("force")
					.executes(
						context -> execute(
								context.getSource(),
								beginPosGetter.apply(context),
								endPosGetter.apply(context),
								destinationPosGetter.apply(context),
								filterGetter.apply(context),
								CloneCommand.Mode.FORCE
							)
					)
			)
			.then(
				CommandManager.literal("move")
					.executes(
						context -> execute(
								context.getSource(),
								beginPosGetter.apply(context),
								endPosGetter.apply(context),
								destinationPosGetter.apply(context),
								filterGetter.apply(context),
								CloneCommand.Mode.MOVE
							)
					)
			)
			.then(
				CommandManager.literal("normal")
					.executes(
						context -> execute(
								context.getSource(),
								beginPosGetter.apply(context),
								endPosGetter.apply(context),
								destinationPosGetter.apply(context),
								filterGetter.apply(context),
								CloneCommand.Mode.NORMAL
							)
					)
			);
	}

	private static int execute(
		ServerCommandSource source,
		CloneCommand.DimensionalPos begin,
		CloneCommand.DimensionalPos end,
		CloneCommand.DimensionalPos destination,
		Predicate<CachedBlockPosition> filter,
		CloneCommand.Mode mode
	) throws CommandSyntaxException {
		BlockPos blockPos = begin.position();
		BlockPos blockPos2 = end.position();
		BlockBox blockBox = BlockBox.create(blockPos, blockPos2);
		BlockPos blockPos3 = destination.position();
		BlockPos blockPos4 = blockPos3.add(blockBox.getDimensions());
		BlockBox blockBox2 = BlockBox.create(blockPos3, blockPos4);
		ServerWorld serverWorld = begin.dimension();
		ServerWorld serverWorld2 = destination.dimension();
		if (!mode.allowsOverlap() && serverWorld == serverWorld2 && blockBox2.intersects(blockBox)) {
			throw OVERLAP_EXCEPTION.create();
		} else {
			int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
			int j = source.getWorld().getGameRules().getInt(GameRules.COMMAND_MODIFICATION_BLOCK_LIMIT);
			if (i > j) {
				throw TOO_BIG_EXCEPTION.create(j, i);
			} else if (serverWorld.isRegionLoaded(blockPos, blockPos2) && serverWorld2.isRegionLoaded(blockPos3, blockPos4)) {
				List<CloneCommand.BlockInfo> list = Lists.<CloneCommand.BlockInfo>newArrayList();
				List<CloneCommand.BlockInfo> list2 = Lists.<CloneCommand.BlockInfo>newArrayList();
				List<CloneCommand.BlockInfo> list3 = Lists.<CloneCommand.BlockInfo>newArrayList();
				Deque<BlockPos> deque = Lists.<BlockPos>newLinkedList();
				BlockPos blockPos5 = new BlockPos(
					blockBox2.getMinX() - blockBox.getMinX(), blockBox2.getMinY() - blockBox.getMinY(), blockBox2.getMinZ() - blockBox.getMinZ()
				);

				for (int k = blockBox.getMinZ(); k <= blockBox.getMaxZ(); k++) {
					for (int l = blockBox.getMinY(); l <= blockBox.getMaxY(); l++) {
						for (int m = blockBox.getMinX(); m <= blockBox.getMaxX(); m++) {
							BlockPos blockPos6 = new BlockPos(m, l, k);
							BlockPos blockPos7 = blockPos6.add(blockPos5);
							CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos6, false);
							BlockState blockState = cachedBlockPosition.getBlockState();
							if (filter.test(cachedBlockPosition)) {
								BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos6);
								if (blockEntity != null) {
									CloneCommand.BlockEntityInfo blockEntityInfo = new CloneCommand.BlockEntityInfo(
										blockEntity.createComponentlessNbt(source.getRegistryManager()), blockEntity.getComponents()
									);
									list2.add(new CloneCommand.BlockInfo(blockPos7, blockState, blockEntityInfo));
									deque.addLast(blockPos6);
								} else if (!blockState.isOpaqueFullCube(serverWorld, blockPos6) && !blockState.isFullCube(serverWorld, blockPos6)) {
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

				if (mode == CloneCommand.Mode.MOVE) {
					for (BlockPos blockPos8 : deque) {
						BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos8);
						Clearable.clear(blockEntity2);
						serverWorld.setBlockState(blockPos8, Blocks.BARRIER.getDefaultState(), Block.NOTIFY_LISTENERS);
					}

					for (BlockPos blockPos8 : deque) {
						serverWorld.setBlockState(blockPos8, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					}
				}

				List<CloneCommand.BlockInfo> list4 = Lists.<CloneCommand.BlockInfo>newArrayList();
				list4.addAll(list);
				list4.addAll(list2);
				list4.addAll(list3);
				List<CloneCommand.BlockInfo> list5 = Lists.reverse(list4);

				for (CloneCommand.BlockInfo blockInfo : list5) {
					BlockEntity blockEntity3 = serverWorld2.getBlockEntity(blockInfo.pos);
					Clearable.clear(blockEntity3);
					serverWorld2.setBlockState(blockInfo.pos, Blocks.BARRIER.getDefaultState(), Block.NOTIFY_LISTENERS);
				}

				int mx = 0;

				for (CloneCommand.BlockInfo blockInfo2 : list4) {
					if (serverWorld2.setBlockState(blockInfo2.pos, blockInfo2.state, Block.NOTIFY_LISTENERS)) {
						mx++;
					}
				}

				for (CloneCommand.BlockInfo blockInfo2x : list2) {
					BlockEntity blockEntity4 = serverWorld2.getBlockEntity(blockInfo2x.pos);
					if (blockInfo2x.blockEntityInfo != null && blockEntity4 != null) {
						blockEntity4.readComponentlessNbt(blockInfo2x.blockEntityInfo.nbt, serverWorld2.getRegistryManager());
						blockEntity4.setComponents(blockInfo2x.blockEntityInfo.components);
						blockEntity4.markDirty();
					}

					serverWorld2.setBlockState(blockInfo2x.pos, blockInfo2x.state, Block.NOTIFY_LISTENERS);
				}

				for (CloneCommand.BlockInfo blockInfo2x : list5) {
					serverWorld2.updateNeighbors(blockInfo2x.pos, blockInfo2x.state.getBlock());
				}

				serverWorld2.getBlockTickScheduler().scheduleTicks(serverWorld.getBlockTickScheduler(), blockBox, blockPos5);
				if (mx == 0) {
					throw FAILED_EXCEPTION.create();
				} else {
					int n = mx;
					source.sendFeedback(() -> Text.translatable("commands.clone.success", n), true);
					return mx;
				}
			} else {
				throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
			}
		}
	}

	@FunctionalInterface
	interface ArgumentGetter<T, R> {
		R apply(T value) throws CommandSyntaxException;
	}

	static record BlockEntityInfo(NbtCompound nbt, ComponentMap components) {
	}

	static record BlockInfo(BlockPos pos, BlockState state, @Nullable CloneCommand.BlockEntityInfo blockEntityInfo) {
	}

	static record DimensionalPos(ServerWorld dimension, BlockPos position) {
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
