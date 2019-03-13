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
import net.minecraft.command.arguments.BlockArgument;
import net.minecraft.command.arguments.BlockArgumentType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;

public class FillCommand {
	private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("commands.fill.toobig", object, object2)
	);
	private static final BlockArgument field_13648 = new BlockArgument(Blocks.field_10124.method_9564(), Collections.emptySet(), null);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.fill.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("fill")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("from", BlockPosArgumentType.create())
						.then(
							ServerCommandManager.argument("to", BlockPosArgumentType.create())
								.then(
									ServerCommandManager.argument("block", BlockArgumentType.create())
										.executes(
											commandContext -> method_13354(
													commandContext.getSource(),
													new MutableIntBoundingBox(BlockPosArgumentType.method_9696(commandContext, "from"), BlockPosArgumentType.method_9696(commandContext, "to")),
													BlockArgumentType.method_9655(commandContext, "block"),
													FillCommand.class_3058.field_13655,
													null
												)
										)
										.then(
											ServerCommandManager.literal("replace")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new MutableIntBoundingBox(BlockPosArgumentType.method_9696(commandContext, "from"), BlockPosArgumentType.method_9696(commandContext, "to")),
															BlockArgumentType.method_9655(commandContext, "block"),
															FillCommand.class_3058.field_13655,
															null
														)
												)
												.then(
													ServerCommandManager.argument("filter", BlockPredicateArgumentType.create())
														.executes(
															commandContext -> method_13354(
																	commandContext.getSource(),
																	new MutableIntBoundingBox(BlockPosArgumentType.method_9696(commandContext, "from"), BlockPosArgumentType.method_9696(commandContext, "to")),
																	BlockArgumentType.method_9655(commandContext, "block"),
																	FillCommand.class_3058.field_13655,
																	BlockPredicateArgumentType.getPredicateArgument(commandContext, "filter")
																)
														)
												)
										)
										.then(
											ServerCommandManager.literal("keep")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new MutableIntBoundingBox(BlockPosArgumentType.method_9696(commandContext, "from"), BlockPosArgumentType.method_9696(commandContext, "to")),
															BlockArgumentType.method_9655(commandContext, "block"),
															FillCommand.class_3058.field_13655,
															cachedBlockPosition -> cachedBlockPosition.getWorld().method_8623(cachedBlockPosition.method_11683())
														)
												)
										)
										.then(
											ServerCommandManager.literal("outline")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new MutableIntBoundingBox(BlockPosArgumentType.method_9696(commandContext, "from"), BlockPosArgumentType.method_9696(commandContext, "to")),
															BlockArgumentType.method_9655(commandContext, "block"),
															FillCommand.class_3058.field_13652,
															null
														)
												)
										)
										.then(
											ServerCommandManager.literal("hollow")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new MutableIntBoundingBox(BlockPosArgumentType.method_9696(commandContext, "from"), BlockPosArgumentType.method_9696(commandContext, "to")),
															BlockArgumentType.method_9655(commandContext, "block"),
															FillCommand.class_3058.field_13656,
															null
														)
												)
										)
										.then(
											ServerCommandManager.literal("destroy")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new MutableIntBoundingBox(BlockPosArgumentType.method_9696(commandContext, "from"), BlockPosArgumentType.method_9696(commandContext, "to")),
															BlockArgumentType.method_9655(commandContext, "block"),
															FillCommand.class_3058.field_13651,
															null
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_13354(
		ServerCommandSource serverCommandSource,
		MutableIntBoundingBox mutableIntBoundingBox,
		BlockArgument blockArgument,
		FillCommand.class_3058 arg,
		@Nullable Predicate<CachedBlockPosition> predicate
	) throws CommandSyntaxException {
		int i = mutableIntBoundingBox.getBlockCountX() * mutableIntBoundingBox.getBlockCountY() * mutableIntBoundingBox.getBlockCountZ();
		if (i > 32768) {
			throw TOOBIG_EXCEPTION.create(32768, i);
		} else {
			List<BlockPos> list = Lists.<BlockPos>newArrayList();
			ServerWorld serverWorld = serverCommandSource.method_9225();
			int j = 0;

			for (BlockPos blockPos : BlockPos.iterateBoxPositions(
				mutableIntBoundingBox.minX,
				mutableIntBoundingBox.minY,
				mutableIntBoundingBox.minZ,
				mutableIntBoundingBox.maxX,
				mutableIntBoundingBox.maxY,
				mutableIntBoundingBox.maxZ
			)) {
				if (predicate == null || predicate.test(new CachedBlockPosition(serverWorld, blockPos, true))) {
					BlockArgument blockArgument2 = arg.field_13654.filter(mutableIntBoundingBox, blockPos, blockArgument, serverWorld);
					if (blockArgument2 != null) {
						BlockEntity blockEntity = serverWorld.method_8321(blockPos);
						Clearable.clear(blockEntity);
						if (blockArgument2.method_9495(serverWorld, blockPos, 2)) {
							list.add(blockPos.toImmutable());
							j++;
						}
					}
				}
			}

			for (BlockPos blockPosx : list) {
				Block block = serverWorld.method_8320(blockPosx).getBlock();
				serverWorld.method_8408(blockPosx, block);
			}

			if (j == 0) {
				throw FAILED_EXCEPTION.create();
			} else {
				serverCommandSource.method_9226(new TranslatableTextComponent("commands.fill.success", j), true);
				return j;
			}
		}
	}

	static enum class_3058 {
		field_13655((mutableIntBoundingBox, blockPos, blockArgument, serverWorld) -> blockArgument),
		field_13652(
			(mutableIntBoundingBox, blockPos, blockArgument, serverWorld) -> blockPos.getX() != mutableIntBoundingBox.minX
						&& blockPos.getX() != mutableIntBoundingBox.maxX
						&& blockPos.getY() != mutableIntBoundingBox.minY
						&& blockPos.getY() != mutableIntBoundingBox.maxY
						&& blockPos.getZ() != mutableIntBoundingBox.minZ
						&& blockPos.getZ() != mutableIntBoundingBox.maxZ
					? null
					: blockArgument
		),
		field_13656(
			(mutableIntBoundingBox, blockPos, blockArgument, serverWorld) -> blockPos.getX() != mutableIntBoundingBox.minX
						&& blockPos.getX() != mutableIntBoundingBox.maxX
						&& blockPos.getY() != mutableIntBoundingBox.minY
						&& blockPos.getY() != mutableIntBoundingBox.maxY
						&& blockPos.getZ() != mutableIntBoundingBox.minZ
						&& blockPos.getZ() != mutableIntBoundingBox.maxZ
					? FillCommand.field_13648
					: blockArgument
		),
		field_13651((mutableIntBoundingBox, blockPos, blockArgument, serverWorld) -> {
			serverWorld.method_8651(blockPos, true);
			return blockArgument;
		});

		public final SetBlockCommand.class_3120 field_13654;

		private class_3058(SetBlockCommand.class_3120 arg) {
			this.field_13654 = arg;
		}
	}
}
