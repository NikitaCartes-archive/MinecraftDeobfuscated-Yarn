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
		GameEvent.BLOCK_ACTIVATE,
		GameEvent.BLOCK_DEACTIVATE,
		GameEvent.CONTAINER_CLOSE,
		GameEvent.CONTAINER_OPEN,
		GameEvent.DISPENSE_FAIL,
		GameEvent.DRINK,
		GameEvent.EAT,
		GameEvent.ELYTRA_GLIDE,
		GameEvent.ENTITY_DAMAGE,
		GameEvent.ENTITY_DIE,
		GameEvent.ENTITY_INTERACT,
		GameEvent.ENTITY_PLACE,
		GameEvent.ENTITY_ROAR,
		GameEvent.ENTITY_SHAKE,
		GameEvent.EQUIP,
		GameEvent.EXPLODE,
		GameEvent.FLAP,
		GameEvent.FLUID_PICKUP,
		GameEvent.FLUID_PLACE,
		GameEvent.HIT_GROUND,
		GameEvent.ITEM_INTERACT_FINISH,
		GameEvent.ITEM_INTERACT_START,
		GameEvent.LIGHTNING_STRIKE,
		GameEvent.NOTE_BLOCK_PLAY,
		GameEvent.PISTON_CONTRACT,
		GameEvent.PISTON_EXTEND,
		GameEvent.PRIME_FUSE,
		GameEvent.PROJECTILE_LAND,
		GameEvent.PROJECTILE_SHOOT,
		GameEvent.SHEAR,
		GameEvent.SPLASH,
		GameEvent.STEP,
		GameEvent.SWIM
	};

	public GameEventTagProvider(DataGenerator root) {
		super(root, Registry.GAME_EVENT);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(GameEventTags.VIBRATIONS).add(field_38363).add(GameEvent.FLAP);
		this.getOrCreateTagBuilder(GameEventTags.WARDEN_EVENTS_CAN_LISTEN).add(field_38363).add(GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, GameEvent.SHRIEK);
		this.getOrCreateTagBuilder(GameEventTags.IGNORE_VIBRATIONS_SNEAKING).add(GameEvent.HIT_GROUND, GameEvent.PROJECTILE_SHOOT, GameEvent.STEP, GameEvent.SWIM);
		this.getOrCreateTagBuilder(GameEventTags.IGNORE_VIBRATIONS_ON_OCCLUDING_BLOCK).add(GameEvent.HIT_GROUND, GameEvent.STEP);
	}
}
