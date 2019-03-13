package net.minecraft.server.world;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.network.ServerPlayerEntity;

final class PlayerChunkWatchingManager {
	private final Set<ServerPlayerEntity> watchingPlayers = Sets.<ServerPlayerEntity>newHashSet();
	private final Set<ServerPlayerEntity> notWatchingPlayers = Sets.<ServerPlayerEntity>newHashSet();

	public Stream<ServerPlayerEntity> getPlayersWatchingChunk(long l) {
		return this.watchingPlayers.stream();
	}

	public void method_14085(long l, ServerPlayerEntity serverPlayerEntity, boolean bl) {
		(bl ? this.notWatchingPlayers : this.watchingPlayers).add(serverPlayerEntity);
	}

	public void method_14084(long l, ServerPlayerEntity serverPlayerEntity) {
		this.watchingPlayers.remove(serverPlayerEntity);
		this.notWatchingPlayers.remove(serverPlayerEntity);
	}

	public void method_14086(ServerPlayerEntity serverPlayerEntity) {
		this.notWatchingPlayers.add(serverPlayerEntity);
		this.watchingPlayers.remove(serverPlayerEntity);
	}

	public void method_14087(ServerPlayerEntity serverPlayerEntity) {
		this.notWatchingPlayers.remove(serverPlayerEntity);
		this.watchingPlayers.add(serverPlayerEntity);
	}

	public boolean method_14082(ServerPlayerEntity serverPlayerEntity) {
		return !this.watchingPlayers.contains(serverPlayerEntity);
	}

	public void method_14081(long l, long m, ServerPlayerEntity serverPlayerEntity) {
	}
}
