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
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;

public class FillCommand {
	private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("commands.fill.toobig", object, object2)
	);
	private static final BlockStateArgument AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.field_10124.getDefaultState(), Collections.emptySet(), null);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.fill.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("fill")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("from", BlockPosArgumentType.create())
						.then(
							CommandManager.argument("to", BlockPosArgumentType.create())
								.then(
									CommandManager.argument("block", BlockStateArgumentType.create())
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													new MutableIntBoundingBox(
														BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")
													),
													BlockStateArgumentType.getBlockState(commandContext, "block"),
													FillCommand.Mode.field_13655,
													null
												)
										)
										.then(
											CommandManager.literal("replace")
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															new MutableIntBoundingBox(
																BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")
															),
															BlockStateArgumentType.getBlockState(commandContext, "block"),
															FillCommand.Mode.field_13655,
															null
														)
												)
												.then(
													CommandManager.argument("filter", BlockPredicateArgumentType.create())
														.executes(
															commandContext -> execute(
																	commandContext.getSource(),
																	new MutableIntBoundingBox(
																		BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")
																	),
																	BlockStateArgumentType.getBlockState(commandContext, "block"),
																	FillCommand.Mode.field_13655,
																	BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter")
																)
														)
												)
										)
										.then(
											CommandManager.literal("keep")
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															new MutableIntBoundingBox(
																BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")
															),
															BlockStateArgumentType.getBlockState(commandContext, "block"),
															FillCommand.Mode.field_13655,
															cachedBlockPosition -> cachedBlockPosition.getWorld().isAir(cachedBlockPosition.getBlockPos())
														)
												)
										)
										.then(
											CommandManager.literal("outline")
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															new MutableIntBoundingBox(
																BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")
															),
															BlockStateArgumentType.getBlockState(commandContext, "block"),
															FillCommand.Mode.field_13652,
															null
														)
												)
										)
										.then(
											CommandManager.literal("hollow")
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															new MutableIntBoundingBox(
																BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")
															),
															BlockStateArgumentType.getBlockState(commandContext, "block"),
															FillCommand.Mode.field_13656,
															null
														)
												)
										)
										.then(
											CommandManager.literal("destroy")
												.executes(
													commandContext -> execute(
															commandContext.getSource(),
															new MutableIntBoundingBox(
																BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")
															),
															BlockStateArgumentType.getBlockState(commandContext, "block"),
															FillCommand.Mode.field_13651,
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
		ServerCommandSource serverCommandSource,
		MutableIntBoundingBox mutableIntBoundingBox,
		BlockStateArgument blockStateArgument,
		FillCommand.Mode mode,
		@Nullable Predicate<CachedBlockPosition> predicate
	) throws CommandSyntaxException {
		int i = mutableIntBoundingBox.getBlockCountX() * mutableIntBoundingBox.getBlockCountY() * mutableIntBoundingBox.getBlockCountZ();
		if (i > 32768) {
			throw TOOBIG_EXCEPTION.create(32768, i);
		} else {
			List<BlockPos> list = Lists.<BlockPos>newArrayList();
			ServerWorld serverWorld = serverCommandSource.getWorld();
			int j = 0;

			for (BlockPos blockPos : BlockPos.iterate(
				mutableIntBoundingBox.minX,
				mutableIntBoundingBox.minY,
				mutableIntBoundingBox.minZ,
				mutableIntBoundingBox.maxX,
				mutableIntBoundingBox.maxY,
				mutableIntBoundingBox.maxZ
			)) {
				if (predicate == null || predicate.test(new CachedBlockPosition(serverWorld, blockPos, true))) {
					BlockStateArgument blockStateArgument2 = mode.filter.filter(mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld);
					if (blockStateArgument2 != null) {
						BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
						Clearable.clear(blockEntity);
						if (blockStateArgument2.setBlockState(serverWorld, blockPos, 2)) {
							list.add(blockPos.toImmutable());
							j++;
						}
					}
				}
			}

			for (BlockPos blockPosx : list) {
				Block block = serverWorld.getBlockState(blockPosx).getBlock();
				serverWorld.updateNeighbors(blockPosx, block);
			}

			if (j == 0) {
				throw FAILED_EXCEPTION.create();
			} else {
				serverCommandSource.sendFeedback(new TranslatableComponent("commands.fill.success", j), true);
				return j;
			}
		}
	}

	static enum Mode {
		field_13655((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> blockStateArgument),
		field_13652(
			(mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> blockPos.getX() != mutableIntBoundingBox.minX
						&& blockPos.getX() != mutableIntBoundingBox.maxX
						&& blockPos.getY() != mutableIntBoundingBox.minY
						&& blockPos.getY() != mutableIntBoundingBox.maxY
						&& blockPos.getZ() != mutableIntBoundingBox.minZ
						&& blockPos.getZ() != mutableIntBoundingBox.maxZ
					? null
					: blockStateArgument
		),
		field_13656(
			(mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> blockPos.getX() != mutableIntBoundingBox.minX
						&& blockPos.getX() != mutableIntBoundingBox.maxX
						&& blockPos.getY() != mutableIntBoundingBox.minY
						&& blockPos.getY() != mutableIntBoundingBox.maxY
						&& blockPos.getZ() != mutableIntBoundingBox.minZ
						&& blockPos.getZ() != mutableIntBoundingBox.maxZ
					? FillCommand.AIR_BLOCK_ARGUMENT
					: blockStateArgument
		),
		field_13651((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> {
			serverWorld.breakBlock(blockPos, true);
			return blockStateArgument;
		});

		public final SetBlockCommand.Filter filter;

		private Mode(SetBlockCommand.Filter filter) {
			this.filter = filter;
		}
	}
}
