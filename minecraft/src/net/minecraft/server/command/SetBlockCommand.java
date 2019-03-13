package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockArgument;
import net.minecraft.command.arguments.BlockArgumentType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Clearable;
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
							ServerCommandManager.argument("block", BlockArgumentType.create())
								.executes(
									commandContext -> method_13620(
											commandContext.getSource(),
											BlockPosArgumentType.method_9696(commandContext, "pos"),
											BlockArgumentType.method_9655(commandContext, "block"),
											SetBlockCommand.class_3121.field_13722,
											null
										)
								)
								.then(
									ServerCommandManager.literal("destroy")
										.executes(
											commandContext -> method_13620(
													commandContext.getSource(),
													BlockPosArgumentType.method_9696(commandContext, "pos"),
													BlockArgumentType.method_9655(commandContext, "block"),
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
													BlockPosArgumentType.method_9696(commandContext, "pos"),
													BlockArgumentType.method_9655(commandContext, "block"),
													SetBlockCommand.class_3121.field_13722,
													cachedBlockPosition -> cachedBlockPosition.getWorld().method_8623(cachedBlockPosition.method_11683())
												)
										)
								)
								.then(
									ServerCommandManager.literal("replace")
										.executes(
											commandContext -> method_13620(
													commandContext.getSource(),
													BlockPosArgumentType.method_9696(commandContext, "pos"),
													BlockArgumentType.method_9655(commandContext, "block"),
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
		ServerCommandSource serverCommandSource,
		BlockPos blockPos,
		BlockArgument blockArgument,
		SetBlockCommand.class_3121 arg,
		@Nullable Predicate<CachedBlockPosition> predicate
	) throws CommandSyntaxException {
		ServerWorld serverWorld = serverCommandSource.method_9225();
		if (predicate != null && !predicate.test(new CachedBlockPosition(serverWorld, blockPos, true))) {
			throw FAILED_EXCEPTION.create();
		} else {
			boolean bl;
			if (arg == SetBlockCommand.class_3121.field_13721) {
				serverWorld.method_8651(blockPos, true);
				bl = !blockArgument.getBlockState().isAir();
			} else {
				BlockEntity blockEntity = serverWorld.method_8321(blockPos);
				Clearable.clear(blockEntity);
				bl = true;
			}

			if (bl && !blockArgument.method_9495(serverWorld, blockPos, 2)) {
				throw FAILED_EXCEPTION.create();
			} else {
				serverWorld.method_8408(blockPos, blockArgument.getBlockState().getBlock());
				serverCommandSource.method_9226(new TranslatableTextComponent("commands.setblock.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
				return 1;
			}
		}
	}

	public interface class_3120 {
		@Nullable
		BlockArgument filter(MutableIntBoundingBox mutableIntBoundingBox, BlockPos blockPos, BlockArgument blockArgument, ServerWorld serverWorld);
	}

	public static enum class_3121 {
		field_13722,
		field_13721;
	}
}
