package net.minecraft.command.argument;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface PosArgument {
	Vec3d getPos(ServerCommandSource source);

	Vec2f getRotation(ServerCommandSource source);

	default BlockPos toAbsoluteBlockPos(ServerCommandSource source) {
		return BlockPos.ofFloored(this.getPos(source));
	}

	boolean isXRelative();

	boolean isYRelative();

	boolean isZRelative();
}
