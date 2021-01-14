package net.minecraft.server.world;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.stream.Stream;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerChunkWatchingManager {
	private final Object2BooleanMap<ServerPlayerEntity> watchingPlayers = new Object2BooleanOpenHashMap<>();

	public Stream<ServerPlayerEntity> getPlayersWatchingChunk(long l) {
		return this.watchingPlayers.keySet().stream();
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

	public boolean method_21715(ServerPlayerEntity serverPlayerEntity) {
		return this.watchingPlayers.getOrDefault(serverPlayerEntity, true);
	}

	public boolean isWatchDisabled(ServerPlayerEntity player) {
		return this.watchingPlayers.getBoolean(player);
	}

	public void movePlayer(long prevPos, long currentPos, ServerPlayerEntity player) {
	}
}
