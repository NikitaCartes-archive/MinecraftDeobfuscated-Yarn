/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import java.util.function.Consumer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

/**
 * A game event handler for an entity so that the listener stored can be
 * moved to the correct dispatcher or unregistered as the entity moves or
 * gets removed.
 * 
 * @apiNote This implementation is currently unused by vanilla as vanilla
 * doesn't have any entity that listens to game events.
 * 
 * @see net.minecraft.entity.Entity#getGameEventHandler()
 */
public class EntityGameEventHandler<T extends GameEventListener> {
    private T listener;
    @Nullable
    private ChunkSectionPos sectionPos;

    public EntityGameEventHandler(T listener) {
        this.listener = listener;
    }

    public void onEntitySetPosCallback(ServerWorld world) {
        this.onEntitySetPos(world);
    }

    public void setListener(T listener, @Nullable World world) {
        Object gameEventListener = this.listener;
        if (gameEventListener == listener) {
            return;
        }
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            EntityGameEventHandler.updateDispatcher(serverWorld, this.sectionPos, dispatcher -> dispatcher.removeListener((GameEventListener)gameEventListener));
            EntityGameEventHandler.updateDispatcher(serverWorld, this.sectionPos, dispatcher -> dispatcher.addListener((GameEventListener)listener));
        }
        this.listener = listener;
    }

    public T getListener() {
        return this.listener;
    }

    public void onEntityRemoval(ServerWorld world) {
        EntityGameEventHandler.updateDispatcher(world, this.sectionPos, dispatcher -> dispatcher.removeListener((GameEventListener)this.listener));
    }

    public void onEntitySetPos(ServerWorld world) {
        this.listener.getPositionSource().getPos(world).map(ChunkSectionPos::from).ifPresent(sectionPos -> {
            if (this.sectionPos == null || !this.sectionPos.equals(sectionPos)) {
                EntityGameEventHandler.updateDispatcher(world, this.sectionPos, dispatcher -> dispatcher.removeListener((GameEventListener)this.listener));
                this.sectionPos = sectionPos;
                EntityGameEventHandler.updateDispatcher(world, this.sectionPos, dispatcher -> dispatcher.addListener((GameEventListener)this.listener));
            }
        });
    }

    private static void updateDispatcher(WorldView world, @Nullable ChunkSectionPos sectionPos, Consumer<GameEventDispatcher> dispatcherConsumer) {
        if (sectionPos == null) {
            return;
        }
        Chunk chunk = world.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.FULL, false);
        if (chunk != null) {
            dispatcherConsumer.accept(chunk.getGameEventDispatcher(sectionPos.getSectionY()));
        }
    }
}

