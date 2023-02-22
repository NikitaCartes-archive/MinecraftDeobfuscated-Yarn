package net.minecraft.world.event.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.event.GameEvent;

/**
 * Dispatches game events to {@link GameEventDispatcher} instance in the applicable
 * chunk sections.
 */
public class GameEventDispatchManager {
	private final ServerWorld world;

	public GameEventDispatchManager(ServerWorld world) {
		this.world = world;
	}

	public void dispatch(GameEvent event, Vec3d emitterPos, GameEvent.Emitter emitter) {
		int i = event.getRange();
		BlockPos blockPos = BlockPos.ofFloored(emitterPos);
		int j = ChunkSectionPos.getSectionCoord(blockPos.getX() - i);
		int k = ChunkSectionPos.getSectionCoord(blockPos.getY() - i);
		int l = ChunkSectionPos.getSectionCoord(blockPos.getZ() - i);
		int m = ChunkSectionPos.getSectionCoord(blockPos.getX() + i);
		int n = ChunkSectionPos.getSectionCoord(blockPos.getY() + i);
		int o = ChunkSectionPos.getSectionCoord(blockPos.getZ() + i);
		List<GameEvent.Message> list = new ArrayList();
		GameEventDispatcher.DispatchCallback dispatchCallback = (listener, listenerPos) -> {
			if (listener.getTriggerOrder() == GameEventListener.TriggerOrder.BY_DISTANCE) {
				list.add(new GameEvent.Message(event, emitterPos, emitter, listener, listenerPos));
			} else {
				listener.listen(this.world, event, emitter, emitterPos);
			}
		};
		boolean bl = false;

		for (int p = j; p <= m; p++) {
			for (int q = l; q <= o; q++) {
				Chunk chunk = this.world.getChunkManager().getWorldChunk(p, q);
				if (chunk != null) {
					for (int r = k; r <= n; r++) {
						bl |= chunk.getGameEventDispatcher(r).dispatch(event, emitterPos, emitter, dispatchCallback);
					}
				}
			}
		}

		if (!list.isEmpty()) {
			this.dispatchListenersByDistance(list);
		}

		if (bl) {
			DebugInfoSender.sendGameEvent(this.world, event, emitterPos);
		}
	}

	private void dispatchListenersByDistance(List<GameEvent.Message> messages) {
		Collections.sort(messages);

		for (GameEvent.Message message : messages) {
			GameEventListener gameEventListener = message.getListener();
			gameEventListener.listen(this.world, message.getEvent(), message.getEmitter(), message.getEmitterPos());
		}
	}
}
