package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;

public interface class_9797 {
	default int method_60772(ServerWorld serverWorld, Entity entity) {
		return 0;
	}

	@Nullable
	TeleportTarget method_60770(ServerWorld serverWorld, Entity entity, BlockPos blockPos);

	default class_9797.class_9798 method_60778() {
		return class_9797.class_9798.NONE;
	}

	public static enum class_9798 {
		CONFUSION,
		NONE;
	}
}
