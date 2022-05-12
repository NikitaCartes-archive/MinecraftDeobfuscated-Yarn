package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.BlockMirror;

public class BlockMirrorArgumentType extends EnumArgumentType<BlockMirror> {
	private BlockMirrorArgumentType() {
		super(BlockMirror.CODEC, BlockMirror::values);
	}

	public static EnumArgumentType<BlockMirror> blockMirror() {
		return new BlockMirrorArgumentType();
	}

	public static BlockMirror getBlockMirror(CommandContext<ServerCommandSource> context, String id) {
		return context.getArgument(id, BlockMirror.class);
	}
}
