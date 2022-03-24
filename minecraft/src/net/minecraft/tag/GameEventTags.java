package net.minecraft.tag;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEventTags {
	public static final TagKey<GameEvent> VIBRATIONS = of("vibrations");
	public static final TagKey<GameEvent> IGNORE_VIBRATIONS_SNEAKING = of("ignore_vibrations_sneaking");
	public static final TagKey<GameEvent> WARDEN_EVENTS_CAN_LISTEN = of("warden_events_can_listen");

	private static TagKey<GameEvent> of(String id) {
		return TagKey.of(Registry.GAME_EVENT_KEY, new Identifier(id));
	}
}
