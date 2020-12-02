package net.minecraft.world.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;

public class GameEvent {
	public static final GameEvent STEP = register("step");
	public static final GameEvent SWIM = register("swim");
	public static final GameEvent FLAP = register("flap");
	public static final GameEvent ELYTRA_FREE_FALL = register("elytra_free_fall");
	public static final GameEvent HIT_GROUND = register("hit_ground");
	public static final GameEvent SPLASH = register("splash");
	public static final GameEvent PROJECTILE_SHOOT = register("projectile_shoot");
	public static final GameEvent PROJECTILE_LAND = register("projectile_land");
	public static final GameEvent ENTITY_HIT = register("entity_hit");
	public static final GameEvent BLOCK_PLACE = register("block_place");
	public static final GameEvent BLOCK_DESTROY = register("block_destroy");
	public static final GameEvent FLUID_PLACE = register("fluid_place");
	public static final GameEvent FLUID_PICKUP = register("fluid_pickup");
	public static final GameEvent BLOCK_OPEN = register("block_open");
	public static final GameEvent BLOCK_CLOSE = register("block_close");
	public static final GameEvent BLOCK_SWITCH = register("block_switch");
	public static final GameEvent BLOCK_UNSWITCH = register("block_unswitch");
	public static final GameEvent BLOCK_ATTACH = register("block_attach");
	public static final GameEvent BLOCK_DETACH = register("block_detach");
	public static final GameEvent BLOCK_PRESS = register("block_press");
	public static final GameEvent BLOCK_UNPRESS = register("block_unpress");
	public static final GameEvent CONTAINER_OPEN = register("container_open");
	public static final GameEvent CONTAINER_CLOSE = register("container_close");
	public static final GameEvent EXPLODE = register("explode");
	public static final GameEvent ARMOR_STAND_ADD_ITEM = register("armor_stand_add_item");
	public static final GameEvent WOLF_SHAKING = register("wolf_shaking");
	public static final GameEvent DISPENSE_FAIL = register("dispense_fail");
	public static final GameEvent FISHING_ROD_CAST = register("fishing_rod_cast");
	public static final GameEvent FISHING_ROD_REEL_IN = register("fishing_rod_reel_in");
	public static final GameEvent PISTON_EXTEND = register("piston_extend");
	public static final GameEvent PISTON_CONTRACT = register("piston_contract");
	public static final GameEvent FLINT_AND_STEEL_USE = register("flint_and_steel_use");
	public static final GameEvent EATING_START = register("eating_start");
	public static final GameEvent EATING_FINISH = register("eating_finish");
	public static final GameEvent LIGHTNING_STRIKE = register("lightning_strike");
	private final String id;
	private final int range;

	public GameEvent(String id, int range) {
		this.id = id;
		this.range = range;
	}

	@Environment(EnvType.CLIENT)
	public String getId() {
		return this.id;
	}

	public int getRange() {
		return this.range;
	}

	private static GameEvent register(String id) {
		return register(id, 16);
	}

	private static GameEvent register(String id, int range) {
		return Registry.register(Registry.GAME_EVENT, id, new GameEvent(id, range));
	}
}
