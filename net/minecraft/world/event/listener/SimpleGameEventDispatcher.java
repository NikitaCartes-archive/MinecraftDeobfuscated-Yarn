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
import net.minecraft.entity.Entity;
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
    private final Set<GameEventListener> field_37673 = Sets.newHashSet();
    private final List<GameEventListener> field_37674 = Lists.newArrayList();
    private boolean field_37675;
    private final ServerWorld world;

    public SimpleGameEventDispatcher(ServerWorld serverWorld) {
        this.world = serverWorld;
    }

    @Override
    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    @Override
    public void addListener(GameEventListener listener) {
        if (this.field_37675) {
            this.field_37674.add(listener);
        } else {
            this.listeners.add(listener);
        }
        DebugInfoSender.sendGameEventListener(this.world, listener);
    }

    @Override
    public void removeListener(GameEventListener listener) {
        if (this.field_37675) {
            this.field_37673.add(listener);
        } else {
            this.listeners.remove(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispatch(GameEvent event, @Nullable Entity entity, Vec3d vec3d) {
        boolean bl = false;
        this.field_37675 = true;
        try {
            Iterator<GameEventListener> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                GameEventListener gameEventListener = iterator.next();
                if (this.field_37673.remove(gameEventListener)) {
                    iterator.remove();
                    continue;
                }
                if (!SimpleGameEventDispatcher.dispatchTo(this.world, event, entity, vec3d, gameEventListener)) continue;
                bl = true;
            }
        } finally {
            this.field_37675 = false;
        }
        if (!this.field_37674.isEmpty()) {
            this.listeners.addAll(this.field_37674);
            this.field_37674.clear();
        }
        if (!this.field_37673.isEmpty()) {
            this.listeners.removeAll(this.field_37673);
            this.field_37673.clear();
        }
        if (bl) {
            DebugInfoSender.sendGameEvent(this.world, event, vec3d);
        }
    }

    private static boolean dispatchTo(ServerWorld serverWorld, GameEvent gameEvent, @Nullable Entity entity, Vec3d vec3d, GameEventListener gameEventListener) {
        int i;
        Optional<Vec3d> optional = gameEventListener.getPositionSource().getPos(serverWorld);
        if (optional.isEmpty()) {
            return false;
        }
        double d = optional.get().squaredDistanceTo(vec3d);
        return d <= (double)(i = gameEventListener.getRange() * gameEventListener.getRange()) && gameEventListener.listen(serverWorld, gameEvent, entity, vec3d);
    }
}

