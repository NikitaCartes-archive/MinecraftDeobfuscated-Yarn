package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.tag.GameEventTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEventTagProvider extends AbstractTagProvider<GameEvent> {
	private static final GameEvent[] field_38363 = new GameEvent[]{
		GameEvent.BLOCK_ATTACH,
		GameEvent.BLOCK_CHANGE,
		GameEvent.BLOCK_CLOSE,
		GameEvent.BLOCK_DESTROY,
		GameEvent.BLOCK_DETACH,
		GameEvent.BLOCK_OPEN,
		GameEvent.BLOCK_PLACE,
		GameEvent.BLOCK_PRESS,
		GameEvent.BLOCK_SWITCH,
		GameEvent.BLOCK_UNPRESS,
		GameEvent.BLOCK_UNSWITCH,
		GameEvent.CONTAINER_CLOSE,
		GameEvent.CONTAINER_OPEN,
		GameEvent.DISPENSE_FAIL,
		GameEvent.DRINKING_FINISH,
		GameEvent.EAT,
		GameEvent.ELYTRA_FREE_FALL,
		GameEvent.ENTITY_DAMAGED,
		GameEvent.ENTITY_KILLED,
		GameEvent.ENTITY_PLACE,
		GameEvent.EQUIP,
		GameEvent.EXPLODE,
		GameEvent.FISHING_ROD_CAST,
		GameEvent.FISHING_ROD_REEL_IN,
		GameEvent.FLUID_PICKUP,
		GameEvent.FLUID_PLACE,
		GameEvent.HIT_GROUND,
		GameEvent.LIGHTNING_STRIKE,
		GameEvent.MINECART_MOVING,
		GameEvent.MOB_INTERACT,
		GameEvent.PISTON_CONTRACT,
		GameEvent.PISTON_EXTEND,
		GameEvent.PRIME_FUSE,
		GameEvent.PROJECTILE_LAND,
		GameEvent.PROJECTILE_SHOOT,
		GameEvent.RAVAGER_ROAR,
		GameEvent.RING_BELL,
		GameEvent.SHEAR,
		GameEvent.SHULKER_CLOSE,
		GameEvent.SHULKER_OPEN,
		GameEvent.SPLASH,
		GameEvent.STEP,
		GameEvent.SWIM,
		GameEvent.WOLF_SHAKING
	};

	public GameEventTagProvider(DataGenerator root) {
		super(root, Registry.GAME_EVENT);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(GameEventTags.VIBRATIONS).add(field_38363).add(GameEvent.FLAP);
		this.getOrCreateTagBuilder(GameEventTags.WARDEN_EVENTS_CAN_LISTEN).add(field_38363).add(GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, GameEvent.SHRIEK);
		this.getOrCreateTagBuilder(GameEventTags.IGNORE_VIBRATIONS_SNEAKING).add(GameEvent.HIT_GROUND, GameEvent.PROJECTILE_SHOOT, GameEvent.STEP, GameEvent.SWIM);
	}
}
