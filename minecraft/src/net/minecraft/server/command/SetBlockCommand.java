package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;

public class SetBlockCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.setblock.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("setblock")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("pos", BlockPosArgumentType.create())
						.then(
							CommandManager.argument("block", BlockStateArgumentType.create())
								.executes(
									commandContext -> execute(
											commandContext.getSource(),
											BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
											BlockStateArgumentType.getBlockState(commandContext, "block"),
											SetBlockCommand.Mode.field_13722,
											null
										)
								)
								.then(
									CommandManager.literal("destroy")
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
													BlockStateArgumentType.getBlockState(commandContext, "block"),
													SetBlockCommand.Mode.field_13721,
													null
												)
										)
								)
								.then(
									CommandManager.literal("keep")
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
													BlockStateArgumentType.getBlockState(commandContext, "block"),
													SetBlockCommand.Mode.field_13722,
													cachedBlockPosition -> cachedBlockPosition.getWorld().isAir(cachedBlockPosition.getBlockPos())
												)
										)
								)
								.then(
									CommandManager.literal("replace")
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
													BlockStateArgumentType.getBlockState(commandContext, "block"),
													SetBlockCommand.Mode.field_13722,
													null
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
		BlockStateArgument blockStateArgument,
		SetBlockCommand.Mode mode,
		@Nullable Predicate<CachedBlockPosition> predicate
	) throws CommandSyntaxException {
		ServerWorld serverWorld = serverCommandSource.getWorld();
		if (predicate != null && !predicate.test(new CachedBlockPosition(serverWorld, blockPos, true))) {
			throw FAILED_EXCEPTION.create();
		} else {
			boolean bl;
			if (mode == SetBlockCommand.Mode.field_13721) {
				serverWorld.breakBlock(blockPos, true);
				bl = !blockStateArgument.getBlockState().isAir();
			} else {
				BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
				Clearable.clear(blockEntity);
				bl = true;
			}

			if (bl && !blockStateArgument.setBlockState(serverWorld, blockPos, 2)) {
				throw FAILED_EXCEPTION.create();
			} else {
				serverWorld.updateNeighbors(blockPos, blockStateArgument.getBlockState().getBlock());
				serverCommandSource.sendFeedback(new TranslatableComponent("commands.setblock.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
				return 1;
			}
		}
	}

	public interface Filter {
		@Nullable
		BlockStateArgument filter(MutableIntBoundingBox mutableIntBoundingBox, BlockPos blockPos, BlockStateArgument blockStateArgument, ServerWorld serverWorld);
	}

	public static enum Mode {
		field_13722,
		field_13721;
	}
}
