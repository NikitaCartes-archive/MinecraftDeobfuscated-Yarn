package net.minecraft.data.server.tag.vanilla;

import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;

public class VanillaGameEventTagProvider extends TagProvider<GameEvent> {
	@VisibleForTesting
	static final List<RegistryKey<GameEvent>> BASIC_GAME_EVENTS = List.of(
		GameEvent.BLOCK_ATTACH.registryKey(),
		GameEvent.BLOCK_CHANGE.registryKey(),
		GameEvent.BLOCK_CLOSE.registryKey(),
		GameEvent.BLOCK_DESTROY.registryKey(),
		GameEvent.BLOCK_DETACH.registryKey(),
		GameEvent.BLOCK_OPEN.registryKey(),
		GameEvent.BLOCK_PLACE.registryKey(),
		GameEvent.BLOCK_ACTIVATE.registryKey(),
		GameEvent.BLOCK_DEACTIVATE.registryKey(),
		GameEvent.CONTAINER_CLOSE.registryKey(),
		GameEvent.CONTAINER_OPEN.registryKey(),
		GameEvent.DRINK.registryKey(),
		GameEvent.EAT.registryKey(),
		GameEvent.ELYTRA_GLIDE.registryKey(),
		GameEvent.ENTITY_DAMAGE.registryKey(),
		GameEvent.ENTITY_DIE.registryKey(),
		GameEvent.ENTITY_DISMOUNT.registryKey(),
		GameEvent.ENTITY_INTERACT.registryKey(),
		GameEvent.ENTITY_MOUNT.registryKey(),
		GameEvent.ENTITY_PLACE.registryKey(),
		GameEvent.ENTITY_ACTION.registryKey(),
		GameEvent.EQUIP.registryKey(),
		GameEvent.EXPLODE.registryKey(),
		GameEvent.FLUID_PICKUP.registryKey(),
		GameEvent.FLUID_PLACE.registryKey(),
		GameEvent.HIT_GROUND.registryKey(),
		GameEvent.INSTRUMENT_PLAY.registryKey(),
		GameEvent.ITEM_INTERACT_FINISH.registryKey(),
		GameEvent.LIGHTNING_STRIKE.registryKey(),
		GameEvent.NOTE_BLOCK_PLAY.registryKey(),
		GameEvent.PRIME_FUSE.registryKey(),
		GameEvent.PROJECTILE_LAND.registryKey(),
		GameEvent.PROJECTILE_SHOOT.registryKey(),
		GameEvent.SHEAR.registryKey(),
		GameEvent.SPLASH.registryKey(),
		GameEvent.STEP.registryKey(),
		GameEvent.SWIM.registryKey(),
		GameEvent.TELEPORT.registryKey(),
		GameEvent.UNEQUIP.registryKey()
	);

	public VanillaGameEventTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.GAME_EVENT, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(GameEventTags.VIBRATIONS).add(BASIC_GAME_EVENTS).add(Vibrations.RESONATIONS).add(GameEvent.FLAP.registryKey());
		this.getOrCreateTagBuilder(GameEventTags.SHRIEKER_CAN_LISTEN).add(GameEvent.SCULK_SENSOR_TENDRILS_CLICKING.registryKey());
		this.getOrCreateTagBuilder(GameEventTags.WARDEN_CAN_LISTEN)
			.add(BASIC_GAME_EVENTS)
			.add(Vibrations.RESONATIONS)
			.add(GameEvent.SHRIEK.registryKey())
			.addTag(GameEventTags.SHRIEKER_CAN_LISTEN);
		this.getOrCreateTagBuilder(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)
			.add(
				GameEvent.HIT_GROUND.registryKey(),
				GameEvent.PROJECTILE_SHOOT.registryKey(),
				GameEvent.STEP.registryKey(),
				GameEvent.SWIM.registryKey(),
				GameEvent.ITEM_INTERACT_START.registryKey(),
				GameEvent.ITEM_INTERACT_FINISH.registryKey()
			);
		this.getOrCreateTagBuilder(GameEventTags.ALLAY_CAN_LISTEN).add(GameEvent.NOTE_BLOCK_PLAY.registryKey());
	}
}
