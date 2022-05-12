package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.BlockRotation;

public class BlockRotationArgumentType extends EnumArgumentType<BlockRotation> {
	private BlockRotationArgumentType() {
		super(BlockRotation.CODEC, BlockRotation::values);
	}

	public static BlockRotationArgumentType blockRotation() {
		return new BlockRotationArgumentType();
	}

	public static BlockRotation getBlockRotation(CommandContext<ServerCommandSource> context, String id) {
		return context.getArgument(id, BlockRotation.class);
	}
}
