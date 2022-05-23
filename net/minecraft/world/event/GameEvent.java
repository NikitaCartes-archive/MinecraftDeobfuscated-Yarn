/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class GameEvent {
    public static final GameEvent BLOCK_ACTIVATE = GameEvent.register("block_activate");
    public static final GameEvent BLOCK_ATTACH = GameEvent.register("block_attach");
    public static final GameEvent BLOCK_CHANGE = GameEvent.register("block_change");
    public static final GameEvent BLOCK_CLOSE = GameEvent.register("block_close");
    public static final GameEvent BLOCK_DEACTIVATE = GameEvent.register("block_deactivate");
    public static final GameEvent BLOCK_DESTROY = GameEvent.register("block_destroy");
    public static final GameEvent BLOCK_DETACH = GameEvent.register("block_detach");
    public static final GameEvent BLOCK_OPEN = GameEvent.register("block_open");
    public static final GameEvent BLOCK_PLACE = GameEvent.register("block_place");
    public static final GameEvent CONTAINER_CLOSE = GameEvent.register("container_close");
    public static final GameEvent CONTAINER_OPEN = GameEvent.register("container_open");
    public static final GameEvent DISPENSE_FAIL = GameEvent.register("dispense_fail");
    public static final GameEvent DRINK = GameEvent.register("drink");
    public static final GameEvent EAT = GameEvent.register("eat");
    public static final GameEvent ELYTRA_GLIDE = GameEvent.register("elytra_glide");
    public static final GameEvent ENTITY_DAMAGE = GameEvent.register("entity_damage");
    public static final GameEvent ENTITY_DIE = GameEvent.register("entity_die");
    public static final GameEvent ENTITY_INTERACT = GameEvent.register("entity_interact");
    public static final GameEvent ENTITY_PLACE = GameEvent.register("entity_place");
    public static final GameEvent ENTITY_ROAR = GameEvent.register("entity_roar");
    public static final GameEvent ENTITY_SHAKE = GameEvent.register("entity_shake");
    public static final GameEvent EQUIP = GameEvent.register("equip");
    public static final GameEvent EXPLODE = GameEvent.register("explode");
    public static final GameEvent FLAP = GameEvent.register("flap");
    public static final GameEvent FLUID_PICKUP = GameEvent.register("fluid_pickup");
    public static final GameEvent FLUID_PLACE = GameEvent.register("fluid_place");
    public static final GameEvent HIT_GROUND = GameEvent.register("hit_ground");
    public static final GameEvent INSTRUMENT_PLAY = GameEvent.register("instrument_play");
    public static final GameEvent ITEM_INTERACT_FINISH = GameEvent.register("item_interact_finish");
    public static final GameEvent ITEM_INTERACT_START = GameEvent.register("item_interact_start");
    public static final GameEvent LIGHTNING_STRIKE = GameEvent.register("lightning_strike");
    public static final GameEvent NOTE_BLOCK_PLAY = GameEvent.register("note_block_play");
    public static final GameEvent PISTON_CONTRACT = GameEvent.register("piston_contract");
    public static final GameEvent PISTON_EXTEND = GameEvent.register("piston_extend");
    public static final GameEvent PRIME_FUSE = GameEvent.register("prime_fuse");
    public static final GameEvent PROJECTILE_LAND = GameEvent.register("projectile_land");
    public static final GameEvent PROJECTILE_SHOOT = GameEvent.register("projectile_shoot");
    public static final GameEvent SCULK_SENSOR_TENDRILS_CLICKING = GameEvent.register("sculk_sensor_tendrils_clicking");
    public static final GameEvent SHEAR = GameEvent.register("shear");
    public static final GameEvent SHRIEK = GameEvent.register("shriek", 32);
    public static final GameEvent SPLASH = GameEvent.register("splash");
    public static final GameEvent STEP = GameEvent.register("step");
    public static final GameEvent SWIM = GameEvent.register("swim");
    public static final GameEvent TELEPORT = GameEvent.register("teleport");
    public static final int DEFAULT_RANGE = 16;
    private final String id;
    private final int range;
    private final RegistryEntry.Reference<GameEvent> registryEntry = Registry.GAME_EVENT.createEntry(this);

    public GameEvent(String id, int range) {
        this.id = id;
        this.range = range;
    }

    public String getId() {
        return this.id;
    }

    public int getRange() {
        return this.range;
    }

    private static GameEvent register(String id) {
        return GameEvent.register(id, 16);
    }

    private static GameEvent register(String id, int range) {
        return Registry.register(Registry.GAME_EVENT, id, new GameEvent(id, range));
    }

    public String toString() {
        return "Game Event{ " + this.id + " , " + this.range + "}";
    }

    @Deprecated
    public RegistryEntry.Reference<GameEvent> getRegistryEntry() {
        return this.registryEntry;
    }

    public boolean isIn(TagKey<GameEvent> tag) {
        return this.registryEntry.isIn(tag);
    }

    public static final class Message
    implements Comparable<Message> {
        private final GameEvent event;
        private final Vec3d emitterPos;
        private final Emitter emitter;
        private final GameEventListener listener;
        private final double distanceTraveled;

        public Message(GameEvent event, Vec3d emitterPos, Emitter emitter, GameEventListener listener, Vec3d listenerPos) {
            this.event = event;
            this.emitterPos = emitterPos;
            this.emitter = emitter;
            this.listener = listener;
            this.distanceTraveled = emitterPos.squaredDistanceTo(listenerPos);
        }

        @Override
        public int compareTo(Message message) {
            return Double.compare(this.distanceTraveled, message.distanceTraveled);
        }

        public GameEvent getEvent() {
            return this.event;
        }

        public Vec3d getEmitterPos() {
            return this.emitterPos;
        }

        public Emitter getEmitter() {
            return this.emitter;
        }

        public GameEventListener getListener() {
            return this.listener;
        }

        @Override
        public /* synthetic */ int compareTo(Object other) {
            return this.compareTo((Message)other);
        }
    }

    public record Emitter(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
        public static Emitter of(@Nullable Entity sourceEntity) {
            return new Emitter(sourceEntity, null);
        }

        public static Emitter of(@Nullable BlockState affectedState) {
            return new Emitter(null, affectedState);
        }

        public static Emitter of(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
            return new Emitter(sourceEntity, affectedState);
        }

        @Nullable
        public Entity sourceEntity() {
            return this.sourceEntity;
        }

        @Nullable
        public BlockState affectedState() {
            return this.affectedState;
        }
    }
}

