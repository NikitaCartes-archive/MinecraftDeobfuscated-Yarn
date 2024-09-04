package net.minecraft.command.argument;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface PosArgument {
	Vec3d getPos(ServerCommandSource source, boolean relativeIfPossible);

	Vec2f getRotation(ServerCommandSource source, boolean relativeIfPossible);

	default Vec3d toAbsolutePos(ServerCommandSource source) {
		return this.getPos(source, false);
	}

	default Vec2f toAbsoluteRotation(ServerCommandSource source) {
		return this.getRotation(source, false);
	}

	default BlockPos toAbsoluteBlockPos(ServerCommandSource source) {
		return BlockPos.ofFloored(this.getPos(source, false));
	}

	boolean isXRelative();

	boolean isYRelative();

	boolean isZRelative();
}
