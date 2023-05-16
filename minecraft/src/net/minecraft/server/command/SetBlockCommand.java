package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class SetBlockCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.setblock.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
		dispatcher.register(
			CommandManager.literal("setblock")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("pos", BlockPosArgumentType.blockPos())
						.then(
							CommandManager.argument("block", BlockStateArgumentType.blockState(commandRegistryAccess))
								.executes(
									context -> execute(
											context.getSource(),
											BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
											BlockStateArgumentType.getBlockState(context, "block"),
											SetBlockCommand.Mode.REPLACE,
											null
										)
								)
								.then(
									CommandManager.literal("destroy")
										.executes(
											context -> execute(
													context.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
													BlockStateArgumentType.getBlockState(context, "block"),
													SetBlockCommand.Mode.DESTROY,
													null
												)
										)
								)
								.then(
									CommandManager.literal("keep")
										.executes(
											context -> execute(
													context.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
													BlockStateArgumentType.getBlockState(context, "block"),
													SetBlockCommand.Mode.REPLACE,
													pos -> pos.getWorld().isAir(pos.getBlockPos())
												)
										)
								)
								.then(
									CommandManager.literal("replace")
										.executes(
											context -> execute(
													context.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
													BlockStateArgumentType.getBlockState(context, "block"),
													SetBlockCommand.Mode.REPLACE,
													null
												)
										)
								)
						)
				)
		);
	}

	private static int execute(
		ServerCommandSource source, BlockPos pos, BlockStateArgument block, SetBlockCommand.Mode mode, @Nullable Predicate<CachedBlockPosition> condition
	) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		if (condition != null && !condition.test(new CachedBlockPosition(serverWorld, pos, true))) {
			throw FAILED_EXCEPTION.create();
		} else {
			boolean bl;
			if (mode == SetBlockCommand.Mode.DESTROY) {
				serverWorld.breakBlock(pos, true);
				bl = !block.getBlockState().isAir() || !serverWorld.getBlockState(pos).isAir();
			} else {
				BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
				Clearable.clear(blockEntity);
				bl = true;
			}

			if (bl && !block.setBlockState(serverWorld, pos, Block.NOTIFY_LISTENERS)) {
				throw FAILED_EXCEPTION.create();
			} else {
				serverWorld.updateNeighbors(pos, block.getBlockState().getBlock());
				source.sendFeedback(() -> Text.translatable("commands.setblock.success", pos.getX(), pos.getY(), pos.getZ()), true);
				return 1;
			}
		}
	}

	public interface Filter {
		@Nullable
		BlockStateArgument filter(BlockBox box, BlockPos pos, BlockStateArgument block, ServerWorld world);
	}

	public static enum Mode {
		REPLACE,
		DESTROY;
	}
}
