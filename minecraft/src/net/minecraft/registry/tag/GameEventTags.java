package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;

public class GameEventTags {
	public static final TagKey<GameEvent> VIBRATIONS = of("vibrations");
	public static final TagKey<GameEvent> WARDEN_CAN_LISTEN = of("warden_can_listen");
	public static final TagKey<GameEvent> SHRIEKER_CAN_LISTEN = of("shrieker_can_listen");
	public static final TagKey<GameEvent> IGNORE_VIBRATIONS_SNEAKING = of("ignore_vibrations_sneaking");
	public static final TagKey<GameEvent> ALLAY_CAN_LISTEN = of("allay_can_listen");

	private static TagKey<GameEvent> of(String id) {
		return TagKey.of(RegistryKeys.GAME_EVENT, new Identifier(id));
	}
}
