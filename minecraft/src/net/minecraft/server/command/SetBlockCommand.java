package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_2247;
import net.minecraft.class_3829;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.BlockProxy;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;

public class SetBlockCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.setblock.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("setblock")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("pos", BlockPosArgumentType.create())
						.then(
							ServerCommandManager.argument("block", BlockStateArgumentType.create())
								.executes(
									commandContext -> method_13620(
											commandContext.getSource(),
											BlockPosArgumentType.getValidPosArgument(commandContext, "pos"),
											BlockStateArgumentType.method_9655(commandContext, "block"),
											SetBlockCommand.class_3121.field_13722,
											null
										)
								)
								.then(
									ServerCommandManager.literal("destroy")
										.executes(
											commandContext -> method_13620(
													commandContext.getSource(),
													BlockPosArgumentType.getValidPosArgument(commandContext, "pos"),
													BlockStateArgumentType.method_9655(commandContext, "block"),
													SetBlockCommand.class_3121.field_13721,
													null
												)
										)
								)
								.then(
									ServerCommandManager.literal("keep")
										.executes(
											commandContext -> method_13620(
													commandContext.getSource(),
													BlockPosArgumentType.getValidPosArgument(commandContext, "pos"),
													BlockStateArgumentType.method_9655(commandContext, "block"),
													SetBlockCommand.class_3121.field_13722,
													blockProxy -> blockProxy.method_11679().isAir(blockProxy.getPos())
												)
										)
								)
								.then(
									ServerCommandManager.literal("replace")
										.executes(
											commandContext -> method_13620(
													commandContext.getSource(),
													BlockPosArgumentType.getValidPosArgument(commandContext, "pos"),
													BlockStateArgumentType.method_9655(commandContext, "block"),
													SetBlockCommand.class_3121.field_13722,
													null
												)
										)
								)
						)
				)
		);
	}

	private static int method_13620(
		ServerCommandSource serverCommandSource, BlockPos blockPos, class_2247 arg, SetBlockCommand.class_3121 arg2, @Nullable Predicate<BlockProxy> predicate
	) throws CommandSyntaxException {
		ServerWorld serverWorld = serverCommandSource.getWorld();
		if (predicate != null && !predicate.test(new BlockProxy(serverWorld, blockPos, true))) {
			throw FAILED_EXCEPTION.create();
		} else {
			boolean bl;
			if (arg2 == SetBlockCommand.class_3121.field_13721) {
				serverWorld.breakBlock(blockPos, true);
				bl = !arg.method_9494().isAir();
			} else {
				BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
				class_3829.method_16825(blockEntity);
				bl = true;
			}

			if (bl && !arg.method_9495(serverWorld, blockPos, 2)) {
				throw FAILED_EXCEPTION.create();
			} else {
				serverWorld.updateNeighbors(blockPos, arg.method_9494().getBlock());
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.setblock.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
				return 1;
			}
		}
	}

	public interface class_3120 {
		@Nullable
		class_2247 filter(MutableIntBoundingBox mutableIntBoundingBox, BlockPos blockPos, class_2247 arg, ServerWorld serverWorld);
	}

	public static enum class_3121 {
		field_13722,
		field_13721;
	}
}
