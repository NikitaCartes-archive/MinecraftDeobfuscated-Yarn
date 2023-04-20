package net.minecraft.data.server.tag.vanilla;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;

public class VanillaGameEventTagProvider extends ValueLookupTagProvider<GameEvent> {
	@VisibleForTesting
	static final GameEvent[] BASIC_GAME_EVENTS = new GameEvent[]{
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
		GameEvent.DRINK,
		GameEvent.EAT,
		GameEvent.ELYTRA_GLIDE,
		GameEvent.ENTITY_DAMAGE,
		GameEvent.ENTITY_DIE,
		GameEvent.ENTITY_DISMOUNT,
		GameEvent.ENTITY_INTERACT,
		GameEvent.ENTITY_MOUNT,
		GameEvent.ENTITY_PLACE,
		GameEvent.ENTITY_ROAR,
		GameEvent.ENTITY_SHAKE,
		GameEvent.EQUIP,
		GameEvent.EXPLODE,
		GameEvent.FLUID_PICKUP,
		GameEvent.FLUID_PLACE,
		GameEvent.HIT_GROUND,
		GameEvent.INSTRUMENT_PLAY,
		GameEvent.ITEM_INTERACT_FINISH,
		GameEvent.LIGHTNING_STRIKE,
		GameEvent.NOTE_BLOCK_PLAY,
		GameEvent.PRIME_FUSE,
		GameEvent.PROJECTILE_LAND,
		GameEvent.PROJECTILE_SHOOT,
		GameEvent.SHEAR,
		GameEvent.SPLASH,
		GameEvent.STEP,
		GameEvent.SWIM,
		GameEvent.TELEPORT
	};

	public VanillaGameEventTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.GAME_EVENT, registryLookupFuture, gameEvent -> gameEvent.getRegistryEntry().registryKey());
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(GameEventTags.VIBRATIONS).add(BASIC_GAME_EVENTS).add(Vibrations.RESONATIONS).add(GameEvent.FLAP);
		this.getOrCreateTagBuilder(GameEventTags.SHRIEKER_CAN_LISTEN).add(GameEvent.SCULK_SENSOR_TENDRILS_CLICKING);
		this.getOrCreateTagBuilder(GameEventTags.WARDEN_CAN_LISTEN)
			.add(BASIC_GAME_EVENTS)
			.add(Vibrations.RESONATIONS)
			.add(GameEvent.SHRIEK)
			.addTag(GameEventTags.SHRIEKER_CAN_LISTEN);
		this.getOrCreateTagBuilder(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)
			.add(GameEvent.HIT_GROUND, GameEvent.PROJECTILE_SHOOT, GameEvent.STEP, GameEvent.SWIM, GameEvent.ITEM_INTERACT_START, GameEvent.ITEM_INTERACT_FINISH);
		this.getOrCreateTagBuilder(GameEventTags.ALLAY_CAN_LISTEN).add(GameEvent.NOTE_BLOCK_PLAY);
	}
}
