package net.minecraft.world.event.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * A simple game event dispatcher implementation that has hooks to
 * debug info senders.
 * 
 * @apiNote Vanilla Minecraft creates it on a per-chunk-section basis.
 */
public class SimpleGameEventDispatcher implements GameEventDispatcher {
	private final List<GameEventListener> listeners = Lists.<GameEventListener>newArrayList();
	private final Set<GameEventListener> field_36891 = Sets.<GameEventListener>newHashSet();
	private final List<GameEventListener> field_36892 = Lists.<GameEventListener>newArrayList();
	private boolean field_36893 = false;
	private final World world;

	public SimpleGameEventDispatcher(World world) {
		this.world = world;
	}

	@Override
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}

	@Override
	public void addListener(GameEventListener listener) {
		(this.field_36893 ? this.field_36892 : this.listeners).add(listener);
		DebugInfoSender.sendGameEventListener(this.world, listener);
	}

	@Override
	public void removeListener(GameEventListener listener) {
		if (this.field_36893) {
			this.field_36891.add(listener);
		} else {
			this.listeners.remove(listener);
		}
	}

	@Override
	public void dispatch(GameEvent event, @Nullable Entity entity, Vec3d pos) {
		boolean bl = false;
		this.field_36893 = true;

		try {
			Iterator<GameEventListener> iterator = this.listeners.iterator();

			while (iterator.hasNext()) {
				GameEventListener gameEventListener = (GameEventListener)iterator.next();
				if (this.field_36891.contains(gameEventListener)) {
					this.field_36891.remove(gameEventListener);
					iterator.remove();
				} else {
					bl |= dispatchTo(this.world, event, entity, pos, gameEventListener);
				}
			}
		} finally {
			this.field_36893 = false;
		}

		if (!this.field_36892.isEmpty()) {
			this.listeners.addAll(this.field_36892);
			this.field_36892.clear();
		}

		if (!this.field_36891.isEmpty()) {
			this.listeners.removeAll(this.field_36891);
			this.field_36891.clear();
		}

		if (bl) {
			DebugInfoSender.sendGameEvent(this.world, event, pos);
		}
	}

	private static boolean dispatchTo(World world, GameEvent event, @Nullable Entity sourceEntity, Vec3d pos, GameEventListener listener) {
		Optional<Vec3d> optional = listener.getPositionSource().getPos(world);
		if (optional.isEmpty()) {
			return false;
		} else {
			return ((Vec3d)optional.get()).squaredDistanceTo(pos) <= (double)(listener.getRange() * listener.getRange())
				? listener.listen(world, event, sourceEntity, pos)
				: false;
		}
	}
}
