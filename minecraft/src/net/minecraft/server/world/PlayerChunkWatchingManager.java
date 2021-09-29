package net.minecraft.server.world;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Set;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerChunkWatchingManager {
	private final Object2BooleanMap<ServerPlayerEntity> watchingPlayers = new Object2BooleanOpenHashMap<>();

	public Set<ServerPlayerEntity> getPlayersWatchingChunk(long l) {
		return this.watchingPlayers.keySet();
	}

	public void add(long l, ServerPlayerEntity player, boolean watchDisabled) {
		this.watchingPlayers.put(player, watchDisabled);
	}

	public void remove(long l, ServerPlayerEntity player) {
		this.watchingPlayers.removeBoolean(player);
	}

	public void disableWatch(ServerPlayerEntity player) {
		this.watchingPlayers.replace(player, true);
	}

	public void enableWatch(ServerPlayerEntity player) {
		this.watchingPlayers.replace(player, false);
	}

	public boolean isWatchInactive(ServerPlayerEntity player) {
		return this.watchingPlayers.getOrDefault(player, true);
	}

	public boolean isWatchDisabled(ServerPlayerEntity player) {
		return this.watchingPlayers.getBoolean(player);
	}

	public void movePlayer(long prevPos, long currentPos, ServerPlayerEntity player) {
	}
}
