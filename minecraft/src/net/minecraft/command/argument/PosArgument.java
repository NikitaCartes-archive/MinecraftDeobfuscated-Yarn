package net.minecraft.command.argument;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface PosArgument {
	Vec3d toAbsolutePos(ServerCommandSource source);

	Vec2f toAbsoluteRotation(ServerCommandSource source);

	default BlockPos toAbsoluteBlockPos(ServerCommandSource source) {
		return BlockPos.ofFloored(this.toAbsolutePos(source));
	}

	boolean isXRelative();

	boolean isYRelative();

	boolean isZRelative();
}
