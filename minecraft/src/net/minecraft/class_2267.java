package net.minecraft;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface class_2267 {
	Vec3d method_9708(ServerCommandSource serverCommandSource);

	Vec2f method_9709(ServerCommandSource serverCommandSource);

	default BlockPos method_9704(ServerCommandSource serverCommandSource) {
		return new BlockPos(this.method_9708(serverCommandSource));
	}

	boolean method_9705();

	boolean method_9706();

	boolean method_9707();
}
