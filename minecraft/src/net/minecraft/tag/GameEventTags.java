package net.minecraft.tag;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEventTags {
	public static final TagKey<GameEvent> VIBRATIONS = of("vibrations");
	public static final TagKey<GameEvent> WARDEN_CAN_LISTEN = of("warden_can_listen");
	public static final TagKey<GameEvent> SHRIEKER_CAN_LISTEN = of("shrieker_can_listen");
	public static final TagKey<GameEvent> IGNORE_VIBRATIONS_SNEAKING = of("ignore_vibrations_sneaking");
	public static final TagKey<GameEvent> DAMPENABLE_VIBRATIONS = of("dampenable_vibrations");

	private static TagKey<GameEvent> of(String id) {
		return TagKey.of(Registry.GAME_EVENT_KEY, new Identifier(id));
	}
}
