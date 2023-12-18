package net.minecraft.world.event.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

/**
 * A simple game event dispatcher implementation that has hooks to
 * debug info senders.
 * 
 * @apiNote Vanilla Minecraft creates it on a per-chunk-section basis.
 */
public class SimpleGameEventDispatcher implements GameEventDispatcher {
	private final List<GameEventListener> listeners = Lists.<GameEventListener>newArrayList();
	private final Set<GameEventListener> toRemove = Sets.<GameEventListener>newHashSet();
	private final List<GameEventListener> toAdd = Lists.<GameEventListener>newArrayList();
	private boolean dispatching;
	private final ServerWorld world;
	private final int ySectionCoord;
	private final SimpleGameEventDispatcher.DisposalCallback disposalCallback;

	public SimpleGameEventDispatcher(ServerWorld world, int ySectionCoord, SimpleGameEventDispatcher.DisposalCallback disposalCallback) {
		this.world = world;
		this.ySectionCoord = ySectionCoord;
		this.disposalCallback = disposalCallback;
	}

	@Override
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}

	@Override
	public void addListener(GameEventListener listener) {
		if (this.dispatching) {
			this.toAdd.add(listener);
		} else {
			this.listeners.add(listener);
		}

		DebugInfoSender.sendGameEventListener(this.world, listener);
	}

	@Override
	public void removeListener(GameEventListener listener) {
		if (this.dispatching) {
			this.toRemove.add(listener);
		} else {
			this.listeners.remove(listener);
		}

		if (this.listeners.isEmpty()) {
			this.disposalCallback.apply(this.ySectionCoord);
		}
	}

	@Override
	public boolean dispatch(RegistryEntry<GameEvent> event, Vec3d pos, GameEvent.Emitter emitter, GameEventDispatcher.DispatchCallback callback) {
		this.dispatching = true;
		boolean bl = false;

		try {
			Iterator<GameEventListener> iterator = this.listeners.iterator();

			while (iterator.hasNext()) {
				GameEventListener gameEventListener = (GameEventListener)iterator.next();
				if (this.toRemove.remove(gameEventListener)) {
					iterator.remove();
				} else {
					Optional<Vec3d> optional = dispatchTo(this.world, pos, gameEventListener);
					if (optional.isPresent()) {
						callback.visit(gameEventListener, (Vec3d)optional.get());
						bl = true;
					}
				}
			}
		} finally {
			this.dispatching = false;
		}

		if (!this.toAdd.isEmpty()) {
			this.listeners.addAll(this.toAdd);
			this.toAdd.clear();
		}

		if (!this.toRemove.isEmpty()) {
			this.listeners.removeAll(this.toRemove);
			this.toRemove.clear();
		}

		return bl;
	}

	private static Optional<Vec3d> dispatchTo(ServerWorld world, Vec3d listenerPos, GameEventListener listener) {
		Optional<Vec3d> optional = listener.getPositionSource().getPos(world);
		if (optional.isEmpty()) {
			return Optional.empty();
		} else {
			double d = BlockPos.ofFloored((Position)optional.get()).getSquaredDistance(BlockPos.ofFloored(listenerPos));
			int i = listener.getRange() * listener.getRange();
			return d > (double)i ? Optional.empty() : optional;
		}
	}

	@FunctionalInterface
	public interface DisposalCallback {
		void apply(int ySectionCoord);
	}
}
