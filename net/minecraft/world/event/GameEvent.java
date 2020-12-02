/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;

public class GameEvent {
    public static final GameEvent STEP = GameEvent.register("step");
    public static final GameEvent SWIM = GameEvent.register("swim");
    public static final GameEvent FLAP = GameEvent.register("flap");
    public static final GameEvent ELYTRA_FREE_FALL = GameEvent.register("elytra_free_fall");
    public static final GameEvent HIT_GROUND = GameEvent.register("hit_ground");
    public static final GameEvent SPLASH = GameEvent.register("splash");
    public static final GameEvent PROJECTILE_SHOOT = GameEvent.register("projectile_shoot");
    public static final GameEvent PROJECTILE_LAND = GameEvent.register("projectile_land");
    public static final GameEvent ENTITY_HIT = GameEvent.register("entity_hit");
    public static final GameEvent BLOCK_PLACE = GameEvent.register("block_place");
    public static final GameEvent BLOCK_DESTROY = GameEvent.register("block_destroy");
    public static final GameEvent FLUID_PLACE = GameEvent.register("fluid_place");
    public static final GameEvent FLUID_PICKUP = GameEvent.register("fluid_pickup");
    public static final GameEvent BLOCK_OPEN = GameEvent.register("block_open");
    public static final GameEvent BLOCK_CLOSE = GameEvent.register("block_close");
    public static final GameEvent BLOCK_SWITCH = GameEvent.register("block_switch");
    public static final GameEvent BLOCK_UNSWITCH = GameEvent.register("block_unswitch");
    public static final GameEvent BLOCK_ATTACH = GameEvent.register("block_attach");
    public static final GameEvent BLOCK_DETACH = GameEvent.register("block_detach");
    public static final GameEvent BLOCK_PRESS = GameEvent.register("block_press");
    public static final GameEvent BLOCK_UNPRESS = GameEvent.register("block_unpress");
    public static final GameEvent CONTAINER_OPEN = GameEvent.register("container_open");
    public static final GameEvent CONTAINER_CLOSE = GameEvent.register("container_close");
    public static final GameEvent EXPLODE = GameEvent.register("explode");
    public static final GameEvent ARMOR_STAND_ADD_ITEM = GameEvent.register("armor_stand_add_item");
    public static final GameEvent WOLF_SHAKING = GameEvent.register("wolf_shaking");
    public static final GameEvent DISPENSE_FAIL = GameEvent.register("dispense_fail");
    public static final GameEvent FISHING_ROD_CAST = GameEvent.register("fishing_rod_cast");
    public static final GameEvent FISHING_ROD_REEL_IN = GameEvent.register("fishing_rod_reel_in");
    public static final GameEvent PISTON_EXTEND = GameEvent.register("piston_extend");
    public static final GameEvent PISTON_CONTRACT = GameEvent.register("piston_contract");
    public static final GameEvent FLINT_AND_STEEL_USE = GameEvent.register("flint_and_steel_use");
    public static final GameEvent EATING_START = GameEvent.register("eating_start");
    public static final GameEvent EATING_FINISH = GameEvent.register("eating_finish");
    public static final GameEvent LIGHTNING_STRIKE = GameEvent.register("lightning_strike");
    private final String id;
    private final int range;

    public GameEvent(String id, int range) {
        this.id = id;
        this.range = range;
    }

    @Environment(value=EnvType.CLIENT)
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
}

