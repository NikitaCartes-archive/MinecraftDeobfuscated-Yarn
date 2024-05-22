package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;

public interface Portal {
	default int getPortalDelay(ServerWorld world, Entity entity) {
		return 0;
	}

	@Nullable
	TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos);

	default Portal.Effect getPortalEffect() {
		return Portal.Effect.NONE;
	}

	public static enum Effect {
		CONFUSION,
		NONE;
	}
}
