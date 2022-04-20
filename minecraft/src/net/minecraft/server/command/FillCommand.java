package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class FillCommand {
	private static final int MAX_BLOCKS = 32768;
	private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(maxCount, count) -> Text.method_43469("commands.fill.toobig", maxCount, count)
	);
	static final BlockStateArgument AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.AIR.getDefaultState(), Collections.emptySet(), null);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.method_43471("commands.fill.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
		dispatcher.register(
			CommandManager.literal("fill")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("from", BlockPosArgumentType.blockPos())
						.then(
							CommandManager.argument("to", BlockPosArgumentType.blockPos())
								.then(
									CommandManager.argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
										.executes(
											context -> execute(
													context.getSource(),
													BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")),
													BlockStateArgumentType.getBlockState(context, "block"),
													FillCommand.Mode.REPLACE,
													null
												)
										)
										.then(
											CommandManager.literal("replace")
												.executes(
													context -> execute(
															context.getSource(),
															BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")),
															BlockStateArgumentType.getBlockState(context, "block"),
															FillCommand.Mode.REPLACE,
															null
														)
												)
												.then(
													CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate(commandRegistryAccess))
														.executes(
															context -> execute(
																	context.getSource(),
																	BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")),
																	BlockStateArgumentType.getBlockState(context, "block"),
																	FillCommand.Mode.REPLACE,
																	BlockPredicateArgumentType.getBlockPredicate(context, "filter")
																)
														)
												)
										)
										.then(
											CommandManager.literal("keep")
												.executes(
													context -> execute(
															context.getSource(),
															BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")),
															BlockStateArgumentType.getBlockState(context, "block"),
															FillCommand.Mode.REPLACE,
															pos -> pos.getWorld().isAir(pos.getBlockPos())
														)
												)
										)
										.then(
											CommandManager.literal("outline")
												.executes(
													context -> execute(
															context.getSource(),
															BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")),
															BlockStateArgumentType.getBlockState(context, "block"),
															FillCommand.Mode.OUTLINE,
															null
														)
												)
										)
										.then(
											CommandManager.literal("hollow")
												.executes(
													context -> execute(
															context.getSource(),
															BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")),
															BlockStateArgumentType.getBlockState(context, "block"),
															FillCommand.Mode.HOLLOW,
															null
														)
												)
										)
										.then(
											CommandManager.literal("destroy")
												.executes(
													context -> execute(
															context.getSource(),
															BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")),
															BlockStateArgumentType.getBlockState(context, "block"),
															FillCommand.Mode.DESTROY,
															null
														)
												)
										)
								)
						)
				)
		);
	}

	private static int execute(
		ServerCommandSource source, BlockBox range, BlockStateArgument block, FillCommand.Mode mode, @Nullable Predicate<CachedBlockPosition> filter
	) throws CommandSyntaxException {
		int i = range.getBlockCountX() * range.getBlockCountY() * range.getBlockCountZ();
		if (i > 32768) {
			throw TOO_BIG_EXCEPTION.create(32768, i);
		} else {
			List<BlockPos> list = Lists.<BlockPos>newArrayList();
			ServerWorld serverWorld = source.getWorld();
			int j = 0;

			for (BlockPos blockPos : BlockPos.iterate(range.getMinX(), range.getMinY(), range.getMinZ(), range.getMaxX(), range.getMaxY(), range.getMaxZ())) {
				if (filter == null || filter.test(new CachedBlockPosition(serverWorld, blockPos, true))) {
					BlockStateArgument blockStateArgument = mode.filter.filter(range, blockPos, block, serverWorld);
					if (blockStateArgument != null) {
						BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
						Clearable.clear(blockEntity);
						if (blockStateArgument.setBlockState(serverWorld, blockPos, Block.NOTIFY_LISTENERS)) {
							list.add(blockPos.toImmutable());
							j++;
						}
					}
				}
			}

			for (BlockPos blockPosx : list) {
				Block block2 = serverWorld.getBlockState(blockPosx).getBlock();
				serverWorld.updateNeighbors(blockPosx, block2);
			}

			if (j == 0) {
				throw FAILED_EXCEPTION.create();
			} else {
				source.sendFeedback(Text.method_43469("commands.fill.success", j), true);
				return j;
			}
		}
	}

	static enum Mode {
		REPLACE((range, pos, block, world) -> block),
		OUTLINE(
			(range, pos, block, world) -> pos.getX() != range.getMinX()
						&& pos.getX() != range.getMaxX()
						&& pos.getY() != range.getMinY()
						&& pos.getY() != range.getMaxY()
						&& pos.getZ() != range.getMinZ()
						&& pos.getZ() != range.getMaxZ()
					? null
					: block
		),
		HOLLOW(
			(range, pos, block, world) -> pos.getX() != range.getMinX()
						&& pos.getX() != range.getMaxX()
						&& pos.getY() != range.getMinY()
						&& pos.getY() != range.getMaxY()
						&& pos.getZ() != range.getMinZ()
						&& pos.getZ() != range.getMaxZ()
					? FillCommand.AIR_BLOCK_ARGUMENT
					: block
		),
		DESTROY((range, pos, block, world) -> {
			world.breakBlock(pos, true);
			return block;
		});

		public final SetBlockCommand.Filter filter;

		private Mode(SetBlockCommand.Filter filter) {
			this.filter = filter;
		}
	}
}
