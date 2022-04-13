/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

/**
 * A simple game event dispatcher implementation that has hooks to
 * debug info senders.
 * 
 * @apiNote Vanilla Minecraft creates it on a per-chunk-section basis.
 */
public class SimpleGameEventDispatcher
implements GameEventDispatcher {
    private final List<GameEventListener> listeners = Lists.newArrayList();
    private final Set<GameEventListener> toRemove = Sets.newHashSet();
    private final List<GameEventListener> toAdd = Lists.newArrayList();
    private boolean dispatching;
    private final ServerWorld world;

    public SimpleGameEventDispatcher(ServerWorld world) {
        this.world = world;
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
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispatch(GameEvent event, Vec3d pos, @Nullable GameEvent.Emitter emitter) {
        boolean bl = false;
        this.dispatching = true;
        try {
            Iterator<GameEventListener> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                GameEventListener gameEventListener = iterator.next();
                if (this.toRemove.remove(gameEventListener)) {
                    iterator.remove();
                    continue;
                }
                if (!SimpleGameEventDispatcher.dispatchTo(this.world, event, emitter, pos, gameEventListener)) continue;
                bl = true;
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
        if (bl) {
            DebugInfoSender.sendGameEvent(this.world, event, pos);
        }
    }

    private static boolean dispatchTo(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d pos, GameEventListener listener) {
        int i;
        Optional<Vec3d> optional = listener.getPositionSource().getPos(world);
        if (optional.isEmpty()) {
            return false;
        }
        double d = optional.get().squaredDistanceTo(pos);
        return d <= (double)(i = listener.getRange() * listener.getRange()) && listener.listen(world, event, emitter, pos);
    }
}

