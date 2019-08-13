package net.minecraft.command.arguments;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface PosArgument {
	Vec3d toAbsolutePos(ServerCommandSource serverCommandSource);

	Vec2f toAbsoluteRotation(ServerCommandSource serverCommandSource);

	default BlockPos toAbsoluteBlockPos(ServerCommandSource serverCommandSource) {
		return new BlockPos(this.toAbsolutePos(serverCommandSource));
	}

	boolean isXRelative();

	boolean isYRelative();

	boolean isZRelative();
}
