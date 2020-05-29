package net.minecraft.world.gen;

import net.minecraft.server.world.ServerWorld;

public interface Spawner {
	int spawn(ServerWorld serverWorld, boolean bl, boolean bl2);
}
