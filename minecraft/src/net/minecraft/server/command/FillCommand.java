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
import net.minecraft.class_2247;
import net.minecraft.class_3829;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.BlockProxy;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;

public class FillCommand {
	private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("commands.fill.toobig", object, object2)
	);
	private static final class_2247 field_13648 = new class_2247(Blocks.field_10124.getDefaultState(), Collections.emptySet(), null);
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
									ServerCommandManager.argument("block", BlockStateArgumentType.create())
										.executes(
											commandContext -> method_13354(
													commandContext.getSource(),
													new MutableIntBoundingBox(
														BlockPosArgumentType.getValidPosArgument(commandContext, "from"), BlockPosArgumentType.getValidPosArgument(commandContext, "to")
													),
													BlockStateArgumentType.method_9655(commandContext, "block"),
													FillCommand.class_3058.field_13655,
													null
												)
										)
										.then(
											ServerCommandManager.literal("replace")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new MutableIntBoundingBox(
																BlockPosArgumentType.getValidPosArgument(commandContext, "from"), BlockPosArgumentType.getValidPosArgument(commandContext, "to")
															),
															BlockStateArgumentType.method_9655(commandContext, "block"),
															FillCommand.class_3058.field_13655,
															null
														)
												)
												.then(
													ServerCommandManager.argument("filter", BlockPredicateArgumentType.create())
														.executes(
															commandContext -> method_13354(
																	commandContext.getSource(),
																	new MutableIntBoundingBox(
																		BlockPosArgumentType.getValidPosArgument(commandContext, "from"), BlockPosArgumentType.getValidPosArgument(commandContext, "to")
																	),
																	BlockStateArgumentType.method_9655(commandContext, "block"),
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
															new MutableIntBoundingBox(
																BlockPosArgumentType.getValidPosArgument(commandContext, "from"), BlockPosArgumentType.getValidPosArgument(commandContext, "to")
															),
															BlockStateArgumentType.method_9655(commandContext, "block"),
															FillCommand.class_3058.field_13655,
															blockProxy -> blockProxy.method_11679().isAir(blockProxy.getPos())
														)
												)
										)
										.then(
											ServerCommandManager.literal("outline")
												.executes(
													commandContext -> method_13354(
															commandContext.getSource(),
															new MutableIntBoundingBox(
																BlockPosArgumentType.getValidPosArgument(commandContext, "from"), BlockPosArgumentType.getValidPosArgument(commandContext, "to")
															),
															BlockStateArgumentType.method_9655(commandContext, "block"),
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
															new MutableIntBoundingBox(
																BlockPosArgumentType.getValidPosArgument(commandContext, "from"), BlockPosArgumentType.getValidPosArgument(commandContext, "to")
															),
															BlockStateArgumentType.method_9655(commandContext, "block"),
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
															new MutableIntBoundingBox(
																BlockPosArgumentType.getValidPosArgument(commandContext, "from"), BlockPosArgumentType.getValidPosArgument(commandContext, "to")
															),
															BlockStateArgumentType.method_9655(commandContext, "block"),
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
		class_2247 arg,
		FillCommand.class_3058 arg2,
		@Nullable Predicate<BlockProxy> predicate
	) throws CommandSyntaxException {
		int i = mutableIntBoundingBox.getBlockCountX() * mutableIntBoundingBox.getBlockCountY() * mutableIntBoundingBox.getBlockCountZ();
		if (i > 32768) {
			throw TOOBIG_EXCEPTION.create(32768, i);
		} else {
			List<BlockPos> list = Lists.<BlockPos>newArrayList();
			ServerWorld serverWorld = serverCommandSource.getWorld();
			int j = 0;

			for (BlockPos blockPos : BlockPos.Mutable.iterateBoxPositions(
				mutableIntBoundingBox.minX,
				mutableIntBoundingBox.minY,
				mutableIntBoundingBox.minZ,
				mutableIntBoundingBox.maxX,
				mutableIntBoundingBox.maxY,
				mutableIntBoundingBox.maxZ
			)) {
				if (predicate == null || predicate.test(new BlockProxy(serverWorld, blockPos, true))) {
					class_2247 lv = arg2.field_13654.filter(mutableIntBoundingBox, blockPos, arg, serverWorld);
					if (lv != null) {
						BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
						class_3829.method_16825(blockEntity);
						if (lv.method_9495(serverWorld, blockPos, 2)) {
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
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.fill.success", j), true);
				return j;
			}
		}
	}

	static enum class_3058 {
		field_13655((mutableIntBoundingBox, blockPos, arg, serverWorld) -> arg),
		field_13652(
			(mutableIntBoundingBox, blockPos, arg, serverWorld) -> blockPos.getX() != mutableIntBoundingBox.minX
						&& blockPos.getX() != mutableIntBoundingBox.maxX
						&& blockPos.getY() != mutableIntBoundingBox.minY
						&& blockPos.getY() != mutableIntBoundingBox.maxY
						&& blockPos.getZ() != mutableIntBoundingBox.minZ
						&& blockPos.getZ() != mutableIntBoundingBox.maxZ
					? null
					: arg
		),
		field_13656(
			(mutableIntBoundingBox, blockPos, arg, serverWorld) -> blockPos.getX() != mutableIntBoundingBox.minX
						&& blockPos.getX() != mutableIntBoundingBox.maxX
						&& blockPos.getY() != mutableIntBoundingBox.minY
						&& blockPos.getY() != mutableIntBoundingBox.maxY
						&& blockPos.getZ() != mutableIntBoundingBox.minZ
						&& blockPos.getZ() != mutableIntBoundingBox.maxZ
					? FillCommand.field_13648
					: arg
		),
		field_13651((mutableIntBoundingBox, blockPos, arg, serverWorld) -> {
			serverWorld.breakBlock(blockPos, true);
			return arg;
		});

		public final SetBlockCommand.class_3120 field_13654;

		private class_3058(SetBlockCommand.class_3120 arg) {
			this.field_13654 = arg;
		}
	}
}
